package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Задача 1: Найти отделы с максимальным количеством уникальных проектов. Пример: Вход: Сотрудники с проектами [{DeptA, [P1, P2]}, {DeptA, [P2, Р3]},
 * {DeptB, [P1]}] Выход: ["DeptA"] (y DeptA 3 уникальных проекта, у DeptB - 1).
 */

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees) {

        Map<Department, Integer> uniqueProjectsByDept = employees.stream()
                .collect(Collectors
                        .groupingBy(
                                Employee::department,
                                Collectors.flatMapping(
                                        e ->  Objects.requireNonNullElse(
                                                e.projects(),
                                                List.<Project>of()).stream()
                                                .map(Project::id),
                                        Collectors.collectingAndThen(Collectors.toSet(), Set::size)
                                )
                        )
                );

        var max = uniqueProjectsByDept.values()
                .stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        return uniqueProjectsByDept
                .entrySet()
                .stream()
                .filter(e -> Objects.equals(e.getValue(), max))
                .map(e -> e.getKey().name())
                .toList();
    }

    public static void main(String[] args) {
        //[{DeptA, [P1, P2]}, {DeptA, [P2, Р3]}, {DeptB, [P1]}]

        var depA = new Department("depA");
        var depB = new Department("depB");

        var employees = List.of(
                new Employee("1", depA, null),
                new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
                new Employee("3", depB, List.of(new Project("1"))),
                new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6")))
        );

        System.out.println(findDepartmentsWithMostUniqueProjects(employees));
    }
}