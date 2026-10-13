package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Streams2;
import org.learning.streams.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Streams2Test {

    private static final LocalDateTime START = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2023, 1, 31, 23, 59);

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        transaction1 = new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0));
        transaction2 = new Transaction("ClientA", "Books", LocalDateTime.of(2023, 1, 2, 10, 0));
        transaction3 = new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 1, 3, 10, 0));
    }

    @Test
    void exampleFromTask() {
        var transactions = List.of(
                transaction1,
                transaction2,
                transaction3
        );

        var result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Electronics", "Books"), START, END);

        assertEquals(List.of("ClientA"), result);
    }

    @Test
    void extraCategoriesDoNotBreak() {
        var transactions = List.of(
                transaction1,
                transaction2,
                transaction3
        );

        var result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Electronics", "Books"), START, END);

        assertEquals(List.of("ClientA"), result);
    }

    @Test
    void transactionOutsidePeriodIgnored() {
        var transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 10, 10, 0)),
                new Transaction("ClientA", "Books",       LocalDateTime.of(2023, 2, 10, 10, 0))
        );

        var result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Electronics", "Books"), START, END);

        assertTrue(result.isEmpty());
    }

    @Test
    void boundariesAreInclusive() {
        var transactions = List.of(
                transaction1,
                transaction2
        );

        var result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Electronics", "Books"), START, END);

        assertEquals(List.of("ClientA"), result);
    }

    @Test
    void noOnePasses() {
        var transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0)),
                new Transaction("ClientB", "Books", LocalDateTime.of(2023, 1, 2, 10, 0))
        );

        var result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Electronics", "Books"), START, END);

        assertTrue(result.isEmpty());
    }
}