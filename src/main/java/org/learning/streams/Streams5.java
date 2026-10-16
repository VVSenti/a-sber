package org.learning.streams;

import java.util.List;
import java.util.Map;

//Вычислить факториал для каждого числа в списке
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Streams5 {

    public Map<Integer, Long> calculateFactorials(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .collect(Collectors.toMap(
                        n -> n,
                        this::factorial
                ));
    }

    private long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Факториал не определён для отрицательных чисел: " + n);
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }


}