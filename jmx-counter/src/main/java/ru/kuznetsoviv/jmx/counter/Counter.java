package ru.kuznetsoviv.jmx.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter implements CounterMBean {

    private final AtomicInteger count = new AtomicInteger();

    @Override
    public int getCount() {
        return count.get();
    }

    @Override
    public void increment() {
        count.incrementAndGet();
    }

    @Override
    public void reset() {
        count.set(0);
    }

}
