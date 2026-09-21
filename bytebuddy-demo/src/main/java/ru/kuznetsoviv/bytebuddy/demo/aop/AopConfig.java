package ru.kuznetsoviv.bytebuddy.demo.aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {

    @Bean
    public Advisor advisor(LoggingInterceptor interceptor) {
        return new DefaultPointcutAdvisor(new LogPointCut(), interceptor);
    }
}
