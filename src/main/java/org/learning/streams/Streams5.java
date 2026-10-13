package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

//Вычислить факториал для каждого числа в списке
public class Streams5 {

    public static void main(String[] args) {
        List<Integer> list= List.of(1, 2, 3, 4);

        Map<Integer, Long> res = calculateFactorials(list);

        res.forEach((key, value) -> System.out.println(value));
    }

    public static Map<Integer, Long> calculateFactorials(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .collect(Collectors.toMap(
                                n -> n,
                                    n -> LongStream.rangeClosed(2, n)
                                        .reduce(1, Math::multiplyExact)
                        )
                );
    }
}