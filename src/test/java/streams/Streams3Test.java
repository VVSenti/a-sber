package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Order;
import org.learning.streams.Streams3;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Streams3Test {

    private List<Order> orders;

    @BeforeEach
    void setUp() {
        orders = List.of(
                new Order("1", "Electronics", 1),
                new Order("1", "Food", 2),
                new Order("2", "Electronics", 3),
                new Order("2", "Food", 4),
                new Order("2", "Food", 4),
                new Order("3", "Electronics", 5),
                new Order("3", "Electronics", 5)
        );
    }

    @Test
    void exampleFromTask() {
        var result = Streams3.groupOrders(orders);

        assertEquals(Map.of(
                "1", Map.of("Electronics", 1, "Food", 2),
                "2", Map.of("Electronics", 3, "Food", 8),
                "3", Map.of("Electronics", 10)
        ), result);
    }

    @Test
    void singleClientSingleCategory() {
        var single = List.of(new Order("1", "Food", 5));

        var result = Streams3.groupOrders(single);

        assertEquals(Map.of("1", Map.of("Food", 5)), result);
    }

    @Test
    void emptyInput() {
        assertTrue(Streams3.groupOrders(List.of()).isEmpty());
    }

    @Test
    void resultContainsAllClients() {
        var result = Streams3.groupOrders(orders);

        assertEquals(3, result.size());
        assertTrue(result.containsKey("1"));
        assertTrue(result.containsKey("2"));
        assertTrue(result.containsKey("3"));
    }

    @Test
    void sumsWithinSameCategory() {
        var result = Streams3.groupOrders(orders);

        assertEquals(8, result.get("2").get("Food"));
        assertEquals(10, result.get("3").get("Electronics"));
    }
}