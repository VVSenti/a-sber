package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toSet;

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees) {
        if(employees==null || employees.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> depProject = employees.stream()
                .filter(emp -> emp != null && emp.department() != null && emp.department().name() != null)
                .collect(groupingBy(emp ->  emp.department().name(),
                        flatMapping(emp -> Objects.requireNonNullElse(emp.projects(), List.<Project>of()).stream(),
                                collectingAndThen(toSet(), Set::size))));

        int maxProject = depProject.values().stream()
                .max(Integer::compareTo)
                .orElse(0);

        return depProject.entrySet().stream()
                .filter(entry -> entry.getValue() == maxProject)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void main(String[] args) {
        //[{DeptA, [P1, P2]}, {DeptA, [P2, P3]}, {DeptB, [P1]}]

        var depA = new Department("depA");
        var depB = new Department("depB");

        var employees = List.of(
                new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
                new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
                new Employee("3", depB, List.of(new Project("1"))),
                new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6")))
        );

        System.out.println(findDepartmentsWithMostUniqueProjects(employees));
    }
}