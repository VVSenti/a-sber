package org.learning.future.task2;

import java.util.concurrent.atomic.AtomicInteger;


public class AtomicCounter {
    private final AtomicInteger value = new AtomicInteger(0);

    public void increment() {
        value.incrementAndGet();
    }

    public int getValue() {
        return value.get();
    }
}
