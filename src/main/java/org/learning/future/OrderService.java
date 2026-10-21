package org.learning.future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class OrderService {

    public record ProcessedOrder(int orderId, double totalAmount, String status) {}
    public record OrderSubmission(int orderId, Future<ProcessedOrder> future) {}

    private final ExecutorService executor;

    public OrderService(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public List<ProcessedOrder> processOrders(List<Integer> orderIds, long totalTimeout, TimeUnit unit) {
        List<OrderSubmission> submissions = new ArrayList<>();

        for (int orderId : orderIds) {
            Future<ProcessedOrder> future = executor.submit(() -> processOrder(orderId));
            submissions.add(new OrderSubmission(orderId, future));
        }

        List<ProcessedOrder> successfulResults = new ArrayList<>();

        long totalTimeoutNanos = unit.toNanos(totalTimeout);
        long startTime = System.nanoTime();

        for (OrderSubmission submission : submissions) {
            int orderId = submission.orderId();
            Future<ProcessedOrder> future = submission.future();

            long elapsedNanos = System.nanoTime() - startTime;
            long remainingNanos = totalTimeoutNanos - elapsedNanos;

            if (remainingNanos <= 0) {
                System.err.printf("Общий таймаут истек. Отменяем заказ %d%n", orderId);
                future.cancel(true);
                continue;
            }

            try {
                ProcessedOrder processedOrder = future.get(remainingNanos, TimeUnit.NANOSECONDS);
                successfulResults.add(processedOrder);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Поток был прерван.");
                break;
            } catch (ExecutionException e) {
                System.err.printf("Ошибка заказа %d: %s%n", orderId, e.getCause().getMessage());
            } catch (TimeoutException e) {
                System.err.printf("Заказ %d не успел выполниться до конца общего таймаута%n", orderId);
                future.cancel(true);
            } catch (CancellationException e) {
                System.err.printf("Заказ %d отменен%n", orderId);
            }
        }

        return successfulResults;
    }

    private ProcessedOrder processOrder(int orderId) {
        try {
            Thread.sleep(100);
            double calculatePrice = orderId * 15.5;
            return new ProcessedOrder(orderId, calculatePrice, "SUCCESS");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Обработка заказа " + orderId + " прервана", e);
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
