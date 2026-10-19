package org.learning.future;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Task4 {
    public static void main(String[] args) {
        System.out.println("Старт инициализации приложения...");

        CountDownLatch latch = new CountDownLatch(3);
        // Ссылка на ошибку если возникнет
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> loadComponent("Конфигурация", latch, errorRef));
        executor.submit(() -> loadComponent("Справочники", latch, errorRef));
        executor.submit(() -> loadComponent("Кэш", latch, errorRef));

        try {
            boolean allLoaded = latch.await(3, TimeUnit.SECONDS);

            if (!allLoaded) {
                System.err.println("\nОшибка: Не все компоненты успели загрузиться за отведенный таймаут!");
            } else if (errorRef.get() != null) {
                System.err.println("\nОшибка при инициализации! Причина: " + errorRef.get().getMessage());
                System.err.println("Приложение не может быть запущено.");
            } else {
                System.out.println("\nВсе 3 компонента успешно загружены! Приложение запущено.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Главный поток был прерван во время ожидания");
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }

    private static void loadComponent(String name, CountDownLatch latch, AtomicReference<Throwable> errorRef) {
        try {
            System.out.println("Началась загрузка: " + name);
            int duration = ThreadLocalRandom.current().nextInt(500, 1500);
            Thread.sleep(duration);

            // Симуляция ошибки
            if (name.equals("Справочники")) {
                throw new RuntimeException("Не удалось подключиться к БД справочников");
            }

            System.out.println("Загружено: " + name + " (заняло " + duration + " мс)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorRef.compareAndSet(null, e);
        } catch (Throwable t) {
            // Ловим RuntimeException и другие непроверяемые исключения
            errorRef.compareAndSet(null, t);
        } finally {
            latch.countDown();
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Потоки не успели завершиться, принудительная остановка...");
                pool.shutdownNow();
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Пул не завершился штатно");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}