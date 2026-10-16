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
        if (requiredCategories == null || requiredCategories.isEmpty()) {
            return List.of();
        }

        // Для каждого клиента собираем множество категорий, в которых он совершал покупки
        // за указанный период

        Map<String, Set<String>> clientCategories = transactions.stream()
                .filter(t -> !t.timestamp().isBefore(start) && !t.timestamp().isAfter(end))
                .collect(Collectors.groupingBy(
                        Transaction::clientId,
                        Collectors.mapping(Transaction::category, Collectors.toSet())
                ));

        // Оставляем только тех клиентов, у которых множество категорий
        // содержит все требуемые категории
        return clientCategories.entrySet().stream()
                .filter(e -> e.getValue().containsAll(requiredCategories))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }
}