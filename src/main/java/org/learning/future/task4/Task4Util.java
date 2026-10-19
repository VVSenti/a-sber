package org.learning.future.task4;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Task4Util {

    public static final String CACHE_TASK_ENDED_PATTERN = "Cache task was executed in %s milliseconds";
    public static final String CONFIG_TASK_ENDED_PATTERN = "Config task was executed in %s milliseconds";
    public static final String INFO_TASK_ENDED_PATTERN = "Info task was executed in %s milliseconds";


    public static Task getCache(CountDownLatch countDownLatch) {
        Callable<String> callable = () -> {
            try {
                var startTime = System.currentTimeMillis();
                Thread.sleep(50);
                var endTime = System.currentTimeMillis();

                var executeTime = endTime - startTime;
                System.out.println(String.format(CACHE_TASK_ENDED_PATTERN, executeTime));
                return "cache";
            } finally {
                countDownLatch.countDown();
            }
        };

        return Task
                .builder()
                .status(Status.NEW)
                .taskName("cache")
                .callable(callable)
                .build();
    }

    public static Task getConfig(CountDownLatch countDownLatch) {

            Callable<String> callable = () -> {
                try {
                var startTime = System.currentTimeMillis();
                Thread.sleep(1000);
                var endTime = System.currentTimeMillis();
                var executeTime = endTime - startTime;
                System.out.println(String.format(CONFIG_TASK_ENDED_PATTERN, executeTime));
                return "config";
                } finally {
                    countDownLatch.countDown();
                }
            };

        return Task
                .builder()
                .status(Status.NEW)
                .taskName("config")
                .callable(callable)
                .build();
    }

    public static Task getInfo(CountDownLatch countDownLatch) {
        Callable<String> callable = () -> {
            try {
                var startTime = System.currentTimeMillis();
                Thread.sleep(3000);
                var endTime = System.currentTimeMillis();
                var executeTime = endTime - startTime;
                System.out.println(String.format(INFO_TASK_ENDED_PATTERN, executeTime));
                return "info";
            } finally {
                countDownLatch.countDown();
            }
        };
        return Task
                .builder()
                .status(Status.NEW)
                .taskName("info")
                .callable(callable)
                .build();
    }
}
