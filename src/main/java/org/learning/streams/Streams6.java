package org.learning.streams;

import java.util.Collection;
import java.util.List;

public class Streams6 {

    public static void main(String[] args) {
        List<Data> data = List.of(
            new Data("22", "1"),
            new Data("11", "3"),
            new Data("33", "1"));
        List<Data> data2 = List.of(
            new Data("11", "3"),
            new Data("22", "3"),
            new Data("33", "2"));
        System.out.println(findEditedData(data, data2));
    }

    //Реализовать метод, который сравнит два List<Data> и выведет в консоль code тех Data, в которых изменились version.
    //Известно, что приходящие на вход списки всегда имеют одинаковые длину и одинаковые code y Data,
    //однако version и порядок элементов могут отличаться.

    public static Collection<String> findEditedData(List<Data> data, List<Data> data2) {
        return null;
    }
}
