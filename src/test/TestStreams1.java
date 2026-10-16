import org.junit.jupiter.api.Test;
import org.learning.streams.Department;
import org.learning.streams.Employee;
import org.learning.streams.Project;
import org.learning.streams.Streams1;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestStreams1 {

    @Test
    void testFindDepartmentsWithMostUniqueProjects() {
        // 1. Готовим тестовые данные
        var depA = new Department("depA");
        var depB = new Department("depB");

        var employees = List.of(
                new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
                new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
                new Employee("3", depB, List.of(new Project("1"))),
                new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6")))
        );

        // 2. Вызываем тестируемый метод
        List<String> result = Streams1.findDepartmentsWithMostUniqueProjects(employees);

        // 3. Проверяем, что в ответе вернулся строго список с ["depB"]
        assertEquals(List.of("depB"), result);
    }
}
