package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Task3 {
    public static void main(String[] args) throws InterruptedException {
        List<String> logs = generateLogs();

        Map<String, Integer> logCounts = new ConcurrentHashMap<>();

        int poolSize = 4;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (String log : logs) {
            executor.submit(() -> {
                String type = extractLogType(log);
                if (type != null) {
                    // Атомарно увеличиваем значение на 1
                    logCounts.merge(type, 1, Integer::sum);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        logCounts.forEach((type, count) -> System.out.println(type + " -> " + count));
    }

    private static String extractLogType(String log) {
        if (log == null || log.isBlank()) {
            return null;
        }
        int spaceIndex = log.indexOf(' ');
        return spaceIndex != -1 ? log.substring(0, spaceIndex) : log;
    }

    private static List<String> generateLogs() {
        List<String> list = new ArrayList<>(10_000);
        for (int i = 0; i < 5000; i++) list.add("INFO user logged in");
        for (int i = 0; i < 3000; i++) list.add("ERROR database connection");
        for (int i = 0; i < 2000; i++) list.add("WARN low disk space");
        return list;
    }
}
