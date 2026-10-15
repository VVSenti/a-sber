package org.learning.future.task2;

import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Setter
public class Counter {

    private int value;

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }

}
