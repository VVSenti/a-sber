package org.learning.streams;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

//Создать map, где ключ - длина строки, значение - отсортированный список уникальных строк этой длины
public class Streams7 {

    public Map<Integer, List<String>> groupByLengthSortedUnique(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return Collections.emptyMap();
        }

        return strings.stream()
                // 1. Оставляем только уникальные строки
                .distinct()
                // 2. Сортируем в алфавитном порядке
                .sorted()
                // 3. Группируем по длине строки
                .collect(Collectors.groupingBy(String::length));
    }
}
