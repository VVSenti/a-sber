package org.learning.streams;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

//Создать map, где ключ - длина строки, значение - отсортированный список уникальных строк этой длины
public class Streams7 {

    public Map<Integer, List<String>> groupByLengthSortedUnique(List<String> strings)
    {
        return strings.stream()
                .distinct()
                .collect(Collectors.groupingBy(
                        String::length,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list->list.stream()
                                        .sorted()
                                        .collect(Collectors.toList())
                        )
                ));
    }
}
