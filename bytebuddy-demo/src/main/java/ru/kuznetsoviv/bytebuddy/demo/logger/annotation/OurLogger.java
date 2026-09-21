package ru.kuznetsoviv.bytebuddy.demo.logger.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OurLogger {
    String value() default "LOGGER";
}
