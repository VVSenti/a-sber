package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Data;
import org.learning.streams.Streams6;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Streams6Test {

    private List<Data> data;
    private List<Data> data2;

    @BeforeEach
    void setUp() {
        data = List.of(
                new Data("22", "1"),
                new Data("11", "3"),
                new Data("33", "1")
        );
        data2 = List.of(
                new Data("11", "3"),
                new Data("22", "3"),
                new Data("33", "2")
        );
    }

    @Test
    void exampleFromTask() {
        var result = Streams6.findEditedData(data, data2);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("22", "33")));
    }

    @Test
    void noChanges() {
        var noChange2 = List.of(
                new Data("22", "1"),
                new Data("11", "3"),
                new Data("33", "1")
        );

        var result = Streams6.findEditedData(data, noChange2);

        assertTrue(result.isEmpty());
    }

    @Test
    void allChanged() {
        var allNew = List.of(
                new Data("22", "9"),
                new Data("11", "9"),
                new Data("33", "9")
        );

        var result = Streams6.findEditedData(data, allNew);

        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of("22", "11", "33")));
    }

    @Test
    void orderDoesNotMatter() {
        var reordered = List.of(
                new Data("33", "2"),
                new Data("22", "3"),
                new Data("11", "3")
        );

        var result = Streams6.findEditedData(data, reordered);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("22", "33")));
    }

    @Test
    void emptyInput() {
        assertTrue(Streams6.findEditedData(List.of(), List.of()).isEmpty());
    }
}