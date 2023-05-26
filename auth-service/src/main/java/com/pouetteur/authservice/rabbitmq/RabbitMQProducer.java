package com.pouetteur.authservice.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.authservice.dto.CommunityDTO;
import com.pouetteur.authservice.dto.MemberToMessageDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

    private static final String EXCHANGE = "auth-service-exchange";
    private static final String ROUTING_KEY = "auth-service-routing-key";

    private static final String EXCHANGE_PROFILE = "profile-service-exchange";
    private static final String ROUTING_KEY_PROFILE = "profile-service-routing-key";

    private final ModelMapper modelMapper;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.modelMapper = new ModelMapper();
    }

    public void send(User user) {
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());
        System.out.println("Sending message to queue: " + userDTO);
        try {
            String userDTOJson = objectMapper.writeValueAsString(userDTO);
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, userDTOJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Member member) {
        MemberToMessageDTO memberDTO = modelMapper.map(member, MemberToMessageDTO.class);
        System.out.println("Sending message to queue: " + memberDTO);
        try {
            String userDTOJson = objectMapper.writeValueAsString(memberDTO);
            rabbitTemplate.convertAndSend(EXCHANGE_PROFILE, ROUTING_KEY_PROFILE, userDTOJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotifAbonnement(Member member) {
        MemberToMessageDTO memberDTO = modelMapper.map(member, MemberToMessageDTO.class);
        System.out.println("Sending message to queue: " + memberDTO);
        try {
            String userDTOJson = objectMapper.writeValueAsString(memberDTO);
            rabbitTemplate.convertAndSend("notif-"+EXCHANGE_PROFILE, "notif-"+ROUTING_KEY_PROFILE, userDTOJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Community community) {
        CommunityDTO communityDTO = modelMapper.map(community, CommunityDTO.class);
        System.out.println("Sending message to queue: " + communityDTO);
        try {
            String userDTOJson = objectMapper.writeValueAsString(communityDTO);
            rabbitTemplate.convertAndSend(EXCHANGE_PROFILE, ROUTING_KEY_PROFILE, userDTOJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}