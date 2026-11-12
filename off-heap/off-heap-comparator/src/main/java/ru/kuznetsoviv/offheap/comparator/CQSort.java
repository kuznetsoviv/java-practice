package ru.kuznetsoviv.offheap.comparator;


import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static java.lang.foreign.ValueLayout.JAVA_INT;

public class CQSort {

    static class Qsort {
        static int qsortCompare(MemorySegment elem1, MemorySegment elem2) {
            return Integer.compare(elem1.get(JAVA_INT, 0), elem2.get(JAVA_INT, 0));
        }
    }

    private static Linker LINKER = Linker.nativeLinker();

    public static int[] qsortTest(int[] unsortedArray) throws Throwable {
        int[] sorted;

        MethodHandle qsort = LINKER.downcallHandle(
                LINKER.defaultLookup().find("qsort").get(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS,
                        ValueLayout.JAVA_LONG,
                        ValueLayout.JAVA_LONG,
                        ValueLayout.ADDRESS
                )
        );
        MethodHandle comparHandle = MethodHandles.lookup()
                .findStatic(
                        Qsort.class,
                        "qsortCompare",
                        MethodType.methodType(
                                int.class,
                                MemorySegment.class,
                                MemorySegment.class
                        )
                );
        FunctionDescriptor qsortCompareDesc = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS.withTargetLayout(ValueLayout.JAVA_INT),
                ValueLayout.ADDRESS.withTargetLayout(JAVA_INT)
        );

        MemorySegment compareFunc = LINKER.upcallStub(
                comparHandle,
                qsortCompareDesc,
                Arena.ofAuto()
        );

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment array = arena.allocateFrom(
                    ValueLayout.JAVA_INT,
                    unsortedArray
            );

            qsort.invoke(
                    array,
                    (long) unsortedArray.length,
                    ValueLayout.JAVA_INT.byteSize(),
                    compareFunc
            );
            sorted = array.toArray(ValueLayout.JAVA_INT);
        }
        return sorted;
    }

}
