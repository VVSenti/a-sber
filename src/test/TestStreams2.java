import org.junit.jupiter.api.Test;
import org.learning.streams.Streams2;
import org.learning.streams.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestStreams2 {

    private final LocalDateTime START = LocalDateTime.of(2023, 1, 1, 0, 0);
    private final LocalDateTime END   = LocalDateTime.of(2023, 12, 31, 23, 59);

    @Test
    void exampleFromTask() {
        List<Transaction> transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0)),
                new Transaction("ClientA", "Books",       LocalDateTime.of(2023, 1, 2, 10, 0)),
                new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 1, 3, 10, 0))
        );
        Set<String> categories = Set.of("Electronics", "Books");

        List<String> result = Streams2.findClientsWithAllCategories(
                transactions, categories, START, END);

        assertEquals(List.of("ClientA"), result);
    }

    @Test
    void multipleClientsWithAllCategories() {
        List<Transaction> transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 2, 1, 10, 0)),
                new Transaction("ClientA", "Books",       LocalDateTime.of(2023, 2, 2, 10, 0)),
                new Transaction("ClientB", "Books",       LocalDateTime.of(2023, 3, 1, 10, 0)),
                new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 3, 2, 10, 0)),
                new Transaction("ClientC", "Electronics", LocalDateTime.of(2023, 3, 3, 10, 0))
        );
        Set<String> categories = Set.of("Electronics", "Books");

        List<String> result = Streams2.findClientsWithAllCategories(
                transactions, categories, START, END);

        assertEquals(List.of("ClientA", "ClientB"), result);
    }


    @Test
    void emptyRequiredCategories() {
        List<Transaction> transactions = List.of(
                new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0))
        );

        List<String> result = Streams2.findClientsWithAllCategories(
                transactions, Set.of(), START, END);

        assertTrue(result.isEmpty());
    }



    @Test
    void singleCategoryCase() {
        List<Transaction> transactions = List.of(
                new Transaction("ClientA", "Books", LocalDateTime.of(2023, 1, 1, 10, 0)),
                new Transaction("ClientB", "Books", LocalDateTime.of(2023, 1, 2, 10, 0))
        );

        List<String> result = Streams2.findClientsWithAllCategories(
                transactions, Set.of("Books"), START, END);

        assertEquals(List.of("ClientA", "ClientB"), result);
    }
}