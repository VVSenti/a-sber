package org.learning.future.task2;

import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter {

    private final LongAdder value = new LongAdder();

    public void increment() {
        value.increment();
    }

    public long getValue() {
        return value.sum();
    }
}
