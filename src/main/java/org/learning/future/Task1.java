package org.learning.future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Task1 {
    public static void main(String[] args) {
        int poolSize = Runtime.getRuntime().availableProcessors() * 2; // I/O-bound

        OrderService service = new OrderService(poolSize);

        List<Integer> orderIds = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            orderIds.add(i);
        }

        // Запускаем обработку с суммарным таймаутом например 5 секунд на всё
        List<OrderService.ProcessedOrder> successfulResults =
                service.processOrders(orderIds, 5, TimeUnit.SECONDS);

        service.shutdown();

        System.out.printf("%nИтоги обработки:%n");
        System.out.printf("Успешно: %d из %d%n", successfulResults.size(), orderIds.size());
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
