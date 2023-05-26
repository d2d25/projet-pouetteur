package com.pouetteur.messagingservice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.service.MessageServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    private static final String QUEUE_PROFILE = "profile-service-queue";
    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    private MessageServiceImpl messageService;

    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = QUEUE_PROFILE)
    public String receiveMessage(String message) {
        System.out.println("Received message from queue: " + message);
        try {
            MemberDTO memberDTO = objectMapper.readValue(message, MemberDTO.class);
            System.out.println("Received message from queue: " + memberDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
