package org.learning.future.task1;

//Задача 1
// Есть список из 100 заказов. Для каждого заказа нужно выполнить условно «долгую» операцию —
// например, получить информацию о клиенте из БД и рассчитать стоимость заказа.
//
// Необходимо:
//
// создать ExecutorService с фиксированным количеством потоков; отправить задачи на выполнение через submit();
// получить результаты через Future; собрать результаты в общий список; корректно завершить ExecutorService;
// обработать InterruptedException и ExecutionException.
//
// Что обсудить с кандидатом:
//
// почему здесь лучше submit(), а не execute(); что произойдет, если вызвать future.get() сразу после каждого submit();
// как подобрать размер пула; что произойдет при shutdown() / shutdownNow(); как не потерять interrupt-флаг.
//
// Усложнение: добавить timeout при получении результата через future.get(timeout, unit).

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task1 {

    public static void main(String[] args) throws InterruptedException {
        var processor = new OrderProcessor();
        var orders = generateRandomOrders(50000);

        var results = processor.process(orders);

        results.stream()
                .map(o -> String.format(
                        "Order: %s. Status: %s",
                        o.getOrder().getNumber(),
                        o.getStatus().name()))
                .forEach(System.out::println);
    }

    private static List<Order> generateRandomOrders(int orderCount) {
        List<Order> result = new ArrayList<>();

        for (int i = 0; i < orderCount; i++) {
            result.add(generateRandomOrder());
        }

        return result;
    }

    private static Order generateRandomOrder() {
        return Order
                .builder()
                .number(UUID.randomUUID())
                .build();
    }

}
