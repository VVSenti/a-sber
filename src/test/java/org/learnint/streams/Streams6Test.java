package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Data;
import org.learning.streams.Streams6;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Streams6Test {

    @Test
    void findsCodesWithChangedVersion() {
        var data = List.of(new Data("22", "1"), new Data("11", "3"), new Data("33", "1"));
        var data2 = List.of(new Data("11", "3"), new Data("22", "3"), new Data("33", "2"));
        var result = Streams6.findEditedData(data, data2);
        assertEquals(Set.of("22", "33"), new HashSet<>(result));
        assertEquals(2, result.size());
    }

    @Test
    void sameDataInDifferentOrderIsNotChanged() {
        var data = List.of(new Data("1", "1"), new Data("2", "2"), new Data("3", "3"));
        var data2 = List.of(new Data("3", "3"), new Data("1", "1"), new Data("2", "2"));
        assertTrue(Streams6.findEditedData(data, data2).isEmpty());
    }

    @Test
    void allChanged() {
        var data = List.of(new Data("1", "1"), new Data("2", "1"));
        var data2 = List.of(new Data("2", "2"), new Data("1", "2"));
        assertEquals(Set.of("1", "2"), new HashSet<>(Streams6.findEditedData(data, data2)));
    }

    @Test
    void emptyLists() {
        assertTrue(Streams6.findEditedData(List.of(), List.of()).isEmpty());
    }

    @Test
    void nullVersionChangedToValueIsDetected() {
        var data = List.of(new Data("1", null));
        var data2 = List.of(new Data("1", "2"));
        assertEquals(List.of("1"), List.copyOf(Streams6.findEditedData(data, data2)));
    }

    @Test
    void bothNullVersionsAreNotChanged() {
        var data = List.of(new Data("1", null));
        var data2 = List.of(new Data("1", null));
        assertTrue(Streams6.findEditedData(data, data2).isEmpty());
    }

    @Test
    void valueChangedToNullIsDetected() {
        var data = List.of(new Data("1", "2"));
        var data2 = List.of(new Data("1", null));
        assertEquals(List.of("1"), List.copyOf(Streams6.findEditedData(data, data2)));
    }
}
