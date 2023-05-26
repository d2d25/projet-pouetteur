package com.pouetteur.messagingservice.modele.exceptions;

public class AtLeast2MembersException extends Exception {
    public AtLeast2MembersException(String conversationId) {
        super("Conversation " + conversationId + " must have at least 2 members");
    }
}
