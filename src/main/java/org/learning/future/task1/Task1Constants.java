package org.learning.future.task1;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Task1Constants {

    public final int CPU_BOUND = Runtime.getRuntime().availableProcessors();
    public final int WAIT_TIME_IN_MILLIS = 3;
    public final int COMPUTE_TIME_IN_MILLIS = 2;
    public final int TOTAL_PROCESS_TIMEOUT_IN_SECONDS = 5;
    public final int AWAIT_TERMINATION_IN_SECONDS = 10;

    public final String ORDER_FAILED_PATTERN_EXCEPTION =  "Order failed: %s";
    public final String ORDER_TIMED_OUT_EXCEPTION =  "Order timed out!";
    public final String INTERRUPT_WHILE_WAITING_EXCEPTION = "Interrupted while waiting!";

}
