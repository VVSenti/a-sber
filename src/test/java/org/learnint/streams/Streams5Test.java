package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Streams5;

import java.util.List;
import java.util.Map;

public class Streams5Test {

    @Test
    void calculatesFactorials() {
        var result = Streams5.calculateFactorials(List.of(0, 1, 3, 5, 10, 20));
        var expected = Map.of(
            0, 1L,
            1, 1L,
            3, 6L,
            5, 120L,
            10, 3_628_800L,
            20, 2_432_902_008_176_640_000L
        );
        assertEquals(expected, result);
    }

    @Test
    void duplicatesDoNotCauseException() {
        var result = Streams5.calculateFactorials(List.of(5, 5, 3));
        assertEquals(Map.of(5, 120L, 3, 6L), result);
    }

    @Test
    void overflowThrows() {
        // 21! не помещается в long
        assertThrows(ArithmeticException.class, () -> Streams5.calculateFactorials(List.of(21)));
    }

    @Test
    void emptyListGivesEmptyMap() {
        assertTrue(Streams5.calculateFactorials(List.of()).isEmpty());
    }
}
