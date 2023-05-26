package com.pouetteur.pouetservice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.pouetservice.modele.Pouet;
import com.pouetteur.pouetservice.modele.dtos.PouetDTO;
import com.pouetteur.pouetservice.modele.dtos.PouetToNotifDTO;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private static final String EXCHANGE = "pouet-service-exchange";
    private static final String ROUTING_KEY = "pouet-service-routing-key";

    private final ModelMapper modelMapper;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.modelMapper = new ModelMapper();
    }

    public void sendNotif(Pouet pouet) {
        PouetToNotifDTO pouetDTO = modelMapper.map(pouet, PouetToNotifDTO.class);
        System.out.println("Sending message to queue: " + pouetDTO);
        try {
            String userDTOJson = objectMapper.writeValueAsString(pouetDTO);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, userDTOJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
