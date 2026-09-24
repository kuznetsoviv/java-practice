package ru.kuznetsoviv.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting application");
        Random random = new Random(Instant.now().getEpochSecond());
        while (true) {
            int data = random.nextInt();
            LOG.info("Process data: {}", data);
            process(data);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void process(int data) throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        LOG.info("Successful operation: {}", data);
    }

}
