package com.pouetteur.notificationservice.modele;

public class MemberV {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    private String username;

    public MemberV() {
    }

    public MemberV(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
