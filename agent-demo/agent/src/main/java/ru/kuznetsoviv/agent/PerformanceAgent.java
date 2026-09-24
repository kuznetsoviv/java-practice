package ru.kuznetsoviv.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;

public class PerformanceAgent {

    private static final Logger LOG = LoggerFactory.getLogger(PerformanceAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        LOG.info("premain method started");
        instrument(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        LOG.info("agentmain method started");
        instrument(agentArgs, inst);
    }

    private static void instrument(String classAndMethodName, Instrumentation instrumentation) {
        if (classAndMethodName == null || classAndMethodName.isBlank()) {
            throw new IllegalArgumentException("ClassAndMethod is blank");
        }
        String[] entry = classAndMethodName.split("#");
        Class<?> clazz = getClass(entry[0], instrumentation);
        if (clazz != null) {
            try {
                PerformanceTransformer dt = new PerformanceTransformer(clazz, entry[1]);
                instrumentation.addTransformer(dt, true);
                instrumentation.retransformClasses(clazz);
            } catch (UnmodifiableClassException e) {
                throw new IllegalStateException(e);
            }
        } else {
            PerformanceTransformer dt = new PerformanceTransformer(entry[0], entry[1]);
            instrumentation.addTransformer(dt, true);
        }
    }

    private static Class<?> getClass(String className, Instrumentation instrumentation) {
        return Arrays.stream(instrumentation.getAllLoadedClasses())
                .filter(clazz -> clazz.getName().equals(className))
                .findFirst()
                .orElse(null);

    }

}
