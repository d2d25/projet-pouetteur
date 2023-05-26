package com.pouetteur.notificationservice.modele.DTO;

import com.pouetteur.notificationservice.modele.Notification;

import java.util.Date;
import java.util.Objects;

public class NotificationDTO {

    private String id;
    private String title;
    private String body;
    private String type;
    private Object notifiant;
    private Boolean is_read;
    private Date created_at;
    private MemberDTO notifie;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.type = notification.getType();
        this.notifiant = notification.getNotifiant();
        this.is_read = notification.getIs_read();
        this.created_at = notification.getCreated_at();
        this.notifie = new MemberDTO(notification.getNotifie());
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public Object getNotifiant() {
        return notifiant;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public MemberDTO getNotifie() {
        return notifie;
    }

    public void setNotifiant(Object notifiant) {
        this.notifiant = notifiant;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", is_read=" + is_read +
                ", created_at=" + created_at +
                ", notifie=" + notifie +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(is_read, that.is_read) &&
                Objects.equals(created_at, that.created_at) &&
                Objects.equals(notifie, that.notifie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, is_read, created_at, notifie);
    }
}
