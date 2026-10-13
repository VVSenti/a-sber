package org.learning.streams;

import java.util.Comparator;
import java.util.List;

//Задача 2: Найти студентов, у которых все оценки >= minGrade, и вернуть их имена в обратном алфавитном порядке

public class Streams4 {

    public static void main(String[] args) {
        List<Student> students = students();

        List<String> filtered = filterAndSortStudents(students,6);

        for (String s : filtered) {
            System.out.println(s);
        }
    }

    public static List<String> filterAndSortStudents(List<Student> students, int minGrade) {
        return students.stream()
                .filter(s -> s.grades() != null && !s.grades().isEmpty()
                )
                .filter(s -> s.grades().stream()
                        .allMatch(g -> g >= minGrade)
                )
                .map(s -> s.name())
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public static List<Student> students() {
        return List.of(
                new Student("Alice",    null),
                new Student("Bob",      List.of(5, 5, 5)),
                new Student("Charlie",  List.of(3, 3, 3)),
                new Student("Diana",    List.of(4, 3, 5, 3)),
                new Student("Eve",      List.of(5, 4, 4)),
                new Student("Frank",    List.of(2, 5, 5)),
                new Student("Grace",    List.of(1, 2, 3)),
                new Student("Henry",    List.of(5, 2)),
                new Student("Ivy",      List.of(0, 5, 5)),
                new Student("Jack",     List.of(3, 2, 4)),
                new Student("Kate",     List.of()),
                new Student("Leo",      List.of()),
                new Student("Mia",      List.of(5)),
                new Student("Noah",     List.of(3)),
                new Student("Olivia",   List.of(2)),
                new Student("Pete",     List.of(1)),
                new Student("Quinn",    List.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5)),
                new Student("Rose",     List.of(3, 3, 3, 3, 3, 3, 3, 3, 3, 3)),
                new Student("Sam",      List.of(4, 4, 4, 4, 4, 4, 4, 4, 4, 2)),
                new Student("Zoe",      List.of(5, 5)),
                new Student("Yan",      List.of(5, 5)),
                new Student("Xenia",    List.of(5, 5)),
                new Student("Walter",   List.of(5, 5)),
                new Student("Victor",   List.of(5, 5)),
                new Student("Uma",      List.of(4, 4)),
                new Student("Tom",      List.of(4, 3)),
                new Student("anna",     List.of(5)),
                new Student("Ben",      List.of(5)),
                new Student("Carl",     List.of(0, 0, 0)),
                new Student("Dora",     List.of(10, 10, 10))
        );
    }
}
