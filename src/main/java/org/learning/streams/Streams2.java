package org.learning.streams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        return null;
    }
}