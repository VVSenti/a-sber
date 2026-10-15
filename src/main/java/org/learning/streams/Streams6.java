package org.learning.streams;

import java.util.*;
import java.util.stream.Collectors;

public class Streams6 {

    public static void main(String[] args) {
        List<Data> data = List.of(
            new Data("22", "1"),
            new Data("11", "3"),
            new Data("33", "1"));
        List<Data> data2 = List.of(
            new Data("11", "3"),
            new Data("22", "3"),
            new Data("33", "2"));
        System.out.println(findEditedData(data, data2));
    }

    //Реализовать метод, который сравнит два List<Data> и выведет в консоль code тех Data, в которых изменились version.
    //Известно, что приходящие на вход списки всегда имеют одинаковые длину и одинаковые code y Data,
    //однако version и порядок элементов могут отличаться.

    public static Collection<String> findEditedData(List<Data> data, List<Data> data2) {
        if (data == null || data2 == null || data.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Создаем карту для второго списка: code -> version
        Map<String, String> data2Map = data2.stream()
                .collect(Collectors.toMap(Data::code, Data::version, (v1, v2) -> v1));

        // 2. Ищем элементы из data, у которых версия отличается от версии в data2
        return data.stream()
                .filter(d -> !Objects.equals(d.version(), data2Map.get(d.code())))
                .map(Data::code)
                .toList();
    }
}
