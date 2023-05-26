package com.pouetteur.messagingservice.service;

import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.modele.exceptions.AtLeast2MembersException;
import com.pouetteur.messagingservice.service.exceptions.*;
import com.pouetteur.messagingservice.modele.Conversation;
import com.pouetteur.messagingservice.modele.Member;
import com.pouetteur.messagingservice.repository.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements IConversationService {
    private final ConversationRepository conversationRepository;

    private final ModelMapper modelMapper;

    public ConversationServiceImpl(ConversationRepository conversationRepository, ModelMapper modelMapper) {
        this.conversationRepository = conversationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ConversationDTO createConversation(ConversationDTO conversationDTO, Authentication authentication) throws AtLeast2MembersException, MissingParametersException, UnAuthorizedException {
        if (conversationDTO == null) {
            throw new MissingParametersException("Missing conversation");
        }
        if (conversationDTO.getMembers() == null || conversationDTO.getMembers().isEmpty() || conversationDTO.getMembers().size() < 2) {
            throw new AtLeast2MembersException(conversationDTO.getId());
        }
        Conversation conversation = modelMapper.map(conversationDTO, Conversation.class);

        if (!conversation.getMembers().contains((Member) authentication.getPrincipal())){
            throw new UnAuthorizedException("vous ne faites pas partie de la conversation ");
        }

        if (conversationDTO.getPhoto()== null){
            if (conversationDTO.getMembers().size() == 2){
                conversation.setPhoto("user.png");
            } else {
                conversation.setPhoto("default.png");
            }
        }

        if (conversation.getName() == null) {
            StringBuilder defaultName = new StringBuilder();
            for (Member member : conversation.getMembers()) {
                defaultName.append(member.getUsername()).append(", ");
            }
            conversation.setName(defaultName.substring(0, defaultName.length() - 2));
        }
        conversation.setMessages(new ArrayList<>());
        return modelMapper.map(conversationRepository.save(conversation),ConversationDTO.class);

    }


    @Override
    public ConversationDTO getConversation(String idConversation, Authentication authentication) throws NotFoundException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));


        if (!conversation.getMembers().contains((Member) authentication.getPrincipal())){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        return modelMapper.map(conversation, ConversationDTO.class);
        //return new ConversationDTO(conversation);
    }

    @Override
    public List<ConversationDTO> getConversations(Authentication authentication) throws NotFoundException {
        Member member = (Member) authentication.getPrincipal();

        List<Conversation> conversations = this.conversationRepository.findByMembers(member);
//        for (Conversation conversation : conversations) {
//            if (!conversation.getMembers().contains(member)){
//                conversations.remove(conversation);
//            }
//        }
        List<ConversationDTO> conversationDTOS = new ArrayList<>();
        for (Conversation conversation : conversations) {
            conversationDTOS.add(modelMapper.map(conversation, ConversationDTO.class));
        }
        if (conversationDTOS.isEmpty()){
            throw new NotFoundException("No conversation found");
        }
        return conversationDTOS;
    }


    @Override
    public ConversationDTO addMembers(String idConversation, List<MemberDTO> membersDTO, Authentication authentication) throws NotFoundException, MissingParametersException, AlreadyThereException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        if (!conversation.getMembers().contains((Member) authentication.getPrincipal())){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        if (membersDTO==null){
            throw new MissingParametersException("Missing membersDTO");
        }
        for (MemberDTO memberDTO: membersDTO) {
            Member member = modelMapper.map(memberDTO, Member.class);
            if (!conversation.getMembers().contains(member)){
                conversation.addMember(member);
            } else {
                throw new AlreadyThereException("Member " + member.getUsername()+ " is already in conversation");
            }
        }
        if (conversation.getPhoto().equals("user.png") && conversation.getMembers().size() > 2){
            conversation.setPhoto("default.png");
        }

//        this.conversationRepository.save(conversation);
//        return new ConversationDTO(conversation);
        return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);

    }

    @Override
    public ConversationDTO removeMember(String idConversation, String idMember) throws NotFoundException, MissingParametersException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));

        if (idMember == null) {
            throw new MissingParametersException("Missing idMember");
        }
//        Member member = conversation.getMembers().stream().filter(m -> m.getId().equals(idMember)).findFirst().orElseThrow(() -> new NotFoundException(MessageFormat.format("Member {0} not found in the conversation", idMember)));
//        conversation.removeMember(member);
        for (Member member : conversation.getMembers()) {
            if (member.getId().equals(idMember)) {
                conversation.removeMember(member);
                if (conversation.getMembers().size()<= 1){
                    conversationRepository.delete(conversation);
                    return null;
                } else {
                    if (conversation.getPhoto().equals("default.png") && conversation.getMembers().size() == 2){
                        conversation.setPhoto("user.png");
                    }
//            this.conversationRepository.save(conversation);
//            return new ConversationDTO(conversation);
                    return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
                }
            }
        }
        throw new NotFoundException(MessageFormat.format("Member {0} not found in the conversation", idMember));
    }


    @Override
    public ConversationDTO editPhoto(String idConversation, String photo, Authentication authentication) throws NotFoundException, MissingParametersException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        if (!conversation.getMembers().contains((Member) authentication.getPrincipal())){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        if (photo == null || photo.isEmpty()) {
            throw new MissingParametersException("Missing photo");
        }
        conversation.setPhoto(photo);

//        this.conversationRepository.save(conversation);
//        return new ConversationDTO(conversation);
        return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
    }

    @Override
    public ConversationDTO editName(String idConversation, String name, Authentication authentication) throws NotFoundException, MissingParametersException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        if (!conversation.getMembers().contains((Member) authentication.getPrincipal())){
            throw new UnAuthorizedException(MessageFormat.format("vous ne faites pas partie de la conversation {0} ", idConversation));
        }
        if (name == null || name.isEmpty()) {
            throw new MissingParametersException("Missing name");
        }
        conversation.setName(name);

//        this.conversationRepository.save(conversation);
//        return new ConversationDTO(conversation);
        return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
    }

    @Override
    public void deleteConversation(String idConversation) throws NotFoundException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() ->
                new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        this.conversationRepository.delete(conversation);
    }

    @Override
    public void leaveConversation(String idConversation, Authentication authentication) throws NotFoundException, MissingParametersException, SuccessfullyLeavedConversationException, UnAuthorizedException {
        Conversation conversation = this.conversationRepository.findById(idConversation).orElseThrow(() -> new NotFoundException(MessageFormat.format("Conversation {0} not found", idConversation)));
        Member memberAuth = (Member) authentication.getPrincipal();

        for (Member member : conversation.getMembers()) {
            if (member.getId().equals(memberAuth.getId())) {
                conversation.removeMember(member);
                if (conversation.getMembers().size()<= 1){
                    conversationRepository.delete(conversation);
                    throw new SuccessfullyLeavedConversationException(MessageFormat.format("Member {0} successfully leaved the conversation {1}, and it was deleted as such (less than 2 members remaining)", memberAuth.getId(), conversation.getId()));
                } else {
                    if (conversation.getPhoto().equals("default.png") && conversation.getMembers().size() == 2){
                        conversation.setPhoto("user.png");
                    }
                    this.conversationRepository.save(conversation);
//                    return modelMapper.map(this.conversationRepository.save(conversation), ConversationDTO.class);
                    throw new SuccessfullyLeavedConversationException(MessageFormat.format("Member {0} successfully leaved the conversation {1}", memberAuth.getId(), conversation.getId()));

                }
            }
        }
        throw new UnAuthorizedException(MessageFormat.format("Member {0} not found in the conversation", memberAuth.getId()));
    }


}
