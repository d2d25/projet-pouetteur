package com.pouetteur.messagingservice.service.exceptions;

public class AlreadyThereException extends Throwable {
    public AlreadyThereException(String memberAlreadyInConversation) {
        super(memberAlreadyInConversation);
    }
}
