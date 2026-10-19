package org.learning.future.task4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Задача 4
 * Запуск задачи только после завершения нескольких потоков — CountDownLatch
 * <p>
 * Условие: Есть сервис, который перед началом работы должен дождаться загрузки трех независимых компонентов:
 * <p>
 * Конфигурации; справочников; кэша.
 * <p>
 * Каждый компонент загружается в отдельном потоке и занимает случайное количество времени. Главный поток не должен начинать работу, пока все три компонента не загрузятся.
 * <p>
 * Использовать: ExecutorService CountDownLatch
 * <p>
 * Схема примерно такая: ┌─ Config ─────┐ │ │ Start ───┼─ Dictionaries├──→ Application starts │ │ └─ Cache ──────┘
 * <p>
 * Что проверить:
 * <p>
 * Понимание CountDownLatch; зачем нужен countDown(); где использовать await(); что произойдет, если одна из задач завершится с исключением; можно ли переиспользовать CountDownLatch; чем CountDownLatch отличается от CyclicBarrier.
 * <p>
 * Усложнение: добавить timeout на ожидание загрузки компонентов и корректную обработку ситуации, когда один из компонентов не загрузился.
 */
public class Task4 {

    private static final Integer THREAD_COUNT = 3;
    private static final Integer TASKS_COUNT = 3;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private static final String WAITING_TIME_EXCEEDED = "Waiting time exceeded";

    public static void main(String[] args) {

        var countDownLatch = new CountDownLatch(TASKS_COUNT);

        var cache = Task4Util.getCache(countDownLatch);
        var info = Task4Util.getInfo(countDownLatch);
        var config = Task4Util.getConfig(countDownLatch);

        var tasks = List.of(cache, info, config);

        for (int i = 0; i < TASKS_COUNT; i++) {
            var curTask = tasks.get(i);
            executorService.submit(curTask.getCallable());

            System.out.println();
            System.out.println(String.format("Start executing task '%s'.", curTask.getTaskName()));
            System.out.println();

            curTask.setStatus(Status.PROCESSING);
            System.out.println(String.format("Task '%s' is done!", curTask.getTaskName()));
        }

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(WAITING_TIME_EXCEEDED);
            Thread.currentThread().interrupt();
        }
        System.out.println("All tasks is Done!");

        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
