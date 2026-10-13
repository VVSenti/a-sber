package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Streams5;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Streams5Test {

    private List<Integer> numbers;

    @BeforeEach
    void setUp() {
        numbers = List.of(1, 2, 3, 4);
    }

    @Test
    void exampleFromTask() {
        var result = Streams5.calculateFactorials(numbers);

        assertEquals(Map.of(
                1, 1L,
                2, 2L,
                3, 6L,
                4, 24L
        ), result);
    }

    @Test
    void zeroAndOne() {
        var result = Streams5.calculateFactorials(List.of(0, 1));

        assertEquals(Map.of(0, 1L, 1, 1L), result);
    }

    @Test
    void duplicatesAreRemoved() {
        var result = Streams5.calculateFactorials(List.of(5, 5, 5, 3));

        assertEquals(Map.of(5, 120L, 3, 6L), result);
    }

    @Test
    void emptyInput() {
        assertTrue(Streams5.calculateFactorials(List.of()).isEmpty());
    }

    @Test
    void twentyFactorialFitsInLong() {
        var result = Streams5.calculateFactorials(List.of(20));

        assertEquals(Map.of(20, 2432902008176640000L), result);
    }

    @Test
    void keyIsOriginalNumber() {
        var result = Streams5.calculateFactorials(numbers);

        assertEquals(4, result.size());
        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(4));
    }
}