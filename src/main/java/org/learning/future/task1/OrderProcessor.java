package org.learning.future.task1;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class OrderProcessor {

    public List<OrderResult> process(List<Order> orders) throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(
                OrderProcessorUtil.computeThreadCount()
        );

        try {
            var futures = new ArrayList<Future<OrderResult>>();
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(Task1Constants.TOTAL_PROCESS_TIMEOUT_IN_SECONDS);

            for (Order order : orders) {
                futures.add(executorService.submit(() -> processOrder(order)));
            }

            var results = new ArrayList<OrderResult>();

            for (Future<OrderResult> future : futures) {
                long remaining = deadline - System.nanoTime();

                if (remaining <= 0) {
                    if (future.isDone()) {
                        try {
                            results.add(future.get());
                        } catch (ExecutionException e) {
                            System.out.println(String.format(Task1Constants.ORDER_FAILED_PATTERN_EXCEPTION, e.getCause()));
                        }
                    }
                    future.cancel(true);
                    continue;
                }
                try {
                    results.add(future.get(remaining, TimeUnit.NANOSECONDS));
                }
                catch (ExecutionException e) {
                    System.out.println(String.format(Task1Constants.ORDER_FAILED_PATTERN_EXCEPTION, e.getCause()));
                }
                catch (InterruptedException e) {
                    System.out.println(Task1Constants.INTERRUPT_WHILE_WAITING_EXCEPTION);
                    Thread.currentThread().interrupt();

                    throw e;
                }
                catch (TimeoutException e) {
                    System.out.println(Task1Constants.ORDER_TIMED_OUT_EXCEPTION);
                    future.cancel(true);
                }
            }

            var notComputedOrders = OrderProcessorUtil.getUnprocessedOrders(orders, results);
            results.addAll(OrderProcessorUtil.mapOrdersToFailedOrderResults(notComputedOrders));

            return results;
        } finally {
            executorService.shutdown();

            if (!executorService.awaitTermination(Task1Constants.AWAIT_TERMINATION_IN_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }

    }

    private OrderResult processOrder(Order order) throws InterruptedException {
        var result = OrderResultMapper.generateNewOrderResultFromOrder(order);

        // Ну оочень долгая транзакция
        Thread.sleep(Task1Constants.COMPUTE_TIME_IN_MILLIS);

        result.setStatus(Status.DONE);
        return result;
    }

}
