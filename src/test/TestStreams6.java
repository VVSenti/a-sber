import org.junit.jupiter.api.Test;
import org.learning.streams.Data;
import org.learning.streams.Streams6;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestStreams6 {

    @Test
    void shouldFindEditedData_whenVersionsDiffer() {
        List<Data> data = List.of(
                new Data("22", "1"),
                new Data("11", "3"),
                new Data("33", "1"));
        List<Data> data2 = List.of(
                new Data("11", "3"),
                new Data("22", "3"),
                new Data("33", "2"));

        var result = Streams6.findEditedData(data, data2);

        assertEquals(2, result.size());
        assertTrue(result.contains("22"));
        assertTrue(result.contains("33"));
        assertFalse(result.contains("11"));
    }

    @Test
    void shouldReturnEmpty_whenVersionsAreEqual() {
        List<Data> data = List.of(
                new Data("11", "1"),
                new Data("22", "2"));
        List<Data> data2 = List.of(
                new Data("22", "2"),
                new Data("11", "1"));

        var result = Streams6.findEditedData(data, data2);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindAll_whenAllVersionsChanged() {
        List<Data> data = List.of(
                new Data("11", "1"),
                new Data("22", "1"));
        List<Data> data2 = List.of(
                new Data("11", "2"),
                new Data("22", "3"));

        var result = Streams6.findEditedData(data, data2);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("11", "22")));
    }
}