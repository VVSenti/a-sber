package org.learning.future.task1;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class OrderProcessorUtil {

    List<Order> getUnprocessedOrders(List<Order> allOrders, List<OrderResult> successfullyProcessedOrders) {
        var processedNumbers = successfullyProcessedOrders
                .stream()
                .map(r -> r.getOrder().getNumber())
                .collect(Collectors.toSet());

        return allOrders
                .stream()
                .filter(o -> !processedNumbers.contains(o.getNumber()))
                .toList();
    }

    List<OrderResult> mapOrdersToFailedOrderResults(List<Order> failedProcessedOrders) {
        return failedProcessedOrders.stream()
                .map(OrderResultMapper::getFailedOrderResultFromOrder)
                .toList();
    }

    int computeThreadCount() {
        return (int) (Task1Constants.CPU_BOUND
                * (1.0 + (double) Task1Constants.WAIT_TIME_IN_MILLIS
                / Task1Constants.COMPUTE_TIME_IN_MILLIS));
    }
}
