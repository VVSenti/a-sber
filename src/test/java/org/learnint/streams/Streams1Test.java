package org.learnint.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.learning.streams.Department;
import org.learning.streams.Employee;
import org.learning.streams.Project;
import org.learning.streams.Streams1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Streams1Test {

    private final Department depA = new Department("depA");
    private final Department depB = new Department("depB");

    @Test
    void picksDepartmentWithMostUniqueProjects() {
        var employees = List.of(
            new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
            new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
            new Employee("3", depB, List.of(new Project("1"))),
            new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6")))
        );
        // depA: 1,2,3 = 3; depB: 1,4,5,6 = 4
        assertEquals(List.of("depB"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void duplicateProjectsInsideDepartmentCountOnce() {
        var employees = List.of(
            new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
            new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
            new Employee("3", depB, List.of(new Project("1")))
        );
        assertEquals(List.of("depA"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void returnsAllDepartmentsOnTie() {
        var employees = List.of(
            new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
            new Employee("2", depB, List.of(new Project("3"), new Project("4")))
        );
        var result = Streams1.findDepartmentsWithMostUniqueProjects(employees);
        assertEquals(Set.of("depA", "depB"), new HashSet<>(result));
        assertEquals(2, result.size());
    }

    @Test
    void emptyInputGivesEmptyResult() {
        assertTrue(Streams1.findDepartmentsWithMostUniqueProjects(List.of()).isEmpty());
    }

    @Test
    void nullEmployeeIsSkipped() {
        var employees = Arrays.asList(
            null,
            new Employee("1", depA, List.of(new Project("1"))));
        assertEquals(List.of("depA"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void employeeWithoutDepartmentIsSkipped() {
        var employees = List.of(
            new Employee("1", null, List.of(new Project("1"), new Project("2"))),
            new Employee("2", depA, List.of(new Project("3"))));
        assertEquals(List.of("depA"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void departmentWithNullNameIsSkipped() {
        var employees = List.of(
            new Employee("1", new Department(null), List.of(new Project("1"))),
            new Employee("2", depA, List.of(new Project("2"))));
        assertEquals(List.of("depA"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void nullProjectsListCountsAsZero() {
        var employees = List.of(
            new Employee("1", depA, null),
            new Employee("2", depB, List.of(new Project("1"))));
        assertEquals(List.of("depB"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void nullProjectIsNotCounted() {
        var employees = List.of(
            new Employee("1", depA, Arrays.asList(new Project("1"), null)),
            new Employee("2", depB, List.of(new Project("1"), new Project("2"))));
        // depA = 1 (null не считается), depB = 2
        assertEquals(List.of("depB"), Streams1.findDepartmentsWithMostUniqueProjects(employees));
    }

    @Test
    void nullEmployeeListGivesEmptyResult() {
        assertTrue(Streams1.findDepartmentsWithMostUniqueProjects(null).isEmpty());
    }
}
