package org.learning.future.task1;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class OrderProcessor {

    public List<OrderResult> process(List<Order> orders) {
        var executorService = Executors.newFixedThreadPool(
                OrderProcessorUtil.computeThreadCount()
        );

        try {
            List<Callable<OrderResult>> tasks = orders.stream()
                    .<Callable<OrderResult>>map(order -> () -> processOrder(order))
                    .toList();
            List<Future<OrderResult>> futures;
            try {
                futures = executorService.invokeAll(
                        tasks,
                        Task1Constants.TOTAL_PROCESS_TIMEOUT_IN_SECONDS,
                        TimeUnit.SECONDS
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return OrderProcessorUtil.mapOrdersToFailedOrderResults(orders);
            }

            var results = new ArrayList<OrderResult>();

            for (Future<OrderResult> future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException e) {
                    System.out.println(String.format(
                            Task1Constants.ORDER_FAILED_PATTERN_EXCEPTION, e.getCause()));
                } catch (InterruptedException e) {
                    System.out.println(Task1Constants.INTERRUPT_WHILE_WAITING_EXCEPTION);
                    Thread.currentThread().interrupt();
                    break;
                } catch (CancellationException e) {
                    System.out.println(Task1Constants.ORDER_TIMED_OUT_EXCEPTION);
                }
            }

            var notComputedOrders = OrderProcessorUtil.getUnprocessedOrders(orders, results);
            results.addAll(OrderProcessorUtil.mapOrdersToFailedOrderResults(notComputedOrders));

            return results;
        } finally {
            executorService.shutdown();

            try {
                if (!executorService.awaitTermination(Task1Constants.AWAIT_TERMINATION_IN_SECONDS, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

    }

    private OrderResult processOrder(Order order) throws InterruptedException {
        var result = OrderResultMapper.generateNewOrderResultFromOrder(order);

        Thread.sleep(Task1Constants.COMPUTE_TIME_IN_MILLIS);

        result.setStatus(Status.DONE);
        return result;
    }
}
