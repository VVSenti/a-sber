package org.learning.future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Task3 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<String> logs = generateLogs();

        //Определяем размер пула динамически
        int poolSize = Runtime.getRuntime().availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        //Делим список на чанки
        int chunkSize = (int) Math.ceil((double) logs.size() / poolSize);
        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();

        for (int i = 0; i < logs.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, logs.size());
            List<String> subList = logs.subList(i, end);

            //Создаем одну задачу на часть списка а не на каждую строку
            tasks.add(() -> {
                Map<String, Integer> localCounts = new HashMap<>();
                for (String log : subList) {
                    String type = extractLogType(log);
                    if (type != null) {
                        localCounts.merge(type, 1, Integer::sum);
                    }
                }
                return localCounts;
            });
        }

        //Запускаем все задачи разом и ждем их завершения
        List<Future<Map<String, Integer>>> futures = executor.invokeAll(tasks);

        //Объединяем результаты в общую ConcurrentHashMap
        Map<String, Integer> logCounts = new ConcurrentHashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> partialResult = future.get(); // Получаем результат части
            partialResult.forEach((type, count) ->
                    logCounts.merge(type, count, Integer::sum)
            );
        }

        //завершаем пул
        executor.shutdown();
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            executor.shutdownNow(); //если не успели прерываем
        }

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
