package ru.kuznetsoviv;


import org.junit.jupiter.api.Test;
import ru.kuznetsoviv.offheap.comparator.CQSort;

public class CQSortTest {

    @Test
    void testCQsort() throws Throwable {
        int[] actual = {0, 9, 3, 4, 6, 5, 1, 8, 2, 7};
        int[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sortedArray = CQSort.qsortTest(actual);
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, sortedArray);
    }

}
