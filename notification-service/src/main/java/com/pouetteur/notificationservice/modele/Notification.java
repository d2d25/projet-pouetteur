package com.pouetteur.notificationservice.modele;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "notification")
public  class Notification {

    public static final String TYPE_ABONNEMENT = "abonnement";
    public static final String TYPE_REACTION = "reaction";

    @Id
    private String id;
    private String title;
    private String body;
    private String type;
    private Object notifiant;
    private Boolean is_read;
    private Date created_at;
    private Member notifie;

    public Notification() {
        // Constructeur vide pour les opérations de mongoDB
    }

    public Notification(String title, String body, String type, Object notifiant, Member notifie) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.notifiant = notifiant;
        this.is_read = false;
        this.created_at = new Date();
        this.notifie = notifie;
    }

    // Getters et setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Member getNotifie() {
        return notifie;
    }

    public void setNotifie(Member notifie) {
        this.notifie = notifie;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getNotifiant() {
        return notifiant;
    }

    public void setNotifiant(Object notifiant) {
        this.notifiant = notifiant;
    }
    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", is_read=" + is_read +
                ", created_at=" + created_at +
                ", notifie=" + notifie +
                '}';
    }
}