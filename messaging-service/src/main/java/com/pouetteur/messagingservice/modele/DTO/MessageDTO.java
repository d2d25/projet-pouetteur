package com.pouetteur.messagingservice.modele.DTO;

import com.pouetteur.messagingservice.modele.Message;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class MessageDTO {
    private String id;
    private MemberDTO author;
    private String body;
    private Date date;

//    public MessageDTO() {
//        this.date = new Date();
//    }

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.author = new MemberDTO(message.getAuthor());
        this.body = message.getBody();
        this.date = message.getDate();
    }
}
