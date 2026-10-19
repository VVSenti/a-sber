package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

// ## Задача 1
public class OrderProcessor {

    record Order(int id, int clientId, int quantity, double price) {
    }

    record OrderResult(int orderId, String clientName, double total) {
    }

    static OrderResult process(Order order) throws InterruptedException {
        // заказ 42 "зависает", каждый 25-й падает с ошибкой
        long delayMs = order.id() == 42 ? 3_000 : 100;
        Thread.sleep(delayMs);

        if (order.id() % 25 == 0) {
            throw new IllegalStateException("Клиент не найден для заказа " + order.id());
        }

        String clientName = "Client-" + order.clientId();
        double total = order.quantity() * order.price();
        return new OrderResult(order.id(), clientName, total);
    }

    public static List<OrderResult> processAll(List<Order> orders, int poolSize, long timeout, TimeUnit unit) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<OrderResult> results = new ArrayList<>();

        try {
            List<Future<OrderResult>> futures = new ArrayList<>();
            for (Order order : orders) {
                futures.add(executor.submit(() -> process(order)));
            }

            for (Future<OrderResult> future : futures) {
                try {
                    results.add(future.get(timeout, unit));
                } catch (ExecutionException e) {
                    System.err.println("Ошибка в задаче: " + e.getCause());
                } catch (TimeoutException e) {
                    System.err.println("Таймаут, отменяем задачу");
                    future.cancel(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    futures.forEach(f -> f.cancel(true));
                    break;
                }
            }
        } finally {
            shutdownGracefully(executor);
        }
        return results;
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

        System.out.println("Количество ядер: " + Runtime.getRuntime().availableProcessors());

        List<OrderResult> results = processAll(orders, 10, 1, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Обработано успешно: " + results.size() + " из " + orders.size());
        System.out.println("Первые 3: " + results.subList(0, 3));
        System.out.println("Время: " + elapsed + " мс");
    }
}
