package com.pouetteur.notificationservice.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.notificationservice.modele.DTO.MemberDTO;
import com.pouetteur.notificationservice.modele.DTO.NotificationDTO;
import com.pouetteur.notificationservice.modele.DTO.PouetDTO;
import com.pouetteur.notificationservice.service.NotificationServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMQConsumer {

    //private static final String QUEUE_PROFILE = "profile-service-queue";
    private static final String QUEUE_POUET = "pouet-service-queue";
    @Autowired
    private NotificationServiceImpl notificationService;

    private ObjectMapper objectMapper;

    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.objectMapper = new ObjectMapper();
    }
/*
    @RabbitListener(queues = QUEUE_PROFILE)
    public void receiveAbonnement(String message) {
        NotificationDTO notificationDTO = new NotificationDTO();
        try {
            MemberDTO memberDTO = this.objectMapper.readValue(message, MemberDTO.class);

            //Construction du DTO de notification
            String title = "Nouveau follower !";
            String notifieId = memberDTO.getId();
            String body = notifieId+" s'est abonné à votre profil.";
            String type = "abonnement";

            notificationDTO.setNotifiant(memberDTO);
            this.notificationService.createNotification(Map.of("title", title, "body", body, "type", type, "notifieId", notifieId, "notifiantMember", memberDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received message from queue: " + message);
    }

 */
    @RabbitListener(queues = QUEUE_POUET)
    public void receiveReaction(String message) {
        NotificationDTO notificationDTO = new NotificationDTO();
        try {
            PouetDTO pouetDTO = this.objectMapper.readValue(message, PouetDTO.class);

            //Construction du DTO de notification

            String title = "Nouvelle réaction à votre pouet";
            String notifieId = pouetDTO.getAuthor().getId();
            String body = notifieId+" a réagit à votre pouet.";
            String type = "reaction";
            notificationDTO.setNotifiant(pouetDTO);
            this.notificationService.createNotification(Map.of("title", title, "body", body, "type", type, "notifieId", notifieId, "notifiantPouet", pouetDTO));

             } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received message from queue: " + message);
    }
}
