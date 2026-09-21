package ru.kuznetsoviv.bytebuddy.demo.logger;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodCall;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import ru.kuznetsoviv.bytebuddy.demo.aop.LogPointCut;
import ru.kuznetsoviv.bytebuddy.demo.aop.LoggingInterceptor;
import ru.kuznetsoviv.bytebuddy.demo.logger.annotation.OurLogger;

import java.lang.reflect.Modifier;

import static net.bytebuddy.implementation.FieldAccessor.ofField;

@Configuration
public class LoggerBeanPostProcessor implements BeanPostProcessor {

    private final ByteBuddy byteBuddy = new ByteBuddy();

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = ClassUtils.getUserClass(bean.getClass());
        if (hasLoggerAnnotation(targetClass)) {
            return createTargetObject(createTargetClass(targetClass));
        }
        return bean;
    }


    private <T> Class<? extends T> createTargetClass(Class<T> targetClass) {
        try {
            return byteBuddy
                    .subclass(targetClass)
                    .name(targetClass.getCanonicalName() + "WithLogger")
                    .modifiers(Visibility.PUBLIC)
                    .defineField(
                            "LOGGER",
                            org.slf4j.Logger.class,
                            Modifier.PRIVATE | Modifier.FINAL
                    )
                    .defineConstructor(Visibility.PUBLIC)
                    .withParameters(org.slf4j.Logger.class)
                    .intercept(
                            MethodCall
                                    .invoke(targetClass.getConstructor())
                                    .andThen(ofField(getTargetClassLoggerAnnotation(targetClass).value()).setsArgumentAt(0))
                    )
                    .make()
                    .load(
                            targetClass.getClassLoader(),
                            ClassLoadingStrategy.Default.INJECTION
                    )
                    .getLoaded();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createTargetObject(Class<? extends T> targetClassWithLogger) {
        try {
            return targetClassWithLogger
                    .getConstructor(org.slf4j.Logger.class)
                    .newInstance(org.slf4j.LoggerFactory.getLogger(targetClassWithLogger));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object createProxyTargetObject(Object targetObject) {
        Advisor logAdvisor = createLogAdvisor();
        return createAopProxyWithAdvisor(targetObject, logAdvisor);
    }

    private Advisor createLogAdvisor() {
        Pointcut logPointCut = new LogPointCut();
        Advice logAdvice = new LoggingInterceptor();
        return new DefaultPointcutAdvisor(logPointCut, logAdvice);
    }

    private Object createAopProxyWithAdvisor(Object targetObject, Advisor advisor) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(targetObject);
        proxyFactory.addAdvisor(advisor);
        return proxyFactory.getProxy();
    }

    private boolean hasLoggerAnnotation(Class<?> targetClass) {
        return getTargetClassLoggerAnnotation(targetClass) != null;
    }

    private <T> OurLogger getTargetClassLoggerAnnotation(Class<T> targetClass) {
        return AnnotationUtils.getAnnotation(targetClass, OurLogger.class);
    }


}
