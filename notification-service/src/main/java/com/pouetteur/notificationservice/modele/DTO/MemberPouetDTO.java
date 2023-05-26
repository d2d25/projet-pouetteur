package com.pouetteur.notificationservice.modele.DTO;

import com.pouetteur.notificationservice.modele.MemberV;

public class MemberPouetDTO {
    private String id;
    private String username;

    public MemberPouetDTO(MemberV member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
