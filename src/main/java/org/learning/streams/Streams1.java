package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }

        // 1. Группируем проекты по имени департамента в Set для устранения дублей
        Map<String, Set<Project>> deptProjects = employees.stream()
                .collect(Collectors.groupingBy(
                        emp -> emp.department().name(),
                        Collectors.flatMapping(
                                emp -> emp.projects().stream(),
                                Collectors.toSet()
                        )
                ));

        // 2. Находим максимальное количество уникальных проектов среди всех отделов
        long maxProjects = deptProjects.values().stream()
                .mapToLong(Set::size)
                .max()
                .orElse(0);

        if (maxProjects == 0) {
            return List.of();
        }

        // 3. Отбираем все отделы, у которых число проектов равно максимуму
        return deptProjects.entrySet().stream()
                .filter(entry -> entry.getValue().size() == maxProjects)
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