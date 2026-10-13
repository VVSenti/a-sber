package org.learning.streams;

import java.util.List;

public class Streams1 {

    public static List<String> findDepartmentsWithMostUniqueProjects(List<Employee> employees) {
        return null;
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