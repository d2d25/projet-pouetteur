package com.pouetteur.notificationservice.modele.DTO.response;

public class CreateNotificationResponse {
    private String id;

    public CreateNotificationResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

