package org.learning.streams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/* 
Найти клиентов, совершивших покупки во ВСЕХ категориях за указанный период.
Пример:
Вход: Транзакции [
	{ClientA, "Electronics", 2023-01-01},
	{ClientA, "Books", 2023-01-02},
	{ClientB, "Electronics", 2023-01-03}
], категории - ["Electronics", "Books"]

Выход: ["ClientA"]
*/

public class Streams2 {

    public static void main(String[] args) {

        var start = LocalDateTime.of(2023, 1, 1, 0, 0);
        var end   = LocalDateTime.of(2023, 1, 31, 23, 59);

        var transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0)),
                new Transaction("ClientA", "Books",       LocalDateTime.of(2023, 1, 2, 10, 0)),
                new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 1, 3, 10, 0))
        );

        var requiredCategories = Set.of("Electronics", "Books");

        var result = findClientsWithAllCategories(transactions, requiredCategories, start, end);

        System.out.println(result);
    }

    public static List<String> findClientsWithAllCategories(
        List<Transaction> transactions,
        Set<String> requiredCategories,
        LocalDateTime start,
        LocalDateTime end
    ) {

        return transactions
                .stream()
                .filter(t -> !t.timestamp().isBefore(start)
                        && !t.timestamp().isAfter(end)
                ).collect(Collectors.groupingBy(
                        Transaction::clientId,
                        Collectors.mapping(Transaction::category, Collectors.toSet())
                ))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().containsAll(requiredCategories))
                .map(Map.Entry::getKey)
                .toList();
    }
}