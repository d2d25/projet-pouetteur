package com.pouetteur.pouetservice.modele.dtos;

import com.pouetteur.pouetservice.modele.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDTO {
    private String id;
    private String username;

    public ProfileDTO(Profile profile) {
        this.id = profile.getId();
        this.username = profile.getUsername();
    }
}
