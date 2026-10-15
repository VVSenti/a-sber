package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Streams4;
import org.learning.streams.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Streams4Test {

    private List<Student> students;

    @BeforeEach
    void setUp() {
        students = List.of(
                new Student("Alice",   List.of(5, 4, 3)),
                new Student("Bob",     List.of(5, 5, 5)),
                new Student("Charlie", List.of(3, 3, 3)),
                new Student("Diana",   List.of(4, 3, 5, 3)),
                new Student("Eve",     List.of(5, 4, 4)),
                new Student("Frank",   List.of(2, 5, 5)),
                new Student("Grace",   List.of(1, 2, 3)),
                new Student("Zed",     List.of(5, 5)),
                new Student("Mike",    List.of(5, 5)),
                new Student("Kate",    List.of())
        );
    }

    @Test
    void exampleFromTask() {
        var result = Streams4.filterAndSortStudents(students, 6);

        assertTrue(result.isEmpty());
    }

    @Test
    void minGradeThree() {
        var result = Streams4.filterAndSortStudents(students, 3);

        assertEquals(7, result.size());
        assertTrue(result.containsAll(List.of(
                "Alice", "Bob", "Charlie", "Diana", "Eve", "Zed", "Mike")));
        assertFalse(result.contains("Kate"));
    }

    @Test
    void boundaryIsInclusive() {
        var result = Streams4.filterAndSortStudents(students, 3);

        assertTrue(result.contains("Charlie"));
    }

    @Test
    void reverseAlphabeticalOrder() {
        var result = Streams4.filterAndSortStudents(students, 5);

        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).compareTo(result.get(i)) > 0,
                    "Ожидался убывающий порядок, но " + result.get(i - 1)
                            + " идёт перед " + result.get(i));
        }
    }

    @Test
    void emptyGradesPassFilter() {
        var result = Streams4.filterAndSortStudents(students, 3);

        assertFalse(result.contains("Kate"));
    }

    @Test
    void noOnePasses() {
        var studentsWithLowGrades = List.of(
                new Student("Alice", List.of(1, 2)),
                new Student("Bob",   List.of(0))
        );

        var result = Streams4.filterAndSortStudents(studentsWithLowGrades, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void emptyStudentsList() {
        var result = Streams4.filterAndSortStudents(List.of(), 3);

        assertTrue(result.isEmpty());
    }
}