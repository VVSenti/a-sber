
import org.junit.Test;
import org.learning.streams.Streams5;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class TestStreams5 {

    private final Streams5 streams = new Streams5();

    @Test
    public void testFactorials() {
        Map<Integer, Long> result = streams.calculateFactorials(List.of(1, 2, 3, 4, 5));

        assertEquals(1L, result.get(1));
        assertEquals(2L, result.get(2));
        assertEquals(6L, result.get(3));
        assertEquals(24L, result.get(4));
        assertEquals(120L, result.get(5));
    }
}
