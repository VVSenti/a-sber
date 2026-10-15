package org.learning.future;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Task4
{
    public static void main(String[] args)
    {
        System.out.println("Старт инициализации приложения...");

        CountDownLatch latch = new CountDownLatch(3);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> loadComponent("Конфигурация", latch));
        executor.submit(() -> loadComponent("Справочники", latch));
        executor.submit(() -> loadComponent("Кэш", latch));

        try {
            boolean allLoaded = latch.await(3, TimeUnit.SECONDS);

            if (allLoaded) {
                System.out.println("\nВсе 3 компонента успешно загружены! Приложение запущено.");
            }
            else {
                System.err.println("\nОшибка: Не все компоненты успели загрузиться за отведенный таймаут!");
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Главный поток был прерван во время ожидания");
        }
        finally {
            executor.shutdown();
        }
    }

    private static void loadComponent(String name, CountDownLatch latch) {
        try {
            System.out.println("Началась загрузка: " + name);
            // Имитируем случайное время работы от 500 до 1500 мс
            int duration = 500 + new Random().nextInt(1000);
            Thread.sleep(duration);
            System.out.println("Загружено: " + name + " (заняло " + duration + " мс)");
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Загрузка " + name + " была прервана");
        }
        finally {
            latch.countDown();
        }
    }
}
