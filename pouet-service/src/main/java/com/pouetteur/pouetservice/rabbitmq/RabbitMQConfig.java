package com.pouetteur.pouetservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final String QUEUE = "auth-service-queue";
    private static final String EXCHANGE = "auth-service-exchange";
    private static final String ROUTING_KEY = "auth-service-routing-key";

    private static final String EXCHANGE_PROFILE = "profile-service-exchange";
    private static final String QUEUE_PROFILE = "profile-service-queue";
    private static final String ROUTING_KEY_PROFILE = "profile-service-routing-key";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public TopicExchange exchangeProfile(){
        return new TopicExchange(EXCHANGE_PROFILE);
    }

    @Bean
    public Queue routingKey() {
        return new Queue(ROUTING_KEY, true);
    }

    @Bean
    public Queue queueProfile() {
        return new Queue(QUEUE_PROFILE, true);
    }

    @Bean
    public Queue routingKeyProfile() {
        return new Queue(ROUTING_KEY_PROFILE, true);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingProfile(){
        return BindingBuilder
                .bind(queueProfile())
                .to(exchangeProfile())
                .with(ROUTING_KEY_PROFILE);
    }


}