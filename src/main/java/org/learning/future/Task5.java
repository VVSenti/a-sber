package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Task5 {
    public record Report(Long id, String data) {}

    public Map<Long, Report> generateReports(List<Long> ids) {
        // потокобезопасная мапа под конкретный запрос
        Map<Long, Report> reports = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        for (Long id : ids) {
            Future<?> future = executor.submit(() -> {
                try {
                    Report report = generateReport(id);
                    reports.put(id, report);
                }
                catch (Exception e) {
                    System.err.println("Ошибка генерации отчета для id=" + id + ": " + e.getMessage());
                }
            });
            futures.add(future);
        }

        // ждем завершения всех задач
        for (Future<?> f : futures) {
            try {
                f.get(10, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                System.err.println("Ошибка ожидания задачи: " + e.getMessage());
            }
        }

        executor.shutdown();

        System.out.println("Reports generated: " + reports.size());
        return reports;
    }

    private Report generateReport(Long id) {
        // имитация работы
        return new Report(id, "Content of report " + id);
    }
}
