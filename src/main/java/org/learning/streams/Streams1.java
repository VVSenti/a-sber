package org.learning.streams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees)
    {
// 0. Базовая проверка на null/пустой список на входе
    if (employees == null || employees.isEmpty()) {
        return List.of();
    }

    Map<Department, Integer> uniqueProjectsByDep = employees.stream()
            // 1. Сразу группируем сотрудников по их департаменту
            .collect(Collectors.groupingBy(
                    Employee::department,
                    // 2. Внутри каждого департамента разворачиваем списки проектов в один поток
                    Collectors.flatMapping(
                            e -> e.projects().stream(),
                            // 3. Собираем проекты в Set и берем его размер
                            Collectors.collectingAndThen(Collectors.toSet(), Set::size)
                    )
            ));


    // Шаг А: Находим максимальное число проектов среди всех департаментов
    int maxCount = uniqueProjectsByDep.values().stream()
            .max(Integer::compare)
            .orElse(0);

    // Если проектов вообще ни у кого нет, возвращаем пустой список
    if (maxCount == 0) {
        return List.of();
    }

    // Шаг Б: Фильтруем карту и забираем имена тех, у кого количество равно maxCount
    return uniqueProjectsByDep.entrySet().stream()
            .filter(entry -> entry.getValue() == maxCount)
            .map(entry -> entry.getKey().name()) // Берем только имя департамента (String)
            .collect(Collectors.toList());      // Возвращаем List<String>
}
    public static void main(String[] args) {
        //[{DeptA, [P1, P2]}, {DeptA, [P2, Р3]}, {DeptB, [P1]}]

        var depA = new Department("depA");
        var depB = new Department("depB");

        var employees = List.of(
            new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
            new Employee("2", depA, List.of(new Project("2"), new Project("3"))),
            new Employee("3", depB, List.of(new Project("1")))
                //    new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6")))
        );
        System.out.println(findDepartmentsWithMostUniqueProjects(employees));
    }
}