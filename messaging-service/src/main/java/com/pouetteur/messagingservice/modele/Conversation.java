package com.pouetteur.messagingservice.modele;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data @Getter @Setter
@NoArgsConstructor
@Document(collection = "conversation")
public class Conversation {
    private static final String DEFAULT_PHOTO = "default.png";
    private static final String DEFAULT_PHOTO_FOR_2 = "";
    @Id
    @Generated
    private String id;
    @NonNull
    private String name;
    private String photo;
    //    @Size(min = 2)
    private Set<Member> members;
    private List<Message> messages;

    public Conversation(String name, List<Member> members) {
        this.name = name;
        this.members = new HashSet<>();
        this.messages = new ArrayList<>();
        this.photo = DEFAULT_PHOTO;
        this.addMembers(members);

    }
    //TODO verify_photo()
    public void addMember(Member member) {
        this.members.add(member);
        if (this.members.size() == 2) {
            this.photo = DEFAULT_PHOTO_FOR_2;
        } else if (this.photo.equals(DEFAULT_PHOTO_FOR_2)){
            this.photo = DEFAULT_PHOTO;
        }
    }
    public void addMembers(List<Member> members) {
        this.members.addAll(members);
        if (this.members.size() == 2) {
            this.photo = DEFAULT_PHOTO_FOR_2;
        } else if (this.photo.equals(DEFAULT_PHOTO_FOR_2)){
            this.photo = DEFAULT_PHOTO;
        }
    }
    public void addMessage(Message message) {
        this.messages.add(message);
    }
    public void removeMember(Member member) {
        this.members.remove(member);
    }

    public Message getLastMessage() {
        return this.messages.get(this.messages.size() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(id, that.id) && name.equals(that.name) && Objects.equals(photo, that.photo) && Objects.equals(members, that.members) && Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, photo, members, messages);
    }
}
