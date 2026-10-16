import org.junit.jupiter.api.Test;
import org.learning.streams.Streams7;

import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestStreams7 {

    private final Streams7 streams7 = new Streams7();

    @Test
    void shouldGroupByLengthSortAndRemoveDuplicates() {

        List<String> input = List.of("bb", "a", "cc", "aa", "b", "ccc", "a");


        Map<Integer, List<String>> result = streams7.groupByLengthSortedUnique(input);


        assertEquals(3, result.size());

        assertEquals(List.of("a", "b"), result.get(1));
        assertEquals(List.of("aa", "bb", "cc"), result.get(2));
        assertEquals(List.of("ccc"), result.get(3));
    }


}