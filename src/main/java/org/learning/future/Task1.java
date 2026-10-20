package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Task1 {
    public record ProcessedOrder(int orderId, double totalAmount, String status) {}
    public record OrderSubmission(int orderId, Future<ProcessedOrder> future) {}

    public static void main(String[] args) {

        //Узнаем количество доступных ядер процессора
        int cores = Runtime.getRuntime().availableProcessors();

        //Так как processOrder содержит Thread.sleep это I/O-bound задача,
        //потоки будут простаивать в ожидании. Поэтому берем потоков больше, чем ядер.
        int poolSize = cores * 2;

        List<Integer> orderIds = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            orderIds.add(i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<OrderSubmission> submissions = new ArrayList<>();

        for (int orderId : orderIds) {
            Future<ProcessedOrder> future = executor.submit(() -> processOrder(orderId));
            submissions.add(new OrderSubmission(orderId, future));
        }

        List<ProcessedOrder> successfulResults = new ArrayList<>();
        List<Integer> failedOrderIds = new ArrayList<>();

        for (OrderSubmission submission : submissions) {
            int orderId = submission.orderId();
            Future<ProcessedOrder> future = submission.future();

            try {
                ProcessedOrder processedOrder = future.get(2, TimeUnit.SECONDS);
                successfulResults.add(processedOrder);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Поток сбора результатов был прерван. Немедленная остановка пула.");
                executor.shutdownNow();
                failedOrderIds.add(orderId);
                break;
            } catch (ExecutionException e) {
                System.err.printf("Ошибка при обработке заказа %d: %s%n", orderId, e.getCause().getMessage());
                failedOrderIds.add(orderId);
            } catch (TimeoutException e) {
                System.err.printf("Таймаут ожидания заказа %d: отменяем задачу%n", orderId);
                future.cancel(true);
                failedOrderIds.add(orderId);
            } catch (CancellationException e) {
                // Ловим отмену задачи
                System.err.printf("Заказ %d был отменен%n", orderId);
                failedOrderIds.add(orderId);
            }
        }

        shutdownAndAwaitTermination(executor);

        System.out.printf("%nИтоги обработки:%n");
        System.out.printf("Успешно: %d из %d%n", successfulResults.size(), orderIds.size());
        System.out.printf("Неуспешно: %d (ID заказов: %s)%n", failedOrderIds.size(), failedOrderIds);
    }

    private static ProcessedOrder processOrder(int orderId) {
        try {
            Thread.sleep(100);
            double calculatePrice = orderId * 15.5;
            return new ProcessedOrder(orderId, calculatePrice, "SUCCESS");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Обработка заказа " + orderId + " прервана", e);
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Пул не завершился штатно");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
//понял свою ошибку
//Изначально я взял availableProcessors, потому что это количество ядер. Это оптимально только для CPU-bound задач
// например математические вычисления. В таком случае мы не создаем лишних потоков, чтобы избежать переключения контекста,
// когда потоки толкаются за процессор.
//Однако в моем коде метод processOrder содержит Thread.sleep(100).
// Это делает задачу I/O-bound, тоесть пока поток спит, ядро процессора простаивает.
// Если мы создадим потоков ровно по количеству ядер, то во время ожидания пул просто встанет,
// и остальные задачи из очереди не будут обрабатываться.
//По формуле Брайана Гетца, для I/O-bound задач потоков нужно больше чем ядер, обычно N * 2 или больше, в зависимости от времени ожидания.
// Поэтому исправил код и взял availableProcessors * 2.
