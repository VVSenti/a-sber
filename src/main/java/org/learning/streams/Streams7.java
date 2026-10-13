package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

//Создать map, где ключ - длина строки, значение - отсортированный список уникальных строк этой длины
public class Streams7 {

    public static void main(String[] args) {
        List<String> list = List.of("asdf", "jhfdxj");

        var map = groupByLengthSortedUnique(list);

        map.entrySet().stream().forEach(v -> System.out.println(v.getKey() + " " + v.getValue()));
    }

    public static Map<Integer, List<String>> groupByLengthSortedUnique(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return Map.of();
        }

        return strings.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.groupingBy(String::length)
                );
    }
}
