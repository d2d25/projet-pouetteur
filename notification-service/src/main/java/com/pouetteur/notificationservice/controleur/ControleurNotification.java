package com.pouetteur.notificationservice.controleur;

import com.pouetteur.notificationservice.modele.DTO.NotificationDTO;
import com.pouetteur.notificationservice.modele.DTO.response.CreateNotificationResponse;
import com.pouetteur.notificationservice.modele.Member;
import com.pouetteur.notificationservice.repository.NotificationRepository;
import com.pouetteur.notificationservice.service.NotificationService;
import com.pouetteur.notificationservice.service.exception.NotificationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class ControleurNotification {

    private final NotificationService notificationService;
    private final Logger logger = LoggerFactory.getLogger(ControleurNotification.class);

    @Autowired
    private NotificationRepository notificationRepository;

    public ControleurNotification(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateNotificationResponse> createNotification(@RequestBody Map<String, Object> requestData){
        System.out.println("Creating notification: "+ requestData);
        CreateNotificationResponse notificationDto = notificationService.createNotification(requestData);
        System.out.println("Notification created: {}"+ notificationDto);
        CreateNotificationResponse createNotificationResponse = new CreateNotificationResponse(notificationDto.getId());
        return new ResponseEntity<>(createNotificationResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable String notificationId) throws NotificationNotFoundException {
        System.out.println("Getting notification with ID: {} "+notificationId);
        NotificationDTO notification = notificationService.getNotification(notificationId);
        System.out.println("Notification Controleur found: {} "+ notification);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @PreAuthorize("#member.id == @notificationRepository.findById(#notificationId).orElse(null).notifie.id")
    @PutMapping("/{notificationId}")
    public ResponseEntity<NotificationDTO> updateNotification(@AuthenticationPrincipal Member member, @PathVariable String notificationId,
                                                              @RequestBody NotificationDTO notificationDTO) throws NotificationNotFoundException {
        System.out.println("Member ID: " + member.getId());
        System.out.println("Notification Member ID: " + notificationRepository.findById(notificationId).orElse(null).getNotifie().getId());
        logger.info("Updating notification with ID: {} and member: {}", notificationId, member);
        NotificationDTO notification = notificationService.updateNotification(notificationId, notificationDTO);
        logger.info("Notification updated: {}", notification);
        return new ResponseEntity<>(notification, HttpStatus.OK);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        logger.info("Deleting notification with ID: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        logger.info("Notification deleted");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("#member.id == #notifieId")
    @GetMapping("/notifie/{notifieId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByNotifieId(@AuthenticationPrincipal Member member, @PathVariable String notifieId) {
        System.out.println("Getting notifications for notifieId: {}"+ notifieId+ " and member: {}"+ member);
        List<NotificationDTO> notifications = notificationService.getNotificationsByNotifieId(notifieId);
        System.out.println("Notifications found: "+ notifications);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<String> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        logger.error("NotificationNotFoundException: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
