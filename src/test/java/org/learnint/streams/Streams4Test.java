package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Streams4;
import org.learning.streams.Student;

import java.util.Arrays;
import java.util.List;

public class Streams4Test {

    @Test
    void filtersAndSortsInReverseOrder() {
        var students = List.of(
            new Student("Anna", List.of(5, 4, 5)),
            new Student("Boris", List.of(3, 5, 4)),
            new Student("Clara", List.of(4, 4, 4)),
            new Student("David", List.of(5, 5)),
            new Student("Elena", List.of(2, 5, 5))
        );
        assertEquals(List.of("David", "Clara", "Anna"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void gradeEqualToMinPasses() {
        var students = List.of(new Student("Anna", List.of(4, 4)));
        assertEquals(List.of("Anna"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void noOneQualifies() {
        var students = List.of(new Student("Anna", List.of(1, 2)));
        assertTrue(Streams4.filterAndSortStudents(students, 5).isEmpty());
    }

    @Test
    void emptyStudentList() {
        assertTrue(Streams4.filterAndSortStudents(List.of(), 4).isEmpty());
    }

    @Test
    void studentWithoutGradesPassesByDefault() {
        var students = List.of(new Student("Anna", List.of()));
        assertEquals(List.of("Anna"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void nullStudentIsSkipped() {
        var students = Arrays.asList(null, new Student("Anna", List.of(5, 5)));
        assertEquals(List.of("Anna"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void studentWithNullGradesListIsSkipped() {
        var students = List.of(
            new Student("Anna", null),
            new Student("Boris", List.of(5, 5)));
        assertEquals(List.of("Boris"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void studentWithNullGradeIsSkipped() {
        var students = List.of(
            new Student("Anna", Arrays.asList(5, null, 5)),
            new Student("Boris", List.of(5, 5)));
        assertEquals(List.of("Boris"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void studentWithNullNameIsSkipped() {
        var students = List.of(
            new Student(null, List.of(5, 5)),
            new Student("Boris", List.of(5, 5)));
        assertEquals(List.of("Boris"), Streams4.filterAndSortStudents(students, 4));
    }

    @Test
    void nullStudentListGivesEmptyResult() {
        assertTrue(Streams4.filterAndSortStudents(null, 4).isEmpty());
    }
}
