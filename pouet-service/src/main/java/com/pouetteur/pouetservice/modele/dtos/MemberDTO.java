package com.pouetteur.pouetservice.modele.dtos;

import com.pouetteur.pouetservice.modele.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDTO {
    private String id;
    private String username;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }

}
