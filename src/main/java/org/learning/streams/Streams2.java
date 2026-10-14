package org.learning.streams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Map;
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
        if (transactions == null || requiredCategories == null || requiredCategories.isEmpty()) {
            return List.of();
        }

        return transactions.stream()
                // 1. Фильтруем транзакции по дате в диапазоне start, end
                .filter(t -> (t.timestamp().isEqual(start) || t.timestamp().isAfter(start))
                        && (t.timestamp().isEqual(end) || t.timestamp().isBefore(end)))
                // 2. Группируем по clientId, собирая уникальные купленные категории в Set
                .collect(Collectors.groupingBy(
                        Transaction::clientId,
                        Collectors.mapping(Transaction::category, Collectors.toSet())
                ))
                .entrySet().stream()
                // 3. Оставляем клиентов, набор категорий которых содержит все requiredCategories
                .filter(entry -> entry.getValue().containsAll(requiredCategories))
                // 4. Достаем имена клиентов
                .map(Map.Entry::getKey)
                .toList();
    }
}