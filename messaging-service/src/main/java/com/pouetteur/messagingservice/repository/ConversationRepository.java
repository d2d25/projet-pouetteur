package com.pouetteur.messagingservice.repository;

import com.pouetteur.messagingservice.modele.Conversation;
import com.pouetteur.messagingservice.modele.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByMembers(Member member);
}
