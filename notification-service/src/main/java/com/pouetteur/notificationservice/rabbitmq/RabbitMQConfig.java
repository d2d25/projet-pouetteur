package com.pouetteur.notificationservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //private static final String QUEUE_PROFILE = "notif-profile-service-queue";
    //private static final String EXCHANGE_PROFILE = "notif-profile-service-exchange";
    //private static final String ROUTING_KEY_PROFILE = "notif-profile-service-routing-key";

    private static final String QUEUE_POUET = "pouet-service-queue";
    private static final String EXCHANGE_POUET = "pouet-service-exchange";
    private static final String ROUTING_KEY_POUET = "pouet-service-routing-key";
/*
    @Bean
    public Queue queueProfile() {
        return new Queue(QUEUE_PROFILE, true);
    }

    @Bean
    public TopicExchange exchangeProfile(){
        return new TopicExchange(EXCHANGE_PROFILE);
    }

    @Bean
    public Queue routingKeyProfile() {
        return new Queue(ROUTING_KEY_PROFILE, true);
    }
*/
    @Bean
    public Queue queuePouet() {
        return new Queue(QUEUE_POUET, true);
    }

    @Bean
    public TopicExchange exchangePouet(){
        return new TopicExchange(EXCHANGE_POUET);
    }

    @Bean
    public Queue routingKeyPouet() {
        return new Queue(ROUTING_KEY_POUET, true);
    }
/*
    @Bean
    public Binding bindingProfile(){
        return BindingBuilder
                .bind(queueProfile())
                .to(exchangeProfile())
                .with(ROUTING_KEY_PROFILE);
    }
*/
    @Bean
    public Binding bindingPouet(){
        return BindingBuilder
                .bind(queuePouet())
                .to(exchangePouet())
                .with(ROUTING_KEY_POUET);
    }


}
