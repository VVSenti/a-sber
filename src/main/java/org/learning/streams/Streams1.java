package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees) {
        if (employees == null) {
            return List.of();
        }

        Map<String, Integer> countByDepartment = employees.stream()
            .filter(Objects::nonNull)
            .filter(e -> e.department() != null && e.department().name() != null)
            .collect(Collectors.groupingBy(e ->
                    e.department().name(),
                Collectors.flatMapping(e ->
                        e.projects() == null ?
                            Stream.empty()
                            : e.projects().stream().filter(Objects::nonNull),
                    Collectors.collectingAndThen(Collectors.toSet(), Set::size))
            ));

        int maxUniqElements = countByDepartment.values().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);

        return countByDepartment.entrySet().stream()
            .filter(e -> e.getValue() == maxUniqElements)
            .map(Map.Entry::getKey)
            .toList();
    }

    public static void main(String[] args) {
        //[{DeptA, [P1, P2]}, {DeptA, [P2, Р3]}, {DeptB, [P1]}]

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