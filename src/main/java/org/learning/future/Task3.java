package org.learning.future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Task3 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        List<String> logs = generateLogs();

        int poolSize = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        List<Callable<Map<String, Integer>>> tasks = createTasks(logs, poolSize);

        List<Future<Map<String, Integer>>> futures = executor.invokeAll(tasks);

        //Объединяем результаты
        Map<String, Integer> logCounts = new ConcurrentHashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> partialResult = future.get();
            partialResult.forEach((type, count) ->
                    logCounts.merge(type, count, Integer::sum)
            );
        }
        //Завершаем пул
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            executor.shutdownNow();
        }

        logCounts.forEach((type, count) -> System.out.println(type + " -> " + count));
    }

    private static List<Callable<Map<String, Integer>>> createTasks(List<String> logs, int poolSize) {
        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();

        for (int i = 0; i < poolSize; i++) {
            final int taskId = i;
            tasks.add(() -> {
                Map<String, Integer> localCounts = new HashMap<>();
                for (int j = taskId; j < logs.size(); j += poolSize) {
                    String log = logs.get(j);
                    String type = extractLogType(log);
                    if (type != null) {
                        localCounts.merge(type, 1, Integer::sum);
                    }
                }
                return localCounts;
            });
        }
        return tasks;
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
//Понял, вариант с шагом j += poolSize действительно гораздо чище