package org.learning.streams;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

//Задача 2: Найти студентов, у которых все оценки >= minGrade, и вернуть их имена в обратном алфавитном порядке

public class Streams4 {

    public static List<String> filterAndSortStudents(List<Student> students, int minGrade) {
        if (students == null) {
            return List.of();
        }

        return students.stream()
            .filter(Objects::nonNull)
            .filter(s -> s.name() != null)
            .filter(s -> s.grades() != null && !s.grades().isEmpty())
            .filter(s -> s.grades().stream().allMatch(g -> g != null && g >= minGrade))
            .map(Student::name)
            .sorted(Comparator.reverseOrder())
            .toList();
    }

    public static void main(String[] args) {
        var students = List.of(
            new Student("Anna", List.of(5, 4, 5)),   // все >= 4 -> проходит
            new Student("Boris", List.of(3, 5, 4)),   // есть 3 -> не проходит
            new Student("Clara", List.of(4, 4, 4)),   // все >= 4 -> проходит
            new Student("David", List.of(5, 5)),      // все >= 4 -> проходит
            new Student("Elena", List.of(2, 5, 5))    // есть 2 -> не проходит
        );

        var result = filterAndSortStudents(students, 4);
        System.out.println(result);
    }
}
