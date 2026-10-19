package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

// ## Задача 4
public class StartupLatch {

    static final long MIN_DELAY_MS = 200;
    static final long MAX_DELAY_MS = 801;

    record Component(String name, long delayMs, boolean fails) {
    }

    static List<Component> randomComponents() {
        return List.of(
            new Component("Config", randomDelay(), false),
            new Component("Dictionaries", randomDelay(), false),
            new Component("Cache", randomDelay(), false));
    }

    static long randomDelay() {
        return ThreadLocalRandom.current().nextLong(MIN_DELAY_MS, MAX_DELAY_MS);
    }

    // Имитация загрузки
    static void load(Component component) throws Exception {
        Thread.sleep(component.delayMs);
        if (component.fails()) {
            throw new IllegalStateException("не удалось загрузить " + component.name);
        }
    }

    static void startBasic(List<Component> components) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(components.size());
        ExecutorService executor = Executors.newFixedThreadPool(components.size());

        try {
            for (Component component : components) {
                executor.submit(() -> {
                    try {
                        load(component);
                        System.out.println(" загружен: " + component.name() + " (" + component.delayMs() + " мс)");
                    } catch (Exception e) {
                        // ничего не делаем
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            System.out.println("  все компоненты загружены -> приложение стартует");
        } finally {
            executor.shutdown();
        }
    }

    // timeout + обработка неудачной загрузки
    static boolean startWithTimeout(List<Component> components, long timeout, TimeUnit unit) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(components.size());
        Set<String> loaded = ConcurrentHashMap.newKeySet();
        Map<String, String> failed = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(components.size());

        try {
            for (Component component : components) {
                executor.submit(() -> {
                    try {
                        load(component);
                        loaded.add(component.name);
                    } catch (Exception e) {
                        failed.put(component.name(), e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            boolean finishedInTime = latch.await(timeout, unit);

            if (!finishedInTime) {
                // считаем незавершённые ДО shutdownNow(), иначе прерванные попадут в failed
                List<String> unfinished = new ArrayList<>();
                for (Component component : components) {
                    if (!loaded.contains(component.name()) && !failed.containsKey(component.name())) {
                        unfinished.add(component.name());
                    }
                }
                System.out.println("  ТАЙМАУТ: не успели загрузиться " + unfinished + ", старт отменён");
                executor.shutdownNow();
                return false;
            }
            // latch открылся, но не факт, что все задачи загрузились успешно
            if (!failed.isEmpty()) {
                System.out.println("  ОШИБКА загрузки: " + failed + ", старт отменён");
                return false;
            }

            System.out.println("  все компоненты загружены " + loaded.size() + "/" + components.size() + " -> приложение стартует");
            return true;
        } finally {
            executor.shutdownNow(); // либо всё завершено, либо отменено
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("1) Базовое решение:");
        startBasic(randomComponents());

        System.out.println("\n2) С таймаутом 3 с, всё грузится нормально:");
        startWithTimeout(randomComponents(), 3, TimeUnit.SECONDS);

        System.out.println("\n3) Один компонент падает с исключением:");
        startWithTimeout(List.of(
            new Component("Config", 300, false),
            new Component("Dictionaries", 500, true),
            new Component("Cache", 400, false)), 3, TimeUnit.SECONDS);

        System.out.println("\n4) Один компонент слишком медленный (таймаут 1,5 с):");
        startWithTimeout(List.of(
            new Component("Config", 300, false),
            new Component("Dictionaries", 500, false),
            new Component("Cache", 10_000, false)), 1500, TimeUnit.MILLISECONDS);

        System.out.println("\n5) CountDownLatch одноразовый:");
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();
        System.out.println("  счётчик = " + latch.getCount() + ", await() возвращается сразу");
        latch.await();
        latch.countDown(); // ничего не меняет, обратно счётчик не вырастет
        System.out.println("  после ещё одного countDown() счётчик = " + latch.getCount());
    }
}
