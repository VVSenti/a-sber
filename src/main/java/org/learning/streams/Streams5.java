package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

//Вычислить факториал для каждого числа в списке
public class Streams5 {

    public static Map<Integer, Long> calculateFactorials(List<Integer> numbers) {
        return numbers.stream()
            .distinct()
            .collect(Collectors.toMap(
                n -> n,
                n -> LongStream.rangeClosed(1, n).reduce(1, Math::multiplyExact)
            ));
    }

    public static void main(String[] args) {
        var numbers = List.of(0, 1, 2, 3, 5, 5, 10);
        System.out.println(calculateFactorials(numbers));
    }
}