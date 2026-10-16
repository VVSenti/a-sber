package org.learning.future.task4;

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

    public static final Integer THREAD_COUNT = 3;
    public static final String TASK_DONE = "task '%s' is done.";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) throws InterruptedException {


        var cache = Task4Util.getCache();
        var info = Task4Util.getInfo();
        var config = Task4Util.getConfig();

        var tasks = List.of(cache, info, config);

        var taskCount = tasks.size();

        var countDownLatch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            var curTask = tasks.get(i);
            executorService.submit(curTask.getCallable());

            System.out.println();
            System.out.println(String.format("Start executing task '%s'.", curTask.getTaskName()));
            System.out.println();

            curTask.setStatus(Status.PROCESSING);
            System.out.println(String.format("Task '%s' is done!", curTask.getTaskName()));
            countDownLatch.countDown();
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("All tasks is Done!");
    }

    private static List<Task> prepareTaskList(List<Callable<String>> callableList) {
        return callableList
                .stream()
                .map(c -> Task
                        .builder()
                        .callable(c)
                        .status(Status.NEW)
                        .taskName(c.toString())
                        .build())
                .toList();
    }
}
