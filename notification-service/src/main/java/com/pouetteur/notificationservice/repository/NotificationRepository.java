package com.pouetteur.notificationservice.repository;

import com.pouetteur.notificationservice.modele.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Trouver toutes les notifications
    List<Notification> findAll();

    // Trouver une notification par son ID
    Optional<Notification> findById(String id);

    // Sauvegarder une nouvelle notification
    Notification save(Notification notification);

    // Supprimer une notification par son ID
    void deleteById(String id);


    List<Notification> findByNotifieId(String notifieId);
}

