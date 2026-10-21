package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// ## Задача 1
public class OrderProcessor {

    record Order(int id, int clientId, int quantity, double price) {
    }

    record OrderResult(int orderId, String clientName, double total) {
    }

    record OrderFailure(int orderId, FailureReason reason, Throwable cause) {
    }

    record ProcessingResult(List<OrderResult> succeeded, List<OrderFailure> failed) {
    }

    record Submitted(Order order, Future<OrderResult> future) {
    }

    static class ProcessingInterruptedException extends RuntimeException {
        ProcessingInterruptedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class QueryTimeoutException extends RuntimeException {
        QueryTimeoutException(String message) {
            super(message);
        }
    }

    static final long QUERY_TIMEOUT_MS = 2_000;

    enum FailureReason {
        ERROR,
        TIMEOUT,
        CANCELLED,
        INTERRUPTED
    }

    static OrderResult process(Order order) {
        try {
            // заказ 42 "зависает", каждый 25-й падает с ошибкой
            long delayMs = order.id() == 42 ? 3_000 : 100;
            Thread.sleep(Math.min(delayMs, QUERY_TIMEOUT_MS));
            if (delayMs > QUERY_TIMEOUT_MS) {
                throw new QueryTimeoutException("запрос по заказу " + order.id() + " превысил" + QUERY_TIMEOUT_MS + " мс");
            }

            if (order.id() % 25 == 0) {
                throw new IllegalStateException("Клиент не найден для заказа " + order.id());
            }

            String clientName = "Client-" + order.clientId();
            double total = order.quantity() * order.price();
            return new OrderResult(order.id(), clientName, total);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessingInterruptedException("Обработка заказа " + order.id() + " прервана", e);
        }
    }

    public static ProcessingResult processAll(List<Order> orders, int poolSize, long timeout, TimeUnit unit) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<OrderResult> results = new ArrayList<>();
        List<OrderFailure> failed = new ArrayList<>();

        try {
            List<Submitted> submitted = new ArrayList<>();
            for (Order order : orders) {
                submitted.add(new Submitted(order, executor.submit(() -> process(order))));
            }

            for (int i = 0; i < submitted.size(); i++) {
                Submitted s = submitted.get(i);
                try {
                    results.add(s.future().get(timeout, unit));
                } catch (ExecutionException e) {
                    failed.add(new OrderFailure(s.order().id(), FailureReason.ERROR, e));
                } catch (CancellationException e) {
                    failed.add(new OrderFailure(s.order().id(), FailureReason.CANCELLED, e));
                } catch (TimeoutException e) {
                    s.future().cancel(true);
                    failed.add(new OrderFailure(s.order().id(), FailureReason.TIMEOUT, e));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    for (int j = i; j < submitted.size(); j++) {
                        Submitted rest = submitted.get(j);
                        rest.future().cancel(true);
                        failed.add(new OrderFailure(rest.order().id(), FailureReason.INTERRUPTED, e));
                    }
                    break;
                }
            }
            return new ProcessingResult(results, failed);
        } finally {
            shutdownGracefully(executor);
        }
    }

    private static void shutdownGracefully(ExecutorService executor) {
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

    public static void main(String[] args) {
        List<Order> orders = IntStream.rangeClosed(1, 100)
            .mapToObj(i -> new Order(i, i % 10, i % 5 + 1, 9.99))
            .toList();

        long start = System.currentTimeMillis();
        ProcessingResult result = processAll(orders, 10, 1, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Количество ядер: " + Runtime.getRuntime().availableProcessors());

        System.out.println("Обработано успешно: " + result.succeeded().size() + " из " + orders.size());
        System.out.println("Первые 3: " + result.succeeded().subList(0, 3));

        Map<FailureReason, Long> byReason = result.failed().stream()
            .collect(Collectors.groupingBy(OrderFailure::reason, TreeMap::new, Collectors.counting()));

        System.out.println("Неуспешных: " + result.failed().size() + " " + byReason);
        result.failed().forEach(f -> System.out.println("  заказ " + f.orderId() + " -> " + f.reason()
            + (f.cause() == null ? "" : ": " + f.cause())));
        System.out.println("Время: " + elapsed + " мс");
    }
}
