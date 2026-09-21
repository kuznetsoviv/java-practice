package ru.kuznetsoviv.bytebuddy.demo.logger.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OurLog {
}
