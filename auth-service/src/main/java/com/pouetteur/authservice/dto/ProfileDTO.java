package com.pouetteur.authservice.dto;

import com.pouetteur.authservice.model.Reaction;
import com.pouetteur.authservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO implements Cloneable{

    private String id;
    private String username;
    private String name;
    private Date creationDate;
    private String profilePhoto;
    private String profileBanner;
    private String bio;
    private List<ProfileDTO> followings;
    private List<ProfileDTO> followers;
    private Map<Reaction, Integer> reactions;
    private String email;
    private List<Role> roles;
    private String birthdate;
    private List<ProfileDTO> members;
    private ProfileDTO owner;
    private String classType;

    @Override
    public ProfileDTO clone() {
        try {
            return (ProfileDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
