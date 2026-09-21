package ru.kuznetsoviv.bytebuddy.demo.aop;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import ru.kuznetsoviv.bytebuddy.demo.logger.annotation.OurLog;

import java.lang.reflect.Method;

public class LogPointCut extends StaticMethodMatcherPointcut {
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return method.getDeclaredAnnotation(OurLog.class) != null;
    }
}
