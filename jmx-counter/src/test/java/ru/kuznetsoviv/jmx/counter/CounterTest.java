package ru.kuznetsoviv.jmx.counter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class CounterTest {

    @Test
    void exposesCountAndOperationsThroughJmx() throws Exception {
        MBeanServer server = MBeanServerFactory.newMBeanServer();
        ObjectName name = new ObjectName("example:type=Counter");

        try {
            server.registerMBean(new Counter(), name);
            server.invoke(name, "increment", new Object[0], new String[0]);
            server.invoke(name, "increment", new Object[0], new String[0]);
            Assertions.assertEquals(2, server.getAttribute(name, "Count"));

            server.invoke(name, "reset", new Object[0], new String[0]);
            Assertions.assertEquals(0, server.getAttribute(name, "Count"));
        } finally {
            server.unregisterMBean(name);
        }
    }

}
