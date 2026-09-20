package ru.kuznetsoviv.bytebuddy.demo.component;

import org.springframework.stereotype.Component;
import ru.kuznetsoviv.bytebuddy.demo.logger.annotation.OurLog;
import ru.kuznetsoviv.bytebuddy.demo.logger.annotation.OurLogger;

@Component
@OurLogger
public class GreetingComponent {

    @OurLog
    public String greet(String name) {

        if (name == null || name.isBlank()) {
            return "Hello, World!";
        }

        return "Hello, " + name + "!";
    }

}
