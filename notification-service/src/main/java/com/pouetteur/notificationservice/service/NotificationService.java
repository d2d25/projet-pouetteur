package com.pouetteur.notificationservice.service;

import com.pouetteur.notificationservice.modele.DTO.NotificationDTO;
import com.pouetteur.notificationservice.modele.DTO.response.CreateNotificationResponse;
import com.pouetteur.notificationservice.service.exception.NotificationNotFoundException;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    CreateNotificationResponse createNotification(Map<String, Object> requestData);
    NotificationDTO getNotification(String notificationId) throws NotificationNotFoundException;
    NotificationDTO updateNotification(String notificationId, NotificationDTO notificationDTO) throws NotificationNotFoundException;
    void deleteNotification(String notificationId);
    List<NotificationDTO> getNotificationsByNotifieId(String notifieId);
}
