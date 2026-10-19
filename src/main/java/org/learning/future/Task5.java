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
/*
Да ты прав, чтото я совсем забылся.
в оригинальном коде быдл несколько проблем:

Использование HashMap в многопоточке.
HashMap не является потокобезопасным, т.к метод put вызывается из 10 потоков одновременно, это приведет к состоянию гонки.
Изза этого один поток перезапишет данные другого, получит NPE или, зациклит программу. Поэтому нужен ConcurrentHashMap.

executor shutdown не блокирует главный поток.
Метод shutdown просто запрещает пулу принимать новые задачи. Он не ждет пока 10 потоков закончат генерацию отчетов.
Поэтому следующий за ним System.out.println("Reports generated: " + reports.size()) выполнится мгновенно, когда мапа еще пуста (выведет 0 или случайное число).
Нужно использовать awaitTermination или дождаться выполнения future.

логическая ошибка жизненного цикла пула.
ExecutorService объявлен как поле класса, но shutdown вызывается внутри метода generateReports.
значит, что после первого вызова метода пул навсегда закроется и повторный вызов generateReports выбросит REE.
пул нужно либо создавать локально внутри метода, либо не закрывать его там.

проглатывание исключений.
если внутри generateReport(id) вылетит RuntimeException поток умрет, но главный поток об этом не узнает т.к нет Future.get и обработки ExecutionException.
В консоли просто будет меньше отчетов, а причину ошибки никто не увидит.
 */
