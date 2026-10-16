import org.junit.jupiter.api.Test;
import org.learning.streams.Streams4;
import org.learning.streams.Student;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestStreams4 {

    private final Streams4 streams4 = new Streams4();

    @Test
    void bothStudentsPassWhenMinGradeIs2() {
        List<Student> students = List.of(
                new Student("Ilia",  List.of(4, 2, 3, 5, 5)),
                new Student("Vasia", List.of(4, 5, 2, 3, 2))
        );

        List<String> result = streams4.filterAndSortStudents(students, 2);

        // Обратный алфавитный порядок: V > I
        assertEquals(List.of("Vasia", "Ilia"), result);
    }

    @Test
    void onlyStudentsWithAllGradesAtLeast4AreReturned() {
        List<Student> students = List.of(
                new Student("Ilia",  List.of(4, 2, 3, 5, 5)),   // есть 2 и 3 → не проходит
                new Student("Anna",  List.of(5, 5, 5, 5, 5)),   // все >= 4 → проходит
                new Student("Boris", List.of(4, 4, 4, 4, 4))    // все >= 4 → проходит
        );

        List<String> result = streams4.filterAndSortStudents(students, 4);

        // Обратный порядок: Boris > Anna
        assertEquals(List.of("Boris", "Anna"), result);
    }
}