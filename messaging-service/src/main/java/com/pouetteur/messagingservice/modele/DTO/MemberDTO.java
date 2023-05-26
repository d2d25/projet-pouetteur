package com.pouetteur.messagingservice.modele.DTO;
import com.pouetteur.messagingservice.modele.Member;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDTO {
    String id;
    String username;
    String name;
    String profilePhoto;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.name = member.getName();
        this.profilePhoto = member.getProfilePhoto();
    }


}
