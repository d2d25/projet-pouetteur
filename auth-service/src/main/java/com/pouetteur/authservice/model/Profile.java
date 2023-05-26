package com.pouetteur.authservice.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;

import java.util.Date;


@MappedSuperclass
@Table(name = "profiles")
public interface Profile extends Cloneable {
    String getId();
    void setId(String id);
    String getUsername();
    void setUsername(String username);
    String getName();
    void setName(String name);
    Date getCreationDate();
    void setCreationDate(Date creationDate);
    String getProfilePhoto();
    void setProfilePhoto(String profilePhoto);
    String getProfileBanner();
    void setProfileBanner(String profileBanner);
    String getBio();
    void setBio(String bio);
}
