package com.pouetteur.messagingservice.modele;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Data @Getter @Setter
@NoArgsConstructor
public class Message {
    @Id
    @Generated
    private String id;
    private Member author;
    private String body;
    private Date date;

    public Message(Member author, String body) {
        this.id = UUID.randomUUID().toString();
        this.author = author;
        this.body = body;
        this.date = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(author, message.author) && Objects.equals(body, message.body) && Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, body, date);
    }
}
