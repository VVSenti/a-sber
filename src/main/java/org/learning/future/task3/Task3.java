package org.learning.future.task3;

// Задача 3.
// Есть список строк, содержащих сообщения:
//
//"ERROR database connection" "INFO user logged in" "ERROR timeout" "INFO user logged out" ...
//
// Нужно параллельно обработать сообщения и посчитать количество сообщений каждого типа (INFO, ERROR, WARN).
//
// Использовать ExecutorService и ConcurrentHashMap.
//
// Например, ожидаемый результат: INFO -> 5000 ERROR -> 3000 WARN -> 2000
//
// Важно: нельзя использовать обычный HashMap с несколькими потоками.
//
// Попросить кандидата реализовать, например: map.merge(type, 1, Integer::sum);
//
// Что проверить:
//
// зачем здесь ConcurrentHashMap; чем ConcurrentHashMap отличается от Collections.synchronizedMap(); насколько потокобезопасна конструкция get() + put(); почему merge() подходит для данной задачи; можно ли использовать AtomicInteger в качестве значения map.
//
// Усложнение: сделать так, чтобы каждый worker обрабатывал свою часть списка, а затем результаты агрегировались.

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Task3 {

    private static final String ERROR_MESSAGE = "ERROR database connection";
    private static final String INFO_MESSAGE = "INFO user logged in";
    private static final String WARN_MESSAGE = "WARN timeout";

    private static final int THREAD_COUNT = 10;

    private static final int ERROR_MESSAGE_COUNT = 3000;
    private static final int INFO_MESSAGE_COUNT = 2400;
    private static final int WARN_MESSAGE_COUNT = 4000;

    public static void main(String[] args) throws InterruptedException {

        var executor = Executors.newFixedThreadPool(THREAD_COUNT);

        var list = prepareData();
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            int finalI = i;
            executor.submit(() -> {
                for (int k = finalI; k < list.size(); k += THREAD_COUNT) {
                    map.merge(list.get(k),1, Integer::sum);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.printf("Key - %s, count - %s%n", entry.getKey(), entry.getValue());
        }
    }

    private static List<String> prepareData() {
        List<String> list = new ArrayList<>();

        list.addAll(generateErrorMessage());
        list.addAll(generateInfoMessage());
        list.addAll(generateWarnMessage());

        return list;
    }

    private static List<String> generateInfoMessage() {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < INFO_MESSAGE_COUNT; i++) result.add(INFO_MESSAGE);

        return result;
    }

    private static List<String> generateWarnMessage() {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < WARN_MESSAGE_COUNT; i++) {
            result.add(WARN_MESSAGE);
        }

        return result;
    }

    private static List<String> generateErrorMessage() {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < ERROR_MESSAGE_COUNT; i++) {
            result.add(ERROR_MESSAGE);
        }

        return result;
    }

}

