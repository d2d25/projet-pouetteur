package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.ProfileDTO;
import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.Role;
import com.pouetteur.authservice.repository.CommunityRepository;
import com.pouetteur.authservice.repository.MemberRepository;
import com.pouetteur.authservice.service.exception.NotAutorizeToUpdateException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
public class ProfileServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ProfileService profileService;

    private Member member;
    private Community community;
    private ProfileDTO memberProfileDTO;
    private ProfileDTO communityProfileDTO;
    private ProfileDTO memberProfileDTOWithoutClassType;
    private ProfileDTO communityProfileDTOWithoutClassType;

    @BeforeEach
    public void setUp() {
        member = new Member("id", "username", "name", new Date(), "profilePhoto", "profileBanner", "bio", "email", List.of(Role.ROLE_USER), "birthdate", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Member.initReactions());
        community = new Community("id", "username", "name", new Date(), "profilePhoto", "profileBanner", "bio", new ArrayList<>(), member);
        memberProfileDTO = new ProfileDTO("id", "username", "name", new Date(), "profilePhoto", "profileBanner", "bio", null, null, null, "email", List.of(Role.ROLE_USER), "birthdate", null, null, Member.class.getSimpleName());
        communityProfileDTO = new ProfileDTO("id", "username", "name", new Date(), "profilePhoto", "profileBanner", "bio", null, null, null, null, null, null, null, null, Community.class.getSimpleName());
        memberProfileDTOWithoutClassType = memberProfileDTO.clone();
        communityProfileDTOWithoutClassType = communityProfileDTO.clone();
    }

    /**
     * Test getAll() method
     * Success
     */
    @DisplayName("Test getAll() method - Success")
    @Test
    public void testGetAll() {
        when(memberRepository.findAll()).thenReturn(List.of(member));
        when(communityRepository.findAll()).thenReturn(List.of(community));
        when(modelMapper.map(member, ProfileDTO.class)).thenReturn(memberProfileDTOWithoutClassType);
        when(modelMapper.map(community, ProfileDTO.class)).thenReturn(communityProfileDTOWithoutClassType);
        List<ProfileDTO> allProfiles = List.of(memberProfileDTO, communityProfileDTO);
        Assertions.assertEquals(allProfiles, profileService.getAll());
    }

    /**
     * Test getProfileById() method
     * Success - Member
     */
    @DisplayName("Test getProfileById() method - Success - Member")
    @Test
    public void testGetProfileByIdSuccessMember() throws NotFoundException {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(modelMapper.map(member, ProfileDTO.class)).thenReturn(memberProfileDTOWithoutClassType);
        Assertions.assertEquals(memberProfileDTO, profileService.getById(member.getId()));
    }

    /**
     * Test getProfileById() method
     * Success - Community
     */
    @DisplayName("Test getProfileById() method - Success - Community")
    @Test
    public void testGetProfileByIdSuccessCommunity() throws NotFoundException {
        when(memberRepository.findById(community.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(community.getId())).thenReturn(Optional.of(community));
        when(modelMapper.map(community, ProfileDTO.class)).thenReturn(communityProfileDTOWithoutClassType);
        Assertions.assertEquals(communityProfileDTO, profileService.getById(community.getId()));
    }

    /**
     * Test getProfileById() method
     * Failure - NotFoundException
     */
    @DisplayName("Test getProfileById() method - Failure - NotFoundException")
    @Test
    public void testGetProfileByIdFailureNotFoundException() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(member.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> profileService.getById(member.getId()));
    }

    /**
     * Test getProfileByUsername() method
     * Success - Member
     */
    @DisplayName("Test getProfileByUsername() method - Success - Member")
    @Test
    public void testGetProfileByUsernameSuccessMember() throws NotFoundException {
        when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));
        when(modelMapper.map(member, ProfileDTO.class)).thenReturn(memberProfileDTOWithoutClassType);
        Assertions.assertEquals(memberProfileDTO, profileService.getByUsername(member.getUsername()));
    }

    /**
     * Test getProfileByUsername() method
     * Success - Community
     */
    @DisplayName("Test getProfileByUsername() method - Success - Community")
    @Test
    public void testGetProfileByUsernameSuccessCommunity() throws NotFoundException {
        when(memberRepository.findByUsername(community.getUsername())).thenReturn(Optional.empty());
        when(communityRepository.findByUsername(community.getUsername())).thenReturn(Optional.of(community));
        when(modelMapper.map(community, ProfileDTO.class)).thenReturn(communityProfileDTOWithoutClassType);
        Assertions.assertEquals(communityProfileDTO, profileService.getByUsername(community.getUsername()));
    }

    /**
     * Test getProfileByUsername() method
     * Failure - NotFoundException
     */
    @DisplayName("Test getProfileByUsername() method - Failure - NotFoundException")
    @Test
    public void testGetProfileByUsernameFailureNotFoundException() {
        when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.empty());
        when(communityRepository.findByUsername(member.getUsername())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> profileService.getByUsername(member.getUsername()));
    }

    /**
     * Test delete
     * Success - Member
     */
    @DisplayName("Test delete() method - Success - Member")
    @Test
    public void testDeleteSuccessMember() throws NotFoundException {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(communityRepository.findById(member.getId())).thenReturn(Optional.empty());
        profileService.delete(member.getId());
        verify(memberRepository, times(1)).delete(member);
    }

    /**
     * Test delete
     * Success - Community
     */
    @DisplayName("Test delete() method - Success - Community")
    @Test
    public void testDeleteSuccessCommunity() throws NotFoundException {
        when(memberRepository.findById(community.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(community.getId())).thenReturn(Optional.of(community));
        profileService.delete(community.getId());
        verify(communityRepository, times(1)).delete(community);
    }

    /**
     * Test delete
     * Failure - NotFoundException
     */
    @DisplayName("Test delete() method - Failure - NotFoundException")
    @Test
    public void testDeleteFailureNotFoundException() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(member.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> profileService.delete(member.getId()));
    }

    /**
     * Test update
     * Success - Member
     */
    @DisplayName("Test update() method - Success - Member")
    @Test
    public void testUpdateSuccessMember() throws NotFoundException, NotAutorizeToUpdateException {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(communityRepository.findById(member.getId())).thenReturn(Optional.empty());
        when(modelMapper.map(memberProfileDTO, Member.class)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(modelMapper.map(member, ProfileDTO.class)).thenReturn(memberProfileDTOWithoutClassType);
        Assertions.assertEquals(memberProfileDTO, profileService.update(member.getId(), memberProfileDTO));
    }

    /**
     * Test update
     * Success - Community
     */
    @DisplayName("Test update() method - Success - Community")
    @Test
    public void testUpdateSuccessCommunity() throws NotFoundException, NotAutorizeToUpdateException {
        when(memberRepository.findById(community.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(community.getId())).thenReturn(Optional.of(community));
        when(modelMapper.map(communityProfileDTO, Community.class)).thenReturn(community);
        when(communityRepository.save(community)).thenReturn(community);
        when(modelMapper.map(community, ProfileDTO.class)).thenReturn(communityProfileDTOWithoutClassType);
        Assertions.assertEquals(communityProfileDTO, profileService.update(community.getId(), communityProfileDTO));
    }

    /**
     * Test update
     * Failure - NotFoundException
     */
    @DisplayName("Test update() method - Failure - NotFoundException")
    @Test
    public void testUpdateFailureNotFoundException() {
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());
        when(communityRepository.findById(member.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> profileService.update(member.getId(), memberProfileDTO));
    }


}
