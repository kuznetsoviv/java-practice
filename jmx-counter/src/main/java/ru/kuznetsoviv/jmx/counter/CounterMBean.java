package ru.kuznetsoviv.jmx.counter;

public interface CounterMBean {

    int getCount();

    void increment();

    void reset();

}
