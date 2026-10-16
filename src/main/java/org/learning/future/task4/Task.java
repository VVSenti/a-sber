package org.learning.future.task4;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;

@Setter
@Getter
@Builder
public class Task {
    private Callable<String> callable;
    private String taskName;
    private Status status;
}
