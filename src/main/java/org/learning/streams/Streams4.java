package org.learning.streams;
import java.util.Comparator;
import java.util.List;

//Задача 2: Найти студентов, у которых все оценки >= minGrade,
// и вернуть их имена в обратном алфавитном порядке

public class Streams4 {

    public List<String> filterAndSortStudents(List<Student> students, int minGrade) {
        return students.stream()
                // 1. Фильтруем: оставляем только студентов, у которых ВСЕ оценки >= minGrade
                .filter(student -> student.grades().stream()
                        .allMatch(grade -> grade >= minGrade))
                // 2. Преобразуем Student -> String (имя)
                .map(Student::name)
                // 3. Сортируем в обратном алфавитном порядке
                .sorted(Comparator.reverseOrder())
                // 4. Собираем результат в список
                .toList();
    }

    public static void main(String[] args) {
        List<Student> list = List.of(
                new Student("Ilia", List.of(4, 2, 3, 5, 5)),
                new Student("Vasia", List.of(4, 5, 2, 3, 2))
        );

        Streams4 streams4 = new Streams4();
        System.out.println(streams4.filterAndSortStudents(list, 3));
        System.out.println(streams4.filterAndSortStudents(list, 5));
        System.out.println(streams4.filterAndSortStudents(list, 1));
    }
}