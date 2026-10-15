package org.learning.streams;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

//Создать map, где ключ - длина строки, значение - отсортированный список уникальных строк этой длины
public class Streams7 {

    public static Map<Integer, List<String>> groupByLengthSortedUnique(List<String> strings) {
        return strings.stream()
            .distinct()
            .sorted()
            .collect(Collectors.groupingBy(String::length));
    }

    public static void main(String[] args) {
        var strings = List.of("Java", "SQL", "Kotlin", "Python", "c#", "c++", "Dart", "PHP", "Rust", "Go", "Swift");
        System.out.println(groupByLengthSortedUnique(strings));
    }
}
