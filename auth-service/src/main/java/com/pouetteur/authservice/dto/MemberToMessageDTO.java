package com.pouetteur.authservice.dto;

import com.pouetteur.authservice.model.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberToMessageDTO {
    String id;
    String username;
    String name;
    String profilePhoto;

    public MemberToMessageDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.name = member.getName();
        this.profilePhoto = member.getProfilePhoto();
    }


}