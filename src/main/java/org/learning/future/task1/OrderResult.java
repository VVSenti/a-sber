package org.learning.future.task1;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class OrderResult {
    private Order order;
    private Status status;
}
