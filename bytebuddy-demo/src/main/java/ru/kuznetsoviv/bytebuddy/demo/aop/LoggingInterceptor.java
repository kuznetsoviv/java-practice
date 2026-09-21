package ru.kuznetsoviv.bytebuddy.demo.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.kuznetsoviv.bytebuddy.demo.logger.annotation.OurLogger;

import java.lang.reflect.Field;
import java.util.Objects;

@Component
public class LoggingInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        OurLogger ourLoggerAnnotation = Objects.requireNonNull(invocation.getThis())
                .getClass().getSuperclass().getAnnotation(OurLogger.class);
        String targetObjectLoggerName = ourLoggerAnnotation.value();

        Object targetObject = invocation.getThis();
        Field loggerField = getLoggerField(targetObject, targetObjectLoggerName);

        Logger logger = (Logger) loggerField.get(targetObject);
        String methodName = invocation.getMethod().getName();
        logger.info("before log");
        logger.info("before method: {} invocation", methodName);
        Object result = invocation.proceed();
        logger.info("after method: {} invocation ", methodName);

        return result;
    }

    @NonNull
    private static Field getLoggerField(Object targetObject, String targetObjectLoggerName) {
        Class<?> targetClass = targetObject.getClass();
        Field loggerField;
        try {
            loggerField = targetClass.getDeclaredField(targetObjectLoggerName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("""
                        Target class: %s does not has Logger field: %s please set default name <%s> or set your
                    """.formatted(targetObject, targetObjectLoggerName, "LOGGER"));
        }

        loggerField.setAccessible(true);
        return loggerField;
    }
}
