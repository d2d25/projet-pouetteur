package com.pouetteur.messagingservice.service;

import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.modele.exceptions.AtLeast2MembersException;
import com.pouetteur.messagingservice.service.exceptions.*;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface IConversationService {

    /**
     * create a new conversation
     *
     * @param conversationDTO the conversation to create
     * @param authentication
     * @return the conversation created
     * @throws AtLeast2MembersException if the conversation has less than 2 members
     **/
    ConversationDTO createConversation(ConversationDTO conversationDTO, Authentication authentication) throws AtLeast2MembersException, MissingParametersException, UnAuthorizedException;

    /**
     * get a conversation
     *
     * @param idConversation the id of the conversation
     * @param authentication the authentication of the member
     * @return the conversation
     * @throws NotFoundException if the conversation doesn't exist
     **/
    ConversationDTO getConversation(String idConversation, Authentication authentication) throws NotFoundException, UnAuthorizedException;


    /**
     * get all conversations of a member
     * @param authentication the authentication of the member
     * @return the list of conversations of the member
     */
    List<ConversationDTO> getConversations(Authentication authentication) throws NotFoundException;

    /**
     * add members to a conversation
     *
     * @param idConversation the id of the conversation
     * @param membersDTO     a list of memberDTO to add
     * @param authentication the authentication of the member
     * @return the conversation updated
     * @throws NotFoundException          if the conversation doesn't exist
     * @throws MissingParametersException if the list of members is empty
     * @throws AlreadyThereException      if a member is already in the conversation
     **/
    ConversationDTO addMembers(String idConversation, List<MemberDTO> membersDTO, Authentication authentication) throws NotFoundException, MissingParametersException, AlreadyThereException, UnAuthorizedException;

    /**
     * remove a member from a conversation
     * @param idConversation the id of the conversation
     * @param idMember the id of the member to remove
     * @return the conversation updated
     * @throws NotFoundException if the conversation doesn't exist
     * @throws MissingParametersException if the id of the member is empty
     **/
    ConversationDTO removeMember(String idConversation, String idMember) throws NotFoundException, MissingParametersException;

    /**
     * edit the photo of a conversation
     *
     * @param idConversation the id of the conversation
     * @param photo          the new photo
     * @param authentication the authentication of the member
     * @return the conversation updated
     * @throws NotFoundException if the conversation doesn't exist
     */
    ConversationDTO editPhoto(String idConversation, String photo, Authentication authentication) throws NotFoundException, MissingParametersException, UnAuthorizedException;

    /**
     * edit the name of a conversation
     *
     * @param idConversation the id of the conversation
     * @param name           the new name
     * @param authentication the authentication of the member
     * @return the conversation updated
     * @throws NotFoundException if the conversation doesn't exist
     */
    ConversationDTO editName(String idConversation, String name, Authentication authentication) throws NotFoundException, MissingParametersException, UnAuthorizedException;

    /**
     * delete a conversation
     * @param idConversation the id of the conversation
     * @Param authentication the authentication of the member
     * @throws NotFoundException if the conversation doesn't exist
     */
    void deleteConversation(String idConversation) throws NotFoundException;


    /**
     * leave a conversation
     * @param idConversation the id of the conversation
     * @param authentication the authentication of the member
     * @throws NotFoundException if the conversation doesn't exist
     * @throws MissingParametersException if the id of the member is empty
     */
    void leaveConversation(String idConversation, Authentication authentication) throws NotFoundException, MissingParametersException, SuccessfullyLeavedConversationException, UnAuthorizedException;



}
