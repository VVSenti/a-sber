package org.learning.streams;

import java.util.List;

/**
 * Задача 1: Найти отделы с максимальным количеством уникальных проектов. Пример: Вход: Сотрудники с проектами [{DeptA, [P1, P2]}, {DeptA, [P2, Р3]},
 * {DeptB, [P1]}] Выход: ["DeptA"] (y DeptA 3 уникальных проекта, у DeptB - 1).
 */
public record Employee(String name, Department department, List<Project> projects) {

}
