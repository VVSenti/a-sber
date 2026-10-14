package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Streams7;

import java.util.List;
import java.util.Map;

public class Streams7Test {

    @Test
    void groupsByLengthWithSortedUniqueValues() {
        var strings = List.of("pear", "fig", "apple", "kiwi", "fig", "plum", "apple", "date", "lime", "yo");
        var expected = Map.of(
            2, List.of("yo"),
            3, List.of("fig"),
            4, List.of("date", "kiwi", "lime", "pear", "plum"),
            5, List.of("apple")
        );
        assertEquals(expected, Streams7.groupByLengthSortedUnique(strings));
    }

    @Test
    void listsAreSortedAlphabetically() {
        var result = Streams7.groupByLengthSortedUnique(List.of("c", "a", "b"));
        assertEquals(List.of("a", "b", "c"), result.get(1));
    }

    @Test
    void duplicatesAreRemoved() {
        var result = Streams7.groupByLengthSortedUnique(List.of("aa", "aa", "aa"));
        assertEquals(Map.of(2, List.of("aa")), result);
    }

    @Test
    void emptyInputGivesEmptyMap() {
        assertTrue(Streams7.groupByLengthSortedUnique(List.of()).isEmpty());
    }
}
