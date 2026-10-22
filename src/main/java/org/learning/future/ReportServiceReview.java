package org.learning.future;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.LongStream;

// ## Задача 5
public class ReportServiceReview {

    record Report(long id, String content) {
    }

    // долгая генерация и ошибка для id = 13
    static Report slowGenerate(Long id) throws InterruptedException {
        Thread.sleep(100);
        if (id == 13) {
            throw new IllegalStateException("нет данных для отчета " + id);
        }
        return new Report(id, "report-" + id);
    }

    // исходный код
    static class BrokenReportService {

        private final ExecutorService executor = Executors.newFixedThreadPool(10);

        private final Map<Long, Report> reports = new HashMap<>();

        public void generateReports(List<Long> ids) {
            for (Long id : ids) {
                executor.submit(() -> {
                    Report report = generateReport(id);
                    reports.put(id, report);
                });
            }
            executor.shutdown();

            System.out.println("Reports generated: " + reports.size());
        }

        private Report generateReport(Long id) {
            try {
                return slowGenerate(id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int size() {
            return reports.size();
        }
    }

    // исправленная версия
    static class ReportService implements AutoCloseable {

        private final ExecutorService executor;

        public ReportService(int poolSize) {
            this.executor = Executors.newFixedThreadPool(poolSize);
        }

        public Map<Long, Report> generateReports(List<Long> ids, long timeout, TimeUnit unit) throws InterruptedException {
            if (ids == null || ids.isEmpty()) {
                return Map.of();
            }

            Map<Long, Future<Report>> futures = new LinkedHashMap<>();
            ids.stream().filter(Objects::nonNull).distinct().forEach(id -> {
                Callable<Report> task = () -> slowGenerate(id);
                futures.put(id, executor.submit(task));
            });

            Map<Long, Report> reports = new LinkedHashMap<>();
            try {
                for (Map.Entry<Long, Future<Report>> entry : futures.entrySet()) {
                    try {
                        reports.put(entry.getKey(), entry.getValue().get(timeout, unit));
                    } catch (ExecutionException e) {
                        System.err.println("Не удалось построить отчёт " + entry.getKey() + ": " + e.getCause());
                    } catch (TimeoutException e) {
                        System.err.println("Таймаут отчёта " + entry.getKey());
                        entry.getValue().cancel(true);
                    }
                }
            } catch (InterruptedException e) {
                futures.values().forEach(f -> f.cancel(true));
                throw e;
            }
            System.out.println("Reports generated: " + reports.size() + " of " + futures.size());
            return reports;
        }

        @Override
        public void close() throws Exception {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<Long> ids = LongStream.rangeClosed(1, 20).boxed().toList(); // id 13 упадёт

        System.out.println("== Исходный код ==");
        BrokenReportService broken = new BrokenReportService();
        broken.generateReports(ids);
        Thread.sleep(1000);
        System.out.println("через секунду в карте: " + broken.size() + " (ошибка id=13 нигде не видна)");

        try {
            broken.generateReports(List.of(100L));
        } catch (RejectedExecutionException e) {
            System.out.println("второй вызов: " + e.getClass().getSimpleName());
        }

        System.out.println("\n== Исправленная версия ==");
        try (ReportService service = new ReportService(10)) {
            Map<Long, Report> result = service.generateReports(ids, 2, TimeUnit.SECONDS);
            System.out.println("получено ключей: " + result.size() + ", есть 13? " + result.containsKey(13L));
            System.out.println("второй вызов работает: " + service.generateReports(List.of(100L, 100L, 101L), 2, TimeUnit.SECONDS).keySet());
        }
    }
}
