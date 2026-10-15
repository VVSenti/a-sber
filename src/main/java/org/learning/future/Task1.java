package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Task1 {
    public record ProcessedOrder(int orderId, double totalAmount, String status){}

    public static void main(String[] args) {
        List<Integer> orderIds = new ArrayList<>();
        for(int i=1;i<=100;i++) {
            orderIds.add(i);
        }

        int poolSize = 10;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        List<Future<ProcessedOrder>> futures = new ArrayList<>();

        for(int orderId:orderIds) {
            Callable<ProcessedOrder> task = ()-> processedOrder(orderId);
            Future<ProcessedOrder> future = executor.submit(task);
            futures.add(future);
        }

        List<ProcessedOrder> results = new ArrayList<>();
        for(Future<ProcessedOrder> future:futures) {
            try {
                ProcessedOrder processedOrder = future.get(2, TimeUnit.SECONDS);
                results.add(processedOrder);
            }
            catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Поток сбора результатов был прерван: "+e.getMessage());
                break;
            }
            catch(ExecutionException e) {
                System.err.println("Ошибка при обработке заказа: "+e.getCause().getMessage());
            }
            catch(TimeoutException e) {
                System.err.println("Таймаут ожидания задачи: задача отменяется");
                future.cancel(true);
            }
        }

        shutdownAndAwaitTermination(executor);
        System.out.printf("Успешно обработано заказов: %d из %d%n", results.size(), orderIds.size());
    }

    private static ProcessedOrder processedOrder(int orderId) throws InterruptedException {
        Thread.sleep(100);
        double calculatePrice = orderId * 15.5;
        return new ProcessedOrder(orderId, calculatePrice,"SUCCESS");
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if(!pool.awaitTermination(60,TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if(!pool.awaitTermination(60,TimeUnit.SECONDS)) {
                    System.err.println("Пул не завершился штатно");
                }
            }
        }
        catch(InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
