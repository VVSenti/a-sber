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

    public static List<String> findClientsWithAllCategories(
        List<Transaction> transactions,
        Set<String> requiredCategories,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return transactions.stream()
            .filter(t -> t.timestamp().isAfter(start) && t.timestamp().isBefore(end))
            .collect(Collectors.groupingBy(
                Transaction::clientId,
                Collectors.mapping(Transaction::category, Collectors.toSet())
            )).entrySet().stream()
            .filter(e -> e.getValue().containsAll(requiredCategories))
            .map(Map.Entry::getKey)
            .toList();
    }

    public static void main(String[] args) {

        var transactions = List.of(
            // ClientA: обе категории в периоде -> должен попасть в результат
            new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0)),
            new Transaction("ClientA", "Books",       LocalDateTime.of(2023, 1, 2, 12, 0)),
            // ClientB: только Electronics -> не попадает
            new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 1, 3, 9, 0)),
            // ClientC: Books в периоде, Electronics вне периода -> не попадает
            new Transaction("ClientC", "Books",       LocalDateTime.of(2023, 1, 5, 15, 0)),
            new Transaction("ClientC", "Electronics", LocalDateTime.of(2022, 6, 1, 10, 0)),
            // ClientD: повторные покупки + обе категории -> попадает
            new Transaction("ClientD", "Books",       LocalDateTime.of(2023, 1, 4, 11, 0)),
            new Transaction("ClientD", "Books",       LocalDateTime.of(2023, 1, 6, 11, 0)),
            new Transaction("ClientD", "Electronics", LocalDateTime.of(2023, 1, 7, 11, 0))
        );

        var categories = Set.of("Electronics", "Books");
        var start = LocalDateTime.of(2023, 1, 1, 0, 0);
        var end = LocalDateTime.of(2023, 1, 31, 23, 59);

        var result = findClientsWithAllCategories(transactions, categories, start, end);

        System.out.println(result);
    }
}