package org.learning.future;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

// ## Задача 3
public class LogCounter {

    static final int THREADS = 4;

    static String typeOf(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String trimmed = message.strip();
        int space = trimmed.indexOf(' ');
        return space < 0 ? trimmed : trimmed.substring(0, space);
    }

    static <T> List<T> runAll(List<Callable<T>> tasks, int threads) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<T> results = new ArrayList<>();
            for (Future<T> future : executor.invokeAll(tasks)) {
                results.add(future.get());
            }
            return results;
        } finally {
            executor.shutdown();
        }
    }

    // ConcurrentHashMap + merge
    static Map<String, Integer> countWithMerge(List<String> messages, int threads) throws Exception {
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (String message : messages) {
            tasks.add(() -> {
                String type = typeOf(message);
                if (type != null) {
                    counts.merge(type, 1, Integer::sum); // атомарно прочитать, прибавить, записать
                }
                return null;
            });
        }
        runAll(tasks, threads);
        return counts;
    }

    // get() + put() - не потокобезопасно
    static Map<String, Integer> countWithGetPut(List<String> messages, int threads) throws Exception {
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (String message : messages) {
            tasks.add(() -> {
                String type = typeOf(message);
                if (type != null) {
                    Integer current = counts.get(type);
                    counts.put(type, current == null ? 1 : current + 1);
                }
                return null;
            });
        }
        runAll(tasks, threads);
        return counts;
    }

    // AtomicInteger
    static Map<String, Integer> countWithAtomic(List<String> messages, int threads) throws Exception {
        Map<String, AtomicInteger> counts = new ConcurrentHashMap<>();

        List<Callable<Void>> tasks = new ArrayList<>();
        for (String message : messages) {
            tasks.add(() -> {
                String type = typeOf(message);
                if (type != null) {
                    counts.computeIfAbsent(type, k -> new AtomicInteger()).incrementAndGet();
                }
                return null;
            });
        }
        runAll(tasks, threads);

        Map<String, Integer> result = new HashMap<>();
        counts.forEach((type, counter) -> result.put(type, counter.get()));
        return result;
    }

    // каждый worker считает свою часть, затем агрегация
    static Map<String, Integer> countByChunks(List<String> messages, int threads) throws Exception {
        int chunkSize = (messages.size() + threads - 1) / threads; // округление вверх

        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();
        for (int from = 0; from < messages.size(); from += chunkSize) {
            List<String> chunk = messages.subList(from, Math.min(from + chunkSize, messages.size()));
            tasks.add(() -> {
                // локальная обычная HashMap: её видит только этот поток, синхронизация не нужна
                Map<String, Integer> local = new HashMap<>();
                for (String message : chunk) {
                    String type = typeOf(message);
                    if (type != null) {
                        local.merge(type, 1, Integer::sum);
                    }
                }
                return local;
            });
        }
        // агрегация в одном потоке (вызывающем), после завершения всех воркеров
        Map<String, Integer> total = new HashMap<>();
        for (Map<String, Integer> partial : runAll(tasks, threads)) {
            partial.forEach((type, count) -> total.merge(type, count, Integer::sum));
        }
        return total;
    }

    // проверка
    static List<String> generateMessages(int info, int error, int warn) {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < info; i++) messages.add(i % 2 == 0 ? "INFO user logged in" : "INFO user logged out");
        for (int i = 0; i < error; i++) messages.add(i % 2 == 0 ? "ERROR database connection" : "ERROR timeout");
        for (int i = 0; i < warn; i++) messages.add("WARN disk usage high");
        Collections.shuffle(messages, new Random(42));
        return messages;
    }

    public static void main(String[] args) throws Exception {
        List<String> messages = generateMessages(5000, 3000, 2000);
        Map<String, Integer> expected = Map.of("INFO", 5000, "ERROR", 3000, "WARN", 2000);

        report("merge", countWithMerge(messages, THREADS), expected);
        report("AtomicInteger", countWithAtomic(messages, THREADS), expected);
        report("по частям", countByChunks(messages, THREADS), expected);
        report("get+put (ошибочный)", countWithGetPut(messages, THREADS), expected);

        // null и пустые строки не должны ронять обработку
        List<String> dirty = new ArrayList<>(List.of("INFO ok", "  ", "ERROR boom"));
        dirty.add(null);
        report("с мусором во входе", countWithMerge(dirty, THREADS), Map.of("INFO", 1, "ERROR", 1));
    }

    private static void report(String name, Map<String, Integer> actual, Map<String, Integer> expected) {
        boolean ok = Objects.equals(new TreeMap<>(actual), new TreeMap<>(expected));
        System.out.println(name + ": " + new TreeMap<>(actual) + (ok ? "  OK" : "  НЕ СОВПАДАЕТ с ожидаемым"));
    }
}
