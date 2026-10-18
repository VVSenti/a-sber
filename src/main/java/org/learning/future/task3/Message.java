package org.learning.future.task3;

import lombok.Builder;

@Builder
public class Message {
    private String message;
    private MessageType type;
}
