package org.learning.streams;

import java.util.Comparator;
import java.util.List;

//Задача 2: Найти студентов, у которых все оценки >= minGrade, и вернуть их имена в обратном алфавитном порядке

public class Streams4 {

    public List<String> filterAndSortStudents(List<Student> students, int minGrade) {

        if (students == null || students.isEmpty()) {
            return List.of();
        }

        return students.stream()
                // 1. Проверяем, что оценки есть и каждая из них >= minGrade
                .filter(s -> s.grades() != null
                        && !s.grades().isEmpty()
                        && s.grades().stream().allMatch(grade -> grade >= minGrade))
                // 2. Получаем имя студента
                .map(Student::name)
                // 3. Сортируем в обратном алфавитном порядке
                .sorted(Comparator.reverseOrder())
                // 4. Собираем в List
                .toList();
    }
}
