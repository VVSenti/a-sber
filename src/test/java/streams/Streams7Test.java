package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Streams7;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class Streams7Test {

    private List<String> strings;

    @BeforeEach
    void setUp() {
        strings = List.of(
                "bb", "a", "cc", "bb", "aaa", "a", "dd", "abc"
        );
    }

    @Test
    void exampleFromTask() {
        var result = Streams7.groupByLengthSortedUnique(strings);

        assertEquals(Map.of(
                1, List.of("a"),
                2, List.of("bb", "cc", "dd"),
                3, List.of("aaa", "abc")
        ), result);
    }

    @Test
    void duplicateStringsRemoved() {
        var result = Streams7.groupByLengthSortedUnique(
                List.of("ab", "ab", "ab"));

        assertEquals(Map.of(2, List.of("ab")), result);
    }

    @Test
    void sortingWithinGroup() {
        var result = Streams7.groupByLengthSortedUnique(
                List.of("cc", "bb", "aa"));

        assertEquals(Map.of(2, List.of("aa", "bb", "cc")), result);
    }

    @Test
    void emptyInput() {
        assertTrue(Streams7.groupByLengthSortedUnique(List.of()).isEmpty());
    }

    @Test
    void nullInput() {
        assertTrue(Streams7.groupByLengthSortedUnique(null).isEmpty());
    }

    @Test
    void keyIsStringLength() {
        var result = Streams7.groupByLengthSortedUnique(strings);

        assertEquals(3, result.size());
        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(3));
    }
}