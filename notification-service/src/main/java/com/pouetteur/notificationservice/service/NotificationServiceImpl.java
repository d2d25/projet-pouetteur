package com.pouetteur.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.notificationservice.modele.DTO.MemberDTO;
import com.pouetteur.notificationservice.modele.DTO.NotificationDTO;
import com.pouetteur.notificationservice.modele.DTO.PouetDTO;
import com.pouetteur.notificationservice.modele.DTO.response.CreateNotificationResponse;
import com.pouetteur.notificationservice.modele.Member;
import com.pouetteur.notificationservice.modele.Notification;
import com.pouetteur.notificationservice.modele.Pouet;
import com.pouetteur.notificationservice.repository.NotificationRepository;
import com.pouetteur.notificationservice.service.exception.NotificationNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public CreateNotificationResponse createNotification(Map<String, Object> requestData) {
        String type = (String) requestData.get("type");
        Notification notification;
        Member notifie = new Member((String) requestData.get("notifieId"));

        ObjectMapper objectMapper = new ObjectMapper();
        if (Notification.TYPE_REACTION.equals(type)) {
            PouetDTO pouetDTO = objectMapper.convertValue(requestData.get("notifiantPouet"), PouetDTO.class);
            Pouet notifiantPouet = modelMapper.map(pouetDTO, Pouet.class);
            notification = new Notification((String) requestData.get("title"), (String) requestData.get("body"), type, notifiantPouet, notifie);
            System.out.println("Notification created successfully avec reaction: "+ notification);
        } else if (Notification.TYPE_ABONNEMENT.equals(type)) {
            MemberDTO memberDTO = objectMapper.convertValue(requestData.get("notifiantMember"), MemberDTO.class);
            Member notifiantMember = modelMapper.map(memberDTO, Member.class);
            notification = new Notification((String) requestData.get("title"), (String) requestData.get("body"), type, notifiantMember, notifie);
            System.out.println("Notification created successfully avec abonnement: "+ notification);
        } else {
            throw new IllegalArgumentException("Invalid notification type");
        }

        notification.setIs_read(false);
        notification.setCreated_at(new Date());
        Notification savedNotification = notificationRepository.save(notification);
        System.out.println("Notification created successfully SAVED: "+ savedNotification);
        return new CreateNotificationResponse(savedNotification.getId());
    }


    @Override
    public NotificationDTO getNotification(String notificationId) throws NotificationNotFoundException {
        System.out.println("Getting notification with ID: {}"+ notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));
        System.out.println("Notification Service found: {}"+ notification);
        return modelMapper.map(notification, NotificationDTO.class);
    }

    @Override
    public NotificationDTO updateNotification(String notificationId, NotificationDTO notificationDTO) throws NotificationNotFoundException {
        System.out.println("Updating notification with ID: {}" + notificationId);
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));

        Notification updatedNotification = modelMapper.map(notificationDTO, Notification.class);
        updatedNotification.setId(existingNotification.getId());
        updatedNotification.setCreated_at(existingNotification.getCreated_at());
        updatedNotification.setIs_read(true);

        Member notifie = new Member();
        notifie.setId(notificationDTO.getNotifie().getId());
        updatedNotification.setNotifie(notifie);

        Notification savedNotification = notificationRepository.save(updatedNotification);
        System.out.println("Notification updated successfully: {}" + savedNotification);
        return modelMapper.map(savedNotification, NotificationDTO.class);
    }

    @Override
    public void deleteNotification(String notificationId) {
        logger.debug("Deleting notification with ID: {}", notificationId);
        notificationRepository.deleteById(notificationId);
        logger.debug("Notification deleted successfully");
    }

    @Override
    public List<NotificationDTO> getNotificationsByNotifieId(String Id) {
        logger.debug("Getting notifications for notifie ID: {}", Id);
        List<Notification> notifications = notificationRepository.findByNotifieId(Id);
        List<NotificationDTO> notificationDTOs = new ArrayList<>();

        for (Notification notification : notifications) {
            notificationDTOs.add(modelMapper.map(notification, NotificationDTO.class));
        }
        logger.debug("Notifications for notifie ID found: {}", notificationDTOs);
        return notificationDTOs;
    }
/*
    @RabbitListener(queues = "notificationQueue")
    public void processNotificationMessage(NotificationDTO notificationDTO) {
        LOGGER.info("Received notification message: {}", notificationDTO);
        try {
            createNotification(notificationDTO);
            LOGGER.info("Successfully processed notification message: {}", notificationDTO);
        } catch (Exception e) {
            LOGGER.error("Error processing notification message: {}", notificationDTO, e);
            throw e;
        }
    }*/

}
