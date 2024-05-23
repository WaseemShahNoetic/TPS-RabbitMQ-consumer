package com.test.springBootrabbitmqconsumer;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MessageListener {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger messageCount = new AtomicInteger(0);
    private final long startTime = System.currentTimeMillis();


    @PostConstruct
    public void init() {
        startConsumerWithTimeout(1); // Set the timeout in seconds
    }

    public void startConsumerWithTimeout(int timeoutSeconds) {
        scheduledFuture = scheduler.schedule(this::stopConsumer, timeoutSeconds, TimeUnit.SECONDS);
    }


//    @PostConstruct
//    public void init() {
//        startConsumerWithTimeout(10000000); // Set the timeout in nanoseconds
//    }
//
//    public void startConsumerWithTimeout(long timeoutNanoseconds) {
//        scheduledFuture = scheduler.schedule(this::stopConsumer, timeoutNanoseconds, TimeUnit.NANOSECONDS);
//    }

    public void stopConsumer() {
        running.set(false);
        scheduler.shutdown();
        System.out.println("Consumer stopped after timeout.");
    }



    @RabbitListener(queues = MQConfig.MESSAGE_QUEUE)
    public void listener (CustomMessage customMessage){
        System.out.println("Received Message from Queue: "+customMessage);
        calculateTps();
    }


    private void calculateTps() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        if (elapsedTime > 0) {
            double tps = (messageCount.get() * 1000.0) / elapsedTime;
            System.out.println("TPS: " + tps);
        }
    }

    @PreDestroy
    public void shutdown() {
        stopConsumer();
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        System.out.println("Shutting down gracefully...");
    }

}
