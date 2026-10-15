package org.learning.streams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StreamsTest {

    @Test
    @DisplayName("Streams1: Поиск отдела с максимальным числом уникальных проектов")
    void testFindDepartmentsWithMostUniqueProjects() {
        var depA = new Department("depA");
        var depB = new Department("depB");

        var employees = List.of(
                new Employee("1", depA, List.of(new Project("1"), new Project("2"))),
                new Employee("2", depA, List.of(new Project("2"), new Project("3"))), // depA имеет 3 проекта: 1, 2, 3
                new Employee("3", depB, List.of(new Project("1"))),
                new Employee("4", depB, List.of(new Project("4"), new Project("5"), new Project("6"))) // depB имеет 4 проекта: 1, 4, 5, 6
        );

        List<String> result = Streams1.findDepartmentsWithMostUniqueProjects(employees);

        assertEquals(List.of("depB"), result);
    }

    @Test
    @DisplayName("Streams2: Клиенты, купившие товары во всех категориях за период")
    void testFindClientsWithAllCategories() {
        LocalDateTime t1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2023, 1, 2, 10, 0);
        LocalDateTime t3 = LocalDateTime.of(2023, 1, 3, 10, 0);

        List<Transaction> transactions = List.of(
                new Transaction("ClientA", "Electronics", t1),
                new Transaction("ClientA", "Books", t2),
                new Transaction("ClientB", "Electronics", t3)
        );

        Set<String> required = Set.of("Electronics", "Books");
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 5, 0, 0);

        List<String> result = Streams2.findClientsWithAllCategories(transactions, required, start, end);

        assertEquals(List.of("ClientA"), result);
    }

    @Test
    @DisplayName("Streams3: Группировка заказов по клиенту и категории с суммой")
    void testGroupOrders() {
        List<Order> list = List.of(
                new Order("1", "Electronics", 1),
                new Order("1", "Food", 2),
                new Order("2", "Electronics", 3),
                new Order("2", "Food", 4),
                new Order("2", "Food", 4),
                new Order("3", "Electronics", 5),
                new Order("3", "Electronics", 5)
        );

        Map<String, Map<String, Integer>> result = Streams3.groupOrders(list);

        assertEquals(1, result.get("1").get("Electronics"));
        assertEquals(2, result.get("1").get("Food"));
        assertEquals(3, result.get("2").get("Electronics"));
        assertEquals(8, result.get("2").get("Food"));
        assertEquals(10, result.get("3").get("Electronics"));
    }

    @Test
    @DisplayName("Streams4: Фильтрация студентов по minGrade и сортировка в обратном порядке")
    void testFilterAndSortStudents() {
        Streams4 streams4 = new Streams4();
        List<Student> students = List.of(
                new Student("Alice", List.of(4, 5, 5)),
                new Student("Bob", List.of(3, 4, 5)),      // есть оценка < 4
                new Student("Charlie", List.of(4, 4, 4))
        );

        List<String> result = streams4.filterAndSortStudents(students, 4);

        assertEquals(List.of("Charlie", "Alice"), result);
    }

    @Test
    @DisplayName("Streams5: Вычисление факториалов для чисел")
    void testCalculateFactorials() {
        Streams5 streams5 = new Streams5();
        List<Integer> numbers = List.of(0, 1, 3, 5);

        Map<Integer, Long> result = streams5.calculateFactorials(numbers);

        assertEquals(1L, result.get(0));
        assertEquals(1L, result.get(1));
        assertEquals(6L, result.get(3));
        assertEquals(120L, result.get(5));
    }

    @Test
    @DisplayName("Streams6: Поиск измененных версий в Data")
    void testFindEditedData() {
        List<Data> data1 = List.of(
                new Data("22", "1"),
                new Data("11", "3"),
                new Data("33", "1")
        );
        List<Data> data2 = List.of(
                new Data("11", "3"),
                new Data("22", "3"), // изменилась
                new Data("33", "2")  // изменилась
        );

        Collection<String> result = Streams6.findEditedData(data1, data2);

        assertTrue(result.containsAll(List.of("22", "33")));
        assertFalse(result.contains("11"));
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Streams7: Группировка строк по длине с уникальностью и сортировкой")
    void testGroupByLengthSortedUnique() {
        Streams7 streams7 = new Streams7();
        List<String> input = List.of("bb", "aa", "ccc", "aa", "d", "bbb");

        Map<Integer, List<String>> result = streams7.groupByLengthSortedUnique(input);

        assertEquals(List.of("d"), result.get(1));
        assertEquals(List.of("aa", "bb"), result.get(2)); // уникальные и по алфавиту
        assertEquals(List.of("bbb", "ccc"), result.get(3));
    }
}
