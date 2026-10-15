package org.learning.future.task2;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

// Задача 2
//Есть 10 потоков. Каждый поток 10 000 раз увеличивает общий счетчик.
//
//Дан код: class Counter { private int value = 0;
//
//public void increment() {
//	value++;
//}
//
//public int getValue() {
//	return value;
//}
//}
//
//Нужно запустить инкременты параллельно через ExecutorService и добиться гарантированного результата 100_000.
//
//Кандидат должен предложить несколько вариантов решения:
//
//synchronized; AtomicInteger; LongAdder.
//
//Что проверить:
//
//понимает ли кандидат, почему value++ не является атомарной операцией; разницу между volatile и AtomicInteger; когда LongAdder предпочтительнее AtomicInteger; что происходит с производительностью при большом количестве конкурентных обновлений.
//
//Хорошее дополнительное задание: попросить кандидата объяснить, почему простое добавление volatile проблему не решает.
public class Task2 {

    private static final int THREAD_COUNT = 10;
    private static final int INCREMENT_PER_THREAD = 10000000;

    public static void main(String[] args) {

        var lock = new ReentrantLock();

        var counter = new Counter();
        var executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int j = 0; j < THREAD_COUNT; j++) {
            executorService.submit(() -> {
                for (int i = 0; i < INCREMENT_PER_THREAD; i++) {
                    lock.lock();
                    try {
                        counter.increment();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        executorService.shutdown();

        System.out.println(counter.getValue());
    }

}
