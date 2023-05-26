package com.pouetteur.messagingservice.service;

import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MessageDTO;
import com.pouetteur.messagingservice.service.exceptions.NotFoundException;
import com.pouetteur.messagingservice.service.exceptions.UnAuthorizedException;
import org.springframework.security.core.Authentication;

public interface IMessageService {

    /**
     * send a message to a conversation
     * @param idConversation the id of the conversation
     * @param text the text of the message
     * @param authentication the authentication of the member who send the message
     * @return the conversation updated
     * @throws NotFoundException if the conversation doesn't exist
     * @throws UnAuthorizedException if the member is not in the conversation
     */
    ConversationDTO sendMessage(String idConversation, String text, Authentication authentication) throws NotFoundException, UnAuthorizedException;

    /**
     * get a message
     * @param idConversation the id of the conversation
     * @param idMessage the id of the message
     * @param authentication the authentication of the member who want to get the message
     * @return the message
     * @throws NotFoundException if the message doesn't exist
     * @throws UnAuthorizedException if the member is not in the conversation
     */
    MessageDTO getMessage(String idConversation, String idMessage, Authentication authentication) throws NotFoundException, UnAuthorizedException;

    /**
     * delete a message
     * @param idConversation the id of the conversation
     * @param idMessage the id of the message
     * @param authentication the authentication of the member who want to delete the message
     * @return the conversation updated (without the message)
     * @throws NotFoundException if the message doesn't exist or if the conversation doesn't exist
     * @throws UnAuthorizedException if the member is not the author of the message
     */
    ConversationDTO deleteMessage(String idConversation, String idMessage, Authentication authentication) throws NotFoundException, UnAuthorizedException;

}
