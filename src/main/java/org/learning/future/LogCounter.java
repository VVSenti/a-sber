package org.learning.future;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// ## Задача 3
public class LogCounter {

    static final int THREADS = 4;
    static final int QUEUE_CAPACITY = 100;

    static String typeOf(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String trimmed = message.strip();
        int space = trimmed.indexOf(' ');
        return space < 0 ? trimmed : trimmed.substring(0, space);
    }

    // Пул с ограниченной очередью вместо Executors.newFixedThreadPool
    static ExecutorService newBoundedPool(int threads, int queueCapacity) {
        AtomicInteger counter = new AtomicInteger();
        ThreadFactory namedThreads = runnable -> new Thread(runnable, "log-worker-" + counter.incrementAndGet());
        return new ThreadPoolExecutor(
            threads, threads,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            namedThreads,
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    // делит список на parts примерно равных частей
    static <T> List<List<T>> partition(List<T> list, int parts) {
        int chunkSize = (list.size() + parts - 1) / parts;
        List<List<T>> chunks = new ArrayList<>();
        for (int from = 0; from < list.size(); from += chunkSize) {
            chunks.add(list.subList(from, Math.min(from + chunkSize, list.size())));
        }
        return chunks;
    }

    static <T> List<T> runAll(List<Callable<T>> tasks, int threads) throws Exception {
        ExecutorService executor = newBoundedPool(threads, QUEUE_CAPACITY);
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
        for (int t = 0; t < threads; t++) {
            int start = t;
            tasks.add(() -> {
                for (int i = start; i < messages.size(); i += threads) {
                    String message = messages.get(i);
                    String type = typeOf(message);
                    if (type != null) {
                        counts.merge(type, 1, Integer::sum);
                    }
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
        for (int t = 0; t < threads; t++) {
            int start = t;
            tasks.add(() -> {
                for (int i = start; i < messages.size(); i += threads) {
                    String message = messages.get(i);
                    String type = typeOf(message);
                    if (type != null) {
                        Integer current = counts.get(type);
                        counts.put(type, current == null ? 1 : current + 1);
                    }
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
        for (int t = 0; t < threads; t++) {
            int start = t;
            tasks.add(() -> {
                for (int i = start; i < messages.size(); i += threads) {
                    String message = messages.get(i);
                    String type = typeOf(message);
                    if (type != null) {
                        counts.computeIfAbsent(type, k -> new AtomicInteger()).incrementAndGet();
                    }
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

        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int start = t;
            tasks.add(() -> {
               Map<String, Integer> local = new HashMap<>();
               for (int i = start; i < messages.size(); i += threads) {
                   String message = messages.get(i);
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
        // пустой вход
        report("пустой вход", countWithMerge(List.of(), THREADS), Map.of());

        // большой вход: при задаче на каждую строку это были бы миллионы объектов в очереди
        List<String> big = generateMessages(1_500_000, 900_000, 600_000);
        long start = System.currentTimeMillis();
        Map<String, Integer> bigResult = countWithMerge(big, THREADS);
        System.out.println("3 млн сообщений: " + new TreeMap<>(bigResult)
            + " за " + (System.currentTimeMillis() - start) + " мс");
    }

    private static void report(String name, Map<String, Integer> actual, Map<String, Integer> expected) {
        boolean ok = Objects.equals(new TreeMap<>(actual), new TreeMap<>(expected));
        System.out.println(name + ": " + new TreeMap<>(actual) + (ok ? "  OK" : "  НЕ СОВПАДАЕТ с ожидаемым"));
    }
}
