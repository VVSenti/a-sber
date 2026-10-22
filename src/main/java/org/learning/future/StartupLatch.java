package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// ## Задача 4
public class StartupLatch {

    static final long MIN_DELAY_MS = 200;
    static final long MAX_DELAY_MS = 801;

    record Component(String name, long delayMs, Supplier<? extends Exception> failure) {
        static Component ok(String name, long delayMs) {
            return new Component(name, delayMs, null);
        }

        static Component failing(String name, long delayMs, Supplier<? extends Exception> failure) {
            return new Component(name, delayMs, failure);
        }
    }

    enum Status {LOADED, FAILED, TIMED_OUT}

    // Итог загрузки одного компонента
    record LoadOutcome(String component, Status status, Throwable cause) {
        static LoadOutcome loaded(String component) {
            return new LoadOutcome(component, Status.LOADED, null);
        }

        static LoadOutcome failed(String component, Throwable cause) {
            return new LoadOutcome(component, Status.FAILED, cause);
        }

        static LoadOutcome timedOut(String component) {
            return new LoadOutcome(component, Status.TIMED_OUT, null);
        }

        // описание причины, безопасное при getMessage() == null
        String describeCause() {
            if (cause == null) {
                return "";
            }
            String message = cause.getMessage();
            return cause.getClass().getSimpleName() + (message == null ? "" : ": " + message);
        }
    }

    static List<Component> randomComponents() {
        return List.of(
            Component.ok("Config", randomDelay()),
            Component.ok("Dictionaries", randomDelay()),
            Component.ok("Cache", randomDelay()));
    }

    static long randomDelay() {
        return ThreadLocalRandom.current().nextLong(MIN_DELAY_MS, MAX_DELAY_MS);
    }

    // Имитация загрузки
    static void load(Component component) throws Exception {
        Thread.sleep(component.delayMs);
        if (component.failure() != null) {
            throw component.failure().get();
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
                    } catch (Throwable t) {
                        if (t instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("  ошибка загрузки " + component.name() + ": " + t);
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
    // Загружает все компоненты параллельно и ждёт не дольше timeout
    static List<LoadOutcome> loadAll(List<Component> components, long timeout, TimeUnit unit) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(components.size());
        ExecutorService executor = Executors.newFixedThreadPool(components.size());

        try {
            List<Future<LoadOutcome>> futures = new ArrayList<>();
            for (Component component : components) {
                futures.add(executor.submit(() -> loadOne(component, latch)));
            }

            boolean finishedInTime = latch.await(timeout, unit);

            List<LoadOutcome> outcomes = new ArrayList<>();
            for (int i = 0; i < components.size(); i++) {
                String name = components.get(i).name();
                Future<LoadOutcome> future = futures.get(i);

                if (!finishedInTime && future.cancel(true)) {
                    outcomes.add(LoadOutcome.timedOut(name));
                } else {
                    outcomes.add(resultOf(name, future));
                }
            }
            return outcomes;
        } finally {
            executor.shutdownNow();
        }
    }

    private static LoadOutcome loadOne(Component component, CountDownLatch latch) {
        try {
            load(component);
            return LoadOutcome.loaded(component.name());
        } catch (Throwable t) {
            if (t instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return LoadOutcome.failed(component.name(), t);
        } finally {
            latch.countDown();
        }
    }

    private static LoadOutcome resultOf(String name, Future<LoadOutcome> future) throws InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            return LoadOutcome.failed(name, e.getCause());
        } catch (CancellationException e) {
            return LoadOutcome.failed(name, e);
        }
    }

    static boolean startApplication(List<Component> components, long timeout, TimeUnit unit) throws InterruptedException {
        List<LoadOutcome> outcomes = loadAll(components, timeout, unit);
        List<String> loaded = outcomes.stream()
            .filter(o -> o.status() == Status.LOADED).map(LoadOutcome::component).toList();
        List<LoadOutcome> failed = outcomes.stream()
            .filter(o -> o.status() == Status.FAILED).toList();
        List<String> timedOut = outcomes.stream()
            .filter(o -> o.status() == Status.TIMED_OUT).map(LoadOutcome::component).toList();

        System.out.println("  загружены: " + loaded);
        failed.forEach(o -> System.out.println("  с ошибкой: " + o.component() + " (" + o.describeCause() + ")"));
        if (!timedOut.isEmpty()) {
            System.out.println("  не успели за отведённое время: " + timedOut);
        }

        // строгая проверка: успешных ровно столько, сколько ожидали
        boolean allLoaded = loaded.size() == components.size();
        System.out.println(allLoaded
            ? "  загружено " + loaded.size() + "/" + components.size() + " -> приложение стартует"
            : "  загружено " + loaded.size() + "/" + components.size() + " -> старт отменён");
        return allLoaded;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("1) Базовое решение:");
        startBasic(randomComponents());

        System.out.println("\n2) С таймаутом 3 с, всё грузится нормально:");
        startApplication(randomComponents(), 3, TimeUnit.SECONDS);

        System.out.println("\n3) Один компонент падает с исключением:");
        startApplication(List.of(
            Component.ok("Config", 300),
            Component.failing("Dictionaries", 500, () -> new IllegalStateException("не удалось загрузить Dictionaries")),
            Component.ok("Cache", 400)), 3, TimeUnit.SECONDS);

        System.out.println("\n4) Исключение БЕЗ сообщения (getMessage() == null):");
        startApplication(List.of(
            Component.ok("Config", 300),
            Component.failing("Dictionaries", 500, IllegalStateException::new),
            Component.ok("Cache", 400)), 3, TimeUnit.SECONDS);

        System.out.println("\n5) Один компонент слишком медленный (таймаут 1,5 с):");
        startApplication(List.of(
            Component.ok("Config", 300),
            Component.ok("Dictionaries", 500),
            Component.ok("Cache", 10_000)), 1500, TimeUnit.MILLISECONDS);

        System.out.println("\n6) Один упал, другой не успел:");
        startApplication(List.of(
            Component.failing("Config", 300, () -> new IllegalStateException("битый файл конфигурации")),
            Component.ok("Dictionaries", 500),
            Component.ok("Cache", 10_000)), 1500, TimeUnit.MILLISECONDS);

        System.out.println("\n7) CountDownLatch одноразовый:");
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();
        System.out.println("  счётчик = " + latch.getCount() + ", await() возвращается сразу");
        latch.await();
        latch.countDown(); // ничего не меняет, обратно счётчик не вырастет
        System.out.println("  после ещё одного countDown() счётчик = " + latch.getCount());
    }
}
