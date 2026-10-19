package org.learning.future;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class Task2 {

    abstract static class Counter{
        public abstract void increment();
        public abstract int getValue();
    }

    static class SynchronizedCounter extends Counter{
        private int value = 0;
        @Override
        public synchronized void increment() {
            value++;
        }
        @Override
        public synchronized int getValue() {
            return value;
        }
    }

    static class AtomicCounter extends Counter{
        private final AtomicInteger value = new AtomicInteger(0);

        @Override
        public void  increment() {
            value.incrementAndGet();
        }
        @Override
        public int getValue() {
            return value.get();
        }
    }

    static class LongAdderCounter extends Counter{
        private final LongAdder value = new LongAdder();

        @Override
        public void  increment() {
            value.increment();
        }

        @Override
        public int getValue() {
            return value.intValue();
        }
    }

    public static void testCounter(Counter counter) throws InterruptedException{

        int threadsCount = 10;
        int incrementsPerThread = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        try {
            CountDownLatch latch = new CountDownLatch(threadsCount);

            for (int i = 0; i < threadsCount; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < incrementsPerThread; j++) {
                            counter.increment();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            System.out.println(counter.getClass().getSimpleName() + " итоговое значение: " + counter.getValue());
        } finally {
            executor.shutdown();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        testCounter(new SynchronizedCounter());
        testCounter(new AtomicCounter());
        testCounter(new LongAdderCounter());
    }
}
