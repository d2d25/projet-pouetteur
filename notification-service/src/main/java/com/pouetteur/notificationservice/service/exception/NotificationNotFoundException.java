package com.pouetteur.notificationservice.service.exception;

public class NotificationNotFoundException extends Exception {
    public NotificationNotFoundException() {
        super("Notification not found");
    }

    public NotificationNotFoundException(String message) {
        super(message);
    }
}
