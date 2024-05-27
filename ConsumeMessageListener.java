package com.test.springBootrabbitmqconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConsumeMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private Integer MessageCount = 0;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MQConfig.MESSAGE_QUEUE)
    public void receiveMessage(CustomMessage customMessage) {
        try {
            System.out.println(MessageCount);
            if(MessageCount >= 5)
            {
                Thread.sleep(5000);
                MessageCount = 0;
            }
            MessageCount+=1;
            System.out.println(customMessage);
        } catch (InterruptedException e)
        {
            logger.error("Thread interrupted while putting message into queue", e);
        }
    }

}
