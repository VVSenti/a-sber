package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Задача 1: Сгруппировать заказы по клиенту и категории, вычислить сумму

public class Streams3 {

    public static Map<String, Map<String, Integer>> groupOrders(List<Order> orders) {
        return orders.stream()
            .collect(Collectors.groupingBy(
                Order::clientId,
                Collectors.groupingBy(
                    Order::categoryId,
                    Collectors.summingInt(Order::amount)
                )
            ));
    }

    public static void main(String[] args) {
        List<Order> list = List.of(
            new Order("1", "Electronics", 1),
            new Order("1", "Food", 2),
            new Order("2", "Electronics", 3),
            new Order("2", "Food", 4),
            new Order("2", "Food", 4),
            new Order("3", "Electronics", 5),
            new Order("3", "Electronics", 5)
        );
        System.out.println(groupOrders(list));
    }
}
