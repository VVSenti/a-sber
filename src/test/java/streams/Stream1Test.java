package streams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.learning.streams.Department;
import org.learning.streams.Employee;
import org.learning.streams.Project;
import org.learning.streams.Streams1;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Stream1Test {

    private Department depA;
    private Department depB;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;

    @BeforeEach
    void setUp() {
        depA = new Department("depA");
        depB = new Department("depB");

        employee1 = new Employee("1", depA, List.of(new Project("1"), new Project("2")));
        employee2 = new Employee("2", depA, List.of(new Project("2"), new Project("3")));
        employee3 = new Employee("3", depB, List.of(new Project("1")));
    }

    @Test
    void exampleFromTask() {

        var employees = List.of(
                employee1, employee2, employee3
        );

        var result = Streams1.findDepartmentsWithMostUniqueProjects(employees);

        assertEquals(List.of("depA"), result);
    }

    @Test
    void twoDepartmentsShareMax() {

        var employees = List.of(
                new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
                new Employee("2", depB, List.of(new Project("3"), new Project("4")))
        );

        var result = Streams1.findDepartmentsWithMostUniqueProjects(employees);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("depA", "depB")));
    }

    @Test
    void duplicatesDoNotCount() {

        var employees = List.of(
                employee1, employee2
        );

        var result = Streams1.findDepartmentsWithMostUniqueProjects(employees);

        assertEquals(List.of("depA"), result);
    }

    @Test
    void emptyInput() {
        assertTrue(Streams1.findDepartmentsWithMostUniqueProjects(List.of()).isEmpty());
    }

}
