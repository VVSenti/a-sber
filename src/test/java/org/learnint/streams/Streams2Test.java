package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Streams2;
import org.learning.streams.Transaction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Streams2Test {

    private final Set<String> categories = Set.of("Electronics", "Books");
    private final LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2023, 1, 31, 23, 59);

    private Set<String> run(List<Transaction> tx, Set<String> cats) {
        return new HashSet<>(Streams2.findClientsWithAllCategories(tx, cats, start, end));
    }

    @Test
    void findsClientsWithAllCategories() {
        var tx = List.of(
            new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 1, 10, 0)),
            new Transaction("ClientA", "Books", LocalDateTime.of(2023, 1, 2, 12, 0)),
            new Transaction("ClientB", "Electronics", LocalDateTime.of(2023, 1, 3, 9, 0)),
            new Transaction("ClientC", "Books", LocalDateTime.of(2023, 1, 5, 15, 0)),
            new Transaction("ClientC", "Electronics", LocalDateTime.of(2022, 6, 1, 10, 0)),
            new Transaction("ClientD", "Books", LocalDateTime.of(2023, 1, 4, 11, 0)),
            new Transaction("ClientD", "Books", LocalDateTime.of(2023, 1, 6, 11, 0)),
            new Transaction("ClientD", "Electronics", LocalDateTime.of(2023, 1, 7, 11, 0))
        );
        assertEquals(Set.of("ClientA", "ClientD"), run(tx, categories));
    }

    @Test
    void transactionsOutsidePeriodAreIgnored() {
        var tx = List.of(
            new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 10, 10, 0)),
            new Transaction("ClientA", "Books", LocalDateTime.of(2023, 2, 1, 10, 0)) // после end
        );
        assertTrue(run(tx, categories).isEmpty());
    }

    @Test
    void periodBoundariesAreInclusive() {
        var tx = List.of(
            new Transaction("ClientA", "Electronics", start),
            new Transaction("ClientA", "Books", end)
        );
        assertEquals(Set.of("ClientA"), run(tx, categories));
    }

    @Test
    void extraCategoriesDoNotInterfere() {
        var tx = List.of(
            new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 2, 10, 0)),
            new Transaction("ClientA", "Books", LocalDateTime.of(2023, 1, 3, 10, 0)),
            new Transaction("ClientA", "Food", LocalDateTime.of(2023, 1, 4, 10, 0))
        );
        assertEquals(Set.of("ClientA"), run(tx, categories));
    }

    @Test
    void emptyRequiredCategoriesReturnsEveryoneWithTransactionInPeriod() {
        var tx = List.of(
            new Transaction("ClientA", "Electronics", LocalDateTime.of(2023, 1, 2, 10, 0)),
            new Transaction("ClientB", "Books", LocalDateTime.of(2023, 1, 3, 10, 0))
        );
        assertEquals(Set.of("ClientA", "ClientB"), run(tx, Set.of()));
    }

    @Test
    void noTransactionsGivesEmptyResult() {
        assertTrue(run(List.of(), categories).isEmpty());
    }
}
