package ru.kuznetsoviv.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ApplicationWithLogging {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationWithLogging.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting application with logging");
        Random random = new Random(Instant.now().getEpochSecond());
        while (true) {
            int data = random.nextInt();
            LOG.info("Process data: {}", data);
            process(data);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void process(int data) throws InterruptedException {
        long startTime = System.nanoTime();
        TimeUnit.SECONDS.sleep(2);
        LOG.info("Successful operation: {}", data);
        long endTime = System.nanoTime();
        long opTime = (endTime - startTime) / (long) 1000;
        LOG.info("[My LOG] Withdrawal operation completed in: {} microSeconds", opTime);
    }

}
