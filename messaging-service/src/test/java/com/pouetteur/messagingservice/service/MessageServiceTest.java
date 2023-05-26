package com.pouetteur.messagingservice.service;

import com.github.javafaker.Faker;
import com.pouetteur.messagingservice.modele.Conversation;
import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.modele.DTO.MessageDTO;
import com.pouetteur.messagingservice.modele.Member;
import com.pouetteur.messagingservice.modele.Message;
import com.pouetteur.messagingservice.repository.ConversationRepository;
import com.pouetteur.messagingservice.service.exceptions.NotFoundException;
import com.pouetteur.messagingservice.service.exceptions.UnAuthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MessageServiceTest.class})
public class MessageServiceTest {
    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private Authentication authentication;

    @Mock
    private Authentication authentication2;

    @Mock
    private ModelMapper modelMapper;

    private Message message;
    private MessageDTO messageDTO;
    private List<Member> members;
    private List<MemberDTO> membersDTO;
    private Conversation conversation;
    private ConversationDTO conversationDTO;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker();
        members = new ArrayList<>();
        membersDTO = new ArrayList<>();
        List<String> listName = List.of("leo", "timothé", "julien", "denez");
        for (int i = 0; i < 4; i++) {
            members.add(new Member(UUID.randomUUID().toString(), listName.get(i)));
            membersDTO.add(new MemberDTO(members.get(i)));
        }
        //Generate conversations and conversationsDTO
        conversation = new Conversation(faker.name().fullName(), members.subList(0, 3));
        conversation.setId("1");

        //Generate messages and messagesDTO
        message = new Message(members.get(0), "first");
        message.setId("1");
        messageDTO = new MessageDTO(message);

        conversation.addMessage(message);
        conversationDTO = new ConversationDTO(conversation);

        when(authentication.getPrincipal()).thenReturn(members.get(0));
        when(authentication2.getPrincipal()).thenReturn(members.get(3));

    }

    /**
     * Test sendMessage - success
     */
    @Test
    @DisplayName("Test sendMessage - success")
    public void testSendMessageSuccess() throws NotFoundException, UnAuthorizedException {
        String text = "salut timothé";
        String conversationId = conversation.getId();

        Conversation conversationWithMessage = new Conversation(conversation.getName(), new ArrayList<>(conversation.getMembers()));
        conversationWithMessage.setId(conversation.getId());
        conversationWithMessage.addMessage(message);
        Message newMessage = new Message(members.get(0), text);
        newMessage.setId("2");
        newMessage.setDate(message.getDate());
        conversationWithMessage.addMessage(newMessage);
        ConversationDTO conversationDTOWithMessage = new ConversationDTO(conversationWithMessage);

        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.ofNullable(conversation));
        when(conversationRepository.save(conversationWithMessage)).thenReturn(conversationWithMessage);
//        when(modelMapper.map(conversationWithMessage, ConversationDTO.class)).thenReturn(conversationDTOWithMessage);
        Assertions.assertEquals(conversationDTOWithMessage.getMessages().size(), messageService.sendMessage(conversationId, text, authentication).getMessages().size());
    }

    /**
     * Test sendMessage - NotFoundException - conversation not found
     */
    @Test
    @DisplayName("Test sendMessage - NotFoundException - conversation not found")
    public void testSendMessageNotFoundExceptionConversationNotFound() {
        String message = "salut timothé";
        String conversationId = conversation.getId();
        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> messageService.sendMessage(conversationId, message, authentication));
    }


    /**
     * Test UnAuthorizedException - sender not in conversation
     */
    @Test
    @DisplayName("Test sendMessage - UnAuthorizedException - sender not found")
    public void testSendMessageNotFoundExceptionSenderNotFound() {
        String message = "salut timothé";
        String conversationId = conversation.getId();
        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.ofNullable(conversation));
        Assertions.assertThrows(UnAuthorizedException.class, () -> messageService.sendMessage(conversationId, message, authentication2));
    }

    /**
     * Test getMessage - success
     */
    @Test
    @DisplayName("Test getMessage - success")
    public void testGetMessageSuccess() {
        //Given
        String conversationId = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(conversationId)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertDoesNotThrow(() -> messageService.getMessage(conversationId, idMessage, authentication));
    }

    /**
     * Test getMessage - NotFoundException - conversation not found
     */
    @Test
    @DisplayName("Test getMessage - NotFoundException - conversation not found")
    public void testGetMessageNotFoundExceptionConversationNotFound() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(null));
        //Then
        Assertions.assertThrows(NotFoundException.class, () -> messageService.getMessage(idConversation, idMessage, authentication));
    }

    /**
     * Test getMessage - NotFoundException - message not found
     */
    @Test
    @DisplayName("Test getMessage - NotFoundException - message not found")
    public void testGetMessageNotFoundExceptionMessageNotFound() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = UUID.randomUUID().toString();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertThrows(NotFoundException.class, () -> messageService.getMessage(idConversation, idMessage, authentication));
    }

    /**
     * Test getMessage - UnAuthorizedException - sender not in conversation
     */
    @Test
    @DisplayName("Test getMessage - UnAuthorizedException - sender not in conversation")
    public void testGetMessageUnAuthorizedExceptionSenderNotInConversation() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertThrows(UnAuthorizedException.class, () -> messageService.getMessage(idConversation, idMessage, authentication2));
    }

    /**
     * Test deleteMessage - success
     */
    @Test
    @DisplayName("Test deleteMessage - success")
    public void testDeleteMessageSuccess() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertDoesNotThrow(() -> messageService.deleteMessage(idConversation, idMessage, authentication));
    }

    /**
     * Test deleteMessage - NotFoundException - conversation not found
     */
    @Test
    @DisplayName("Test deleteMessage - NotFoundException - conversation not found")
    public void testDeleteMessageNotFoundExceptionConversationNotFound() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(null));
        //Then
        Assertions.assertThrows(NotFoundException.class, () -> messageService.deleteMessage(idConversation, idMessage, authentication));
    }

    /**
     * Test deleteMessage - NotFoundException - message not found
     */
    @Test
    @DisplayName("Test deleteMessage - NotFoundException - message not found")
    public void testDeleteMessageNotFoundExceptionMessageNotFound() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = UUID.randomUUID().toString();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertThrows(NotFoundException.class, () -> messageService.deleteMessage(idConversation, idMessage, authentication));
    }

    /**
     * Test deleteMessage - UnAuthorizedException - sender not in conversation
     */
    @Test
    @DisplayName("Test deleteMessage - UnAuthorizedException - sender not in conversation")
    public void testDeleteMessageUnAuthorizedExceptionSenderNotInConversation() {
        //Given
        String idConversation = conversation.getId();
        String idMessage = conversation.getMessages().get(0).getId();
        //When
        when(conversationRepository.findById(idConversation)).thenReturn(java.util.Optional.ofNullable(conversation));
        //Then
        Assertions.assertThrows(UnAuthorizedException.class, () -> messageService.deleteMessage(idConversation, idMessage, authentication2));
    }




}
