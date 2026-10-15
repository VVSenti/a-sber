package org.learning.future.task1;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class Order {
    private UUID number;
}
