package com.pouetteur.notificationservice.config;
/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQErrorHandler implements FatalExceptionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQErrorHandler.class);

    @Override
    public boolean isFatal(Throwable t) {
        LOGGER.error("Error occurred in RabbitMQ Listener", t);
        if (!(t instanceof ListenerExecutionFailedException)) {
            return false;
        }
        Throwable cause = t.getCause();
        if (cause instanceof AmqpRejectAndDontRequeueException) {
            return true;
        }
        return false;
    }

}*/