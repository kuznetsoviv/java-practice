package ru.kuznetsoviv.bytebuddy.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kuznetsoviv.bytebuddy.demo.component.GreetingComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GreetingComponentTest {

    @Autowired
    private GreetingComponent greetingService;

    @Test
    void shouldReturnGreetingForName() {
        String result = greetingService.greet("Alice");

        assertEquals("Hello, Alice!", result);
    }

}
