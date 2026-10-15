package org.learning.streams;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

//Вычислить факториал для каждого числа в списке
public class Streams5
{

    public Map<Integer, Long> calculateFactorials(List<Integer> numbers)
    {

        if (numbers == null || numbers.isEmpty()) {
            return Collections.emptyMap();
        }

        return numbers.stream()
                .collect(Collectors.toMap(
                        num -> num,
                        Streams5::factorial,
                        (existing, replacement) -> existing
                ));
    }

    private static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Факториал не определен для отрицательных чисел: " + n);
        }
        return LongStream.rangeClosed(2, n)
                .reduce(1L, Math::multiplyExact);
    }

}