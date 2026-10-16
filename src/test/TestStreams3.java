

import org.junit.jupiter.api.Test;
import org.learning.streams.Order;
import org.learning.streams.Streams3;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


 public class TestStreams3{
    @Test
    void groupOrders_shouldGroupByClientAndCategory_withSum() {
        List<Order> orders = List.of(
                new Order("1", "Electronics", 1),
                new Order("1", "Food", 2),
                new Order("2", "Electronics", 3),
                new Order("2", "Food", 4),
                new Order("2", "Food", 4),
                new Order("3", "Electronics", 5),
                new Order("3", "Electronics", 5)
        );

        Map<String, Map<String, Integer>> expected = Map.of(
                "1", Map.of("Electronics", 1, "Food", 2),
                "2", Map.of("Electronics", 3, "Food", 8),
                "3", Map.of("Electronics", 10)
        );

        assertEquals(expected, Streams3.groupOrders(orders));
    }
}