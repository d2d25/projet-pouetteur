package com.pouetteur.messagingservice.modele.DTO;

import com.pouetteur.messagingservice.modele.Conversation;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ConversationDTO {
    private String id;
    private String name;
    private String photo;
    private List<MemberDTO> members;
    private List<MessageDTO> messages;

    public ConversationDTO(Conversation conversation) {
        this.id = conversation.getId();
        this.name = conversation.getName();
        this.photo = conversation.getPhoto();
        this.members = conversation.getMembers().stream().map(MemberDTO::new).toList();
        this.messages = conversation.getMessages().stream().map(MessageDTO::new).toList();
    }
}
