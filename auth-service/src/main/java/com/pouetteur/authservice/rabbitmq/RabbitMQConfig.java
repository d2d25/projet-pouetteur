package com.pouetteur.authservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final String QUEUE = "pouet-service-queue";
    private static final String EXCHANGE = "pouet-service-exchange";
    private static final String ROUTING_KEY = "pouet-service-routing-key";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue routingKey() {
        return new Queue(ROUTING_KEY, true);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

}
