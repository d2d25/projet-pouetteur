package com.pouetteur.messagingservice.service;

import com.pouetteur.messagingservice.modele.Conversation;
import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MessageDTO;
import com.pouetteur.messagingservice.modele.Member;
import com.pouetteur.messagingservice.modele.Message;
import com.pouetteur.messagingservice.repository.ConversationRepository;
import com.pouetteur.messagingservice.service.exceptions.NotFoundException;
import com.pouetteur.messagingservice.service.exceptions.UnAuthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class MessageServiceImpl implements IMessageService {
    private final ConversationRepository conversationRepository;
    private final ModelMapper modelMapper;

    public MessageServiceImpl(ConversationRepository conversationRepository, ModelMapper modelMapper) {
        this.conversationRepository = conversationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ConversationDTO sendMessage(String idConversation, String text, Authentication authentication) throws NotFoundException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));

        Member member = (Member) authentication.getPrincipal();
        if (!conversation.getMembers().contains(member)){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        //Member member = conversation.getMembers().stream().filter(m -> m.getId().equals(idMember)).findFirst().orElseThrow(() -> new UnAuthorizedException(MessageFormat.format("Member {0} is UnAuthorized to do this action", idMember)));
        Message message = new Message(member, text);
        conversation.addMessage(message);
        this.conversationRepository.save(conversation);
        return new ConversationDTO(conversation);
        //return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
    }

    @Override
    public MessageDTO getMessage(String idConversation, String idMessage, Authentication authentication) throws NotFoundException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        Member member = (Member) authentication.getPrincipal();
        if (!conversation.getMembers().contains(member)){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
//        if (conversation.getMembers().stream().noneMatch(m -> m.getId().equals(idMember))) {
//            throw new UnAuthorizedException(MessageFormat.format("Member {0} is UnAuthorized to do this action", idMember));
//        }
        Message message = conversation.getMessages().stream().filter(m -> m.getId().equals(idMessage)).findFirst().orElseThrow(() -> new NotFoundException(MessageFormat.format("Message {0} not found", idMessage)));
//        return new MessageDTO(message);
        return modelMapper.map(message, MessageDTO.class);
    }

    @Override
    public ConversationDTO deleteMessage(String idConversation, String idMessage, Authentication authentication ) throws NotFoundException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        Message message = conversation.getMessages().stream().filter(m -> m.getId().equals(idMessage)).findFirst().orElseThrow(() -> new NotFoundException(MessageFormat.format( "Message {0} not found", idMessage)));
        Member member = (Member) authentication.getPrincipal();
        if (!conversation.getMembers().contains(member)){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        if (!message.getAuthor().getId().equals(member.getId())) {
            throw new UnAuthorizedException(MessageFormat.format("Member {0} is UnAuthorized to do this action", member.getId()));
        }
        conversation.getMessages().remove(message);
//        this.conversationRepository.save(conversation);
//        return new ConversationDTO(conversation);
        return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
    }


}
