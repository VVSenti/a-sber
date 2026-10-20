package org.learning.future.task1;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderResultMapper {

    public OrderResult getFailedOrderResultFromOrder(Order order) {
        return OrderResult
                .builder()
                .order(order)
                .status(Status.ERROR)
                .build();
    }

    public OrderResult generateNewOrderResultFromOrder(Order order) {
        return OrderResult
                .builder()
                .order(order)
                .status(Status.NEW)
                .build();
    }

}
