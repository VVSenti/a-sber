package org.learning.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

// ## Задача 2
public class CounterDemo {

    static final int THREADS = 10;

    interface Counter {
        void increment();

        long getValue();
    }

    static class UnsafeCounter implements Counter {
        private int value = 0;

        public void increment() {
            value++;
        }

        public long getValue() {
            return value;
        }
    }

    /** volatile даёт видимость, но не атомарность: проблему НЕ решает. */
    static class VolatileCounter implements Counter {
        private volatile int value = 0;

        public void increment() {
            value++;
        }

        public long getValue() {
            return value;
        }
    }

    static class SynchronizedCounter implements Counter {
        private int value = 0;

        @Override
        public synchronized void increment() {
            value++;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    static class AtomicCounter implements Counter {
        private final AtomicInteger value = new AtomicInteger(0);

        @Override
        public void increment() {
            value.incrementAndGet();
        }

        @Override
        public long getValue() {
            return value.get();
        }
    }

    static class LongAdderCounter implements Counter {
        private final LongAdder value = new LongAdder();

        @Override
        public void increment() {
            value.increment();
        }

        @Override
        public long getValue() {
            return value.sum();
        }
    }

    static long run(Counter counter, int iterations) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        try {
            CyclicBarrier startTogether = new CyclicBarrier(THREADS);
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                tasks.add(() -> {
                    startTogether.await();
                    for (int j = 0; j < iterations; j++) {
                        counter.increment();
                    }
                    return null;
                });
            }
            for (Future<Void> future : executor.invokeAll(tasks)) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }
        return counter.getValue();
    }

    public static void main(String[] args) throws Exception {
        int iterations = 10_000;
        long expected = (long) THREADS * iterations;
        System.out.println("Ожидается: " + expected);

        System.out.println("UnsafeCounter:       " + run(new UnsafeCounter(), iterations) + "  <- на многоядерной машине обычно меньше");
        System.out.println("VolatileCounter:     " + run(new VolatileCounter(), iterations) + "  <- тоже, volatile не помогает");
        System.out.println("SynchronizedCounter: " + run(new SynchronizedCounter(), iterations));
        System.out.println("AtomicCounter:       " + run(new AtomicCounter(), iterations));
        System.out.println("LongAdderCounter:    " + run(new LongAdderCounter(), iterations));

        // увеличиваем нагрузку
        int many = 1_000_000;
        System.out.println("\nПо " + many + " инкрементов на поток (ожидается " + (long) THREADS * many + "):");
        System.out.println("UnsafeCounter:       " + run(new UnsafeCounter(), many));
        System.out.println("VolatileCounter:     " + run(new VolatileCounter(), many));

        // сравнение скорости при большой нагрузке
        int heavy = 5_000_000;
        System.out.println("\nПо " + heavy + " инкрементов на поток:");
        timeIt("synchronized", new SynchronizedCounter(), heavy);
        timeIt("AtomicInteger", new AtomicCounter(), heavy);
        timeIt("LongAdder", new LongAdderCounter(), heavy);
    }

    private static void timeIt(String name, Counter counter, int iterations) throws Exception {
        long start = System.nanoTime();
        long result = run(counter, iterations);
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println(name + ": " + result + " за " + ms + " мс");
    }
}
