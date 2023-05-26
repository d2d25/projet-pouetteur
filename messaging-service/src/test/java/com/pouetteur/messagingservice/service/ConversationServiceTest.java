package com.pouetteur.messagingservice.service;

import com.github.javafaker.Faker;
import com.pouetteur.messagingservice.modele.Conversation;
import com.pouetteur.messagingservice.modele.DTO.ConversationDTO;
import com.pouetteur.messagingservice.modele.DTO.MemberDTO;
import com.pouetteur.messagingservice.modele.Member;
import com.pouetteur.messagingservice.modele.exceptions.AtLeast2MembersException;
import com.pouetteur.messagingservice.repository.ConversationRepository;
import com.pouetteur.messagingservice.service.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;


import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {IConversationService.class})
public class ConversationServiceTest {
    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    @Mock
    private ModelMapper modelMapper;

    private List<Member> members;
    private List<MemberDTO> membersDTO;
    private Conversation conversation;
    private ConversationDTO conversationDTO;

    @Mock
    private Authentication authentication;

    @Mock
    private Authentication authentication2;

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
        conversation = new Conversation(faker.name().fullName(), List.of(members.get(0), members.get(1), members.get(2)));
        conversation.setId("1");
        conversationDTO = new ConversationDTO(conversation);

        when(authentication.getPrincipal()).thenReturn(members.get(0));
        when(authentication2.getPrincipal()).thenReturn(members.get(1));
    }

    /**
     * Test getConversation - success
     */
    @Test
    @DisplayName("Test getConversation - success")
    public void testGetConversationSuccess() throws NotFoundException, UnAuthorizedException {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        when(modelMapper.map(conversation, ConversationDTO.class)).thenReturn(conversationDTO);
        Assertions.assertEquals(conversationDTO, conversationService.getConversation(conversation.getId(), authentication));
    }

    /**
     * Test getConversation - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test getConversation - throws NotFoundException (conversation doesn't exist)")
    public void testGetConversationThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.getConversation(conversation.getId(), authentication));
    }

    /**
     * Test createConversation - success
     */
    @Test
    @DisplayName("Test createConversation - success")
    public void testCreateConversationSuccess() throws AtLeast2MembersException, MissingParametersException, UnAuthorizedException {
        Conversation conversationWithoutId = new Conversation(conversation.getName(), new ArrayList<>(conversation.getMembers()));
        ConversationDTO conversationDTOWithoutId = new ConversationDTO(conversationWithoutId);
        when(modelMapper.map(conversationDTOWithoutId, Conversation.class)).thenReturn(conversationWithoutId);

        when(conversationRepository.save(conversationWithoutId)).thenReturn(conversation);
        when(modelMapper.map(conversation, ConversationDTO.class)).thenReturn(conversationDTO);
        Assertions.assertEquals(conversationDTO, conversationService.createConversation(conversationDTOWithoutId, authentication));

    }


    /**
     * Test createConversation - success (name not given)
     */
    @Test
    @DisplayName("Test createConversation - success (name not given)")
    public void testCreateConversationSuccess_NameNotGiven() throws AtLeast2MembersException, MissingParametersException, UnAuthorizedException {
        Conversation conversationWithoutName = new Conversation(null, new ArrayList<>(conversation.getMembers()));
        conversationWithoutName.setId("1");
        ConversationDTO conversationDTOWithoutName = new ConversationDTO(conversationWithoutName);

        String name = conversation.getMembers().stream().map(Member::getUsername).reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        Conversation conversationWithName = new Conversation(name, new ArrayList<>(conversation.getMembers()));
        conversationWithName.setId("1");
        ConversationDTO conversationDTOWithName = new ConversationDTO(conversationWithName);

        when(modelMapper.map(conversationDTOWithoutName, Conversation.class)).thenReturn(conversationWithoutName);
        when(conversationRepository.save(conversationWithName)).thenReturn(conversationWithName);

        when(modelMapper.map(conversationWithName, ConversationDTO.class)).thenReturn(conversationDTOWithName);
        Assertions.assertEquals(conversationDTOWithName, conversationService.createConversation(conversationDTOWithoutName, authentication));
    }

    /**
     * Test createConversation - throws MissingParametersException (conversation is empty)
     */
    @Test
    @DisplayName("Test createConversation - throws MissingParametersException (conversation is empty)")
    public void testCreateConversationThrowsMissingParametersException_EmptyConversation() {
        Assertions.assertThrows(MissingParametersException.class, () -> conversationService.createConversation(null, authentication));
    }


//    /**
//     * Test createConversation - throws MissingParametersException (name is empty)
//     */
//    @Test
//    @DisplayName("Test createConversation - throws MissingParametersException (name is empty)")
//    public void testCreateConversationThrowsMissingParametersException() {
//        when(conversationDTO.getName()).thenReturn(null);
//        Assertions.assertThrows(MissingParametersException.class, () -> conversationService.createConversation(conversationDTO));
//    }

    /**
     * Test createConversation - throws AtLeast2MembersException (members list is empty)
     */
    @Test
    @DisplayName("Test createConversation - throws AtLeast2MembersException (members list is empty)")
    public void testCreateConversationThrowsAtLeast2MembersException_EmptyList() {
        Conversation conversationWithoutMembers = new Conversation(conversation.getName(), new ArrayList<>());
        ConversationDTO conversationDTOWithoutMembers = new ConversationDTO(conversationWithoutMembers);
        Assertions.assertThrows(AtLeast2MembersException.class, () -> conversationService.createConversation(conversationDTOWithoutMembers, authentication));
    }

    /**
     * Test createConversation - throws AtLeast2MembersException (less than 2)
     */
    @Test
    @DisplayName("Test createConversation - throws AtLeast2MembersException (less than 2)")
    public void testCreateConversationThrowsAtLeast2MembersException() {
        conversation.setMembers(Set.of(members.get(0)));
        ConversationDTO conversationDTO = new ConversationDTO(conversation);
        Assertions.assertThrows(AtLeast2MembersException.class, () -> conversationService.createConversation(conversationDTO, authentication));
    }

    /**
     * Test addMembers - success
     */
    @Test
    @DisplayName("Test addMembers - success")
    public void testAddMembersSuccess() throws NotFoundException, MissingParametersException, AlreadyThereException, UnAuthorizedException {
        //Clone de la conversation
        Conversation conversationNewMembers = new Conversation(conversation.getName(), new ArrayList<>(members));
        conversationNewMembers.setId(conversation.getId());
        ConversationDTO conversationNewMembersDTO = new ConversationDTO(conversationNewMembers);

        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));


        when(modelMapper.map(membersDTO.get(3), Member.class)).thenReturn(members.get(3));

        when(conversationRepository.save(conversationNewMembers)).thenReturn(conversationNewMembers);

        when(modelMapper.map(conversationNewMembers, ConversationDTO.class)).thenReturn(conversationNewMembersDTO);

        Assertions.assertEquals(conversationNewMembersDTO, conversationService.addMembers(conversation.getId(), List.of(membersDTO.get(3)), authentication));
        verify(conversationRepository, times(1)).save(conversationNewMembers);
    }

    /**
     * Test addMembers - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test addMembers - throws NotFoundException (conversation doesn't exist)")
    public void testAddMembersThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));

        Assertions.assertThrows(NotFoundException.class, () -> conversationService.addMembers(conversation.getId(), List.of(membersDTO.get(3)), authentication));
    }

    /**
     * Test addMembers - throws MissingParametersException (members list is empty)
     */
    @Test
    @DisplayName("Test addMembers - throws MissingParametersException (members list is empty)")
    public void testAddMembersThrowsMissingParametersException(){
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));

        Assertions.assertThrows(MissingParametersException.class, () -> conversationService.addMembers(conversation.getId(), null, authentication));
    }

    /**
     * Test addMembers - throws AlreadyThereException (member already in conversation)
     */
    @Test
    @DisplayName("Test addMembers - throws AlreadyThereException (member already in conversation)")
    public void testAddMembersThrowsAlreadyThereException() throws NotFoundException, MissingParametersException {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        for (int i = 0; i < membersDTO.size(); i++) {
            when(modelMapper.map(membersDTO.get(i), Member.class)).thenReturn(members.get(i));
        }

        Assertions.assertThrows(AlreadyThereException.class, () -> conversationService.addMembers(conversation.getId(), List.of(membersDTO.get(3), membersDTO.get(0)), authentication));
    }


//    /**
//     * Test addMembers - throws NotFoundException (member doesn't exist)
//     */

//    /**
//     * Test removeMember - success
//     */
//    @Test
//    @DisplayName("Test removeMember - success")
//    public void testRemoveMemberSuccess() throws NotFoundException, MissingParametersException {
//        Conversation conversationRemovedMember = new Conversation(conversation.getName(), List.of(members.get(1), members.get(2)));
//        conversationRemovedMember.setId(conversation.getId());
//        conversationRemovedMember.setPhoto(conversation.getPhoto());
//        ConversationDTO conversationRemovedMemberDTO = new ConversationDTO(conversationRemovedMember);
//
//        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
//        when(conversationRepository.save(conversationRemovedMember)).thenReturn(conversationRemovedMember);
//        when(modelMapper.map(conversationRemovedMember, ConversationDTO.class)).thenReturn(conversationRemovedMemberDTO);
//        List<Member> members = new ArrayList<>(conversation.getMembers());
//        String idMember = members.get(0).getId();
//        Assertions.assertEquals(conversationRemovedMemberDTO, conversationService.removeMember(conversation.getId(), idMember));
//    }
//
//    /**
//     * Test removeMember - throws NotFoundException (conversation doesn't exist)
//     */
//    @Test
//    @DisplayName("Test removeMember - throws NotFoundException (conversation doesn't exist)")
//    public void testRemoveMemberThrowsNotFoundException() {
//        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
//        Assertions.assertThrows(NotFoundException.class, () -> conversationService.removeMember(conversation.getId(), membersDTO.get(2).getId()));
//    }
//
//    /**
//     * Test removeMember - throws NotFoundException (member not in conversation)
//     */
//    @Test
//    @DisplayName("Test removeMember - throws NotFoundException (member not in conversation)")
//    public void testRemoveMemberThrowsNotFoundException2() throws NotFoundException, MissingParametersException {
//        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
//        when(modelMapper.map(List.of(membersDTO.get(0), membersDTO.get(1)), Member[].class)).thenReturn(List.of(members.get(0), members.get(1)).toArray(new Member[0]));
//        Assertions.assertThrows(NotFoundException.class, () -> conversationService.removeMember(conversation.getId(), membersDTO.get(2).getId()));
//    }
//
//    /**
//     * Test removeMember - member is the last one in the conversation
//     */
//    @Test
//    @DisplayName("Test removeMember - member is the last one in the conversation")
//    public void testRemoveMemberLastMember() throws NotFoundException, MissingParametersException {
//        conversation.removeMember(members.get(2));
//        Conversation conversationRemovedMember = new Conversation(conversation.getName(), List.of(members.get(0), members.get(1)));
//        conversationRemovedMember.setId(conversation.getId());
//        conversationRemovedMember.setPhoto(conversation.getPhoto());
//        ConversationDTO conversationRemovedMemberDTO = new ConversationDTO(conversationRemovedMember);
//
//        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
//        //verify if the conversation is deleted (null expected)
//        Assertions.assertNull(conversationService.removeMember(conversation.getId(), membersDTO.get(1).getId()));
//    }
//
    /**
     * Test leaveConversation - success
     */
    @Test
    @DisplayName("Test leaveConversation - success")
    public void testleaveConversationSuccess() throws NotFoundException, MissingParametersException, UnAuthorizedException {
        Conversation conversationRemovedMember = new Conversation(conversation.getName(), List.of(members.get(1), members.get(2)));
        conversationRemovedMember.setId(conversation.getId());
        conversationRemovedMember.setPhoto(conversation.getPhoto());
        ConversationDTO conversationRemovedMemberDTO = new ConversationDTO(conversationRemovedMember);

        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        when(conversationRepository.save(conversationRemovedMember)).thenReturn(conversationRemovedMember);
        when(modelMapper.map(conversationRemovedMember, ConversationDTO.class)).thenReturn(conversationRemovedMemberDTO);
        List<Member> members = new ArrayList<>(conversation.getMembers());
        String idMember = members.get(0).getId();

        Assertions.assertThrows(SuccessfullyLeavedConversationException.class, () -> conversationService.leaveConversation(conversation.getId(), authentication2));

    }

    /**
     * Test leaveConversation - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test leaveConversation - throws NotFoundException (conversation doesn't exist)")
    public void testleaveConversationThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.leaveConversation(conversation.getId(), authentication));
    }

    /**
     * Test leaveConversation - throws NotFoundException (member not in conversation)
     */
    @Test
    @DisplayName("Test leaveConversation - throws NotFoundException (member not in conversation)")
    public void testleaveConversationThrowsNotFoundException2() throws NotFoundException, MissingParametersException {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        when(modelMapper.map(List.of(membersDTO.get(0), membersDTO.get(1)), Member[].class)).thenReturn(List.of(members.get(0), members.get(1)).toArray(new Member[0]));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.leaveConversation(conversation.getId(), authentication));
    }

    /**
     * Test leaveConversation - member is the last one in the conversation
     */
    @Test
    @DisplayName("Test leaveConversation - member is the last one in the conversation")
    public void testleaveConversationLastMember() throws NotFoundException, MissingParametersException {
        conversation.removeMember(members.get(2));
        Conversation conversationRemovedMember = new Conversation(conversation.getName(), List.of(members.get(0), members.get(1)));
        conversationRemovedMember.setId(conversation.getId());
        conversationRemovedMember.setPhoto(conversation.getPhoto());
        ConversationDTO conversationRemovedMemberDTO = new ConversationDTO(conversationRemovedMember);

        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        //verify if the conversation is deleted (null expected)
        Assertions.assertNull(conversationService.removeMember(conversation.getId(), membersDTO.get(1).getId()));
    }




    /**
     * Test editPhoto - success
     */
    @Test
    @DisplayName("Test editPhoto - success")
    public void testEditPhotoSuccess() throws NotFoundException, MissingParametersException, UnAuthorizedException {
        Conversation conversationEditedPhoto = new Conversation(conversation.getName(), new ArrayList<>(conversation.getMembers()));
        conversationEditedPhoto.setId(conversation.getId());
        conversationEditedPhoto.setPhoto("newPhoto");
        ConversationDTO conversationEditedPhotoDTO = new ConversationDTO(conversationEditedPhoto);

        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        when(conversationRepository.save(conversationEditedPhoto)).thenReturn(conversationEditedPhoto);
        when(modelMapper.map(conversationEditedPhoto, ConversationDTO.class)).thenReturn(conversationEditedPhotoDTO);
        Assertions.assertEquals(conversationEditedPhotoDTO, conversationService.editPhoto(conversation.getId(), "newPhoto", authentication));
    }


    /**
     * Test editPhoto - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test editPhoto - throws NotFoundException (conversation doesn't exist)")
    public void testEditPhotoThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.editPhoto(conversation.getId(), "newPhoto", authentication));
    }


    /**
     * Test editPhoto - throws MissingParametersException (photo is empty)
     */
    @Test
    @DisplayName("Test editPhoto - throws MissingParametersException (photo is empty)")
    public void testEditPhotoThrowsMissingParametersException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        Assertions.assertThrows(MissingParametersException.class, () -> conversationService.editPhoto(conversation.getId(), "", authentication));
    }


    /**
     * Test editName - success
     */
    @Test
    @DisplayName("Test editName - success")
    public void testEditNameSuccess() throws NotFoundException, MissingParametersException, UnAuthorizedException {
        Conversation conversationEditedName = new Conversation("newName", new ArrayList<>(conversation.getMembers()));
        conversationEditedName.setId(conversation.getId());
        conversationEditedName.setPhoto(conversation.getPhoto());
        ConversationDTO conversationEditedNameDTO = new ConversationDTO(conversationEditedName);

        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        when(conversationRepository.save(conversationEditedName)).thenReturn(conversationEditedName);
        when(modelMapper.map(conversationEditedName, ConversationDTO.class)).thenReturn(conversationEditedNameDTO);

        Assertions.assertEquals(conversationEditedNameDTO, conversationService.editName(conversation.getId(), "newName", authentication));
    }

    /**
     * Test editName - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test editName - throws NotFoundException (conversation doesn't exist)")
    public void testEditNameThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.editName(conversation.getId(), "newName", authentication));
    }

    /**
     * Test editName - throws MissingParametersException (name is empty)
     */
    @Test
    @DisplayName("Test editName - throws MissingParametersException (name is empty)")
    public void testEditNameThrowsMissingParametersException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        Assertions.assertThrows(MissingParametersException.class, () -> conversationService.editName(conversation.getId(), "", authentication));
    }

    /**
     * Test deleteConversation - success
     */
    @Test
    @DisplayName("Test deleteConversation - success")
    public void testDeleteConversationSuccess() throws NotFoundException {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(conversation));
        conversationService.deleteConversation(conversation.getId());
        verify(conversationRepository, times(1)).delete(conversation);

    }

    /**
     * Test deleteConversation - throws NotFoundException (conversation doesn't exist)
     */
    @Test
    @DisplayName("Test deleteConversation - throws NotFoundException (conversation doesn't exist)")
    public void testDeleteConversationThrowsNotFoundException() {
        when(conversationRepository.findById(conversation.getId())).thenReturn(java.util.Optional.ofNullable(null));
        Assertions.assertThrows(NotFoundException.class, () -> conversationService.deleteConversation(conversation.getId()));
    }

}
