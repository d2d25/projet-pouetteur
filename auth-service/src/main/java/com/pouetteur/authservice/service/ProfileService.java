package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.ProfileDTO;
import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.rabbitmq.RabbitMQProducer;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.repository.CommunityRepository;
import com.pouetteur.authservice.repository.MemberRepository;
import com.pouetteur.authservice.service.exception.ClassTypeExpectException;
import com.pouetteur.authservice.service.exception.NotAutorizeToUpdateException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService implements IProfileService{

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;


    /**
     * Get all profiles
     * @return : List of profiles
     */
    @Override
    public List<ProfileDTO> getAll() {
        List<Member> members = memberRepository.findAll();
        List<Community> communities = communityRepository.findAll();
        List<ProfileDTO> allProfiles = new ArrayList<>();
        members.forEach(member -> {
            ProfileDTO profileDTO = modelMapper.map(member, ProfileDTO.class);
            profileDTO.setClassType(Member.class.getSimpleName());
            allProfiles.add(profileDTO);
        });
        communities.forEach(community -> {
            ProfileDTO profileDTO = modelMapper.map(community, ProfileDTO.class);
            profileDTO.setClassType(Community.class.getSimpleName());
            allProfiles.add(profileDTO);
        });
        return allProfiles;
    }

    /**
     * Get profile by id
     * Cherche d'abord dans la liste des membres, puis dans la liste des communautés
     * @param id : Profile id
     * @return : Profile
     * @throws NotFoundException : Profile not found
     */
    @Override
    public ProfileDTO getById(String id) throws NotFoundException {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            ProfileDTO profileDTO = modelMapper.map(member, ProfileDTO.class);
            profileDTO.setClassType(Member.class.getSimpleName());
            return profileDTO;
        }
        Community community = communityRepository.findById(id).orElse(null);
        if (community != null) {
            ProfileDTO profileDTO = modelMapper.map(community, ProfileDTO.class);
            profileDTO.setClassType(Community.class.getSimpleName());
            return profileDTO;
        }
        throw new NotFoundException(MessageFormat.format("Profile {0} not found", id));
    }

    /**
     * Get profile by username
     * Cherche d'abord dans la liste des membres, puis dans la liste des communautés
     * @param username : Profile username
     * @return : Profile
     * @throws NotFoundException : Profile not found
     */
    @Override
    public ProfileDTO getByUsername(String username) throws NotFoundException {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            ProfileDTO profileDTO = modelMapper.map(member.get(), ProfileDTO.class);
            profileDTO.setClassType(Member.class.getSimpleName());
            return profileDTO;
        }
        Optional<Community> community = communityRepository.findByUsername(username);
        if (community.isPresent()) {
            ProfileDTO profileDTO = modelMapper.map(community.get(), ProfileDTO.class);
            profileDTO.setClassType(Community.class.getSimpleName());
            return profileDTO;
        }
        throw new NotFoundException(MessageFormat.format("Profile {0} not found", username));
    }

    /**
     * Delete profile
     * @param id : Profile id
     * @throws NotFoundException : Profile not found
     */
    @Override
    public void delete(String id) throws NotFoundException {
        Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            memberRepository.delete(member);
            return;
        }
        Community community = communityRepository.findById(id).orElse(null);
        if (community != null) {
            communityRepository.delete(community);
            return;
        }
        throw new NotFoundException(MessageFormat.format("Profile {0} not found", id));
    }

    /**
     * Update profile
     * @param id : Profile id
     * @param profile : Profile to update
     * @return : Profile updated
     * @throws NotFoundException : Profile not found
     */
    @Override
    public ProfileDTO update(String id, ProfileDTO profile) throws NotFoundException, NotAutorizeToUpdateException {
        if (!id.equals(profile.getId())) {
            throw new NotAutorizeToUpdateException(MessageFormat.format("ProfileService ne permet pas de changer l'id du profile {0} en {1}", id, profile.getId()));
        }
            Member member = memberRepository.findById(id).orElse(null);
        if (member != null) {
            if (!profile.getId().equals(member.getId())) {
                throw new NotAutorizeToUpdateException(MessageFormat.format("Vous n'etes pas autorisé a changer votre id {0} en {1}", member.getId(), profile.getId()));
            }
            if(!profile.getEmail().equals(member.getEmail())){
                throw new NotAutorizeToUpdateException("ProfileService ne permet pas de modifier l'Email du membre, il faut passer par AuthService");
            }
            if (!profile.getUsername().equals(member.getUsername())) {
                throw new NotAutorizeToUpdateException("ProfileService ne permet pas de modifier le Username du membre, il faut passer par AuthService");
            }
            if (profile.getCreationDate()== null) {
                profile.setCreationDate(member.getCreationDate());
            } else if (!profile.getCreationDate().equals(member.getCreationDate())) {
                throw new NotAutorizeToUpdateException("ProfileService ne permet pas de modifier la date de creation du membre");
            }

            member = modelMapper.map(profile, Member.class);
            member = memberRepository.save(member);
            return modelMapper.map(member, ProfileDTO.class);
        }
        Community community = communityRepository.findById(id).orElse(null);
        if (community != null) {
            community = modelMapper.map(profile, Community.class);
            community = communityRepository.save(community);
            return modelMapper.map(community, ProfileDTO.class);
        }
        throw new NotFoundException(MessageFormat.format("Profile {0} not found", id));
    }

    /**
     * Create profile
     * @param profile : Profile to create
     * @return : Profile created
     */
    @Override
    public ProfileDTO createCommunity(ProfileDTO profile) throws ClassTypeExpectException {
        if (profile.getClassType().equals(Member.class.getSimpleName())) {
            throw new ClassTypeExpectException(MessageFormat.format("Profile service ne peut pas créer un profil {0} sans type ou un membre", profile.getClassType()));
        }
        if (profile.getClassType().equals(Community.class.getSimpleName())) {
            Community community = modelMapper.map(profile, Community.class);
            community = communityRepository.save(community);
            return modelMapper.map(community, ProfileDTO.class);
        }
        throw new ClassTypeExpectException(MessageFormat.format("Profile {0} not found", profile.getClassType()));
    }

    /**
     * Get All Community
     * @return : List of community
     */
    @Override
    public List<ProfileDTO> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        List<ProfileDTO> allProfiles = new ArrayList<>();
        communities.forEach(community -> {
            ProfileDTO profileDTO = modelMapper.map(community, ProfileDTO.class);
            profileDTO.setClassType(Community.class.getSimpleName());
            allProfiles.add(profileDTO);
        });
        return allProfiles;
    }

    /**
     * Get All Members
     * @return : List of members
     */
    @Override
    public List<ProfileDTO> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        List<ProfileDTO> allProfiles = new ArrayList<>();
        members.forEach(member -> {
            ProfileDTO profileDTO = modelMapper.map(member, ProfileDTO.class);
            profileDTO.setClassType(Member.class.getSimpleName());
            allProfiles.add(profileDTO);
        });
        return allProfiles;
    }

    /**
     * Create member
     * @param profile : Profile to create
     * @return : Profile created
     */
    @Override
    public ProfileDTO createMember(ProfileDTO profile) throws ClassTypeExpectException {
        if (profile.getClassType().equals(Community.class.getSimpleName())) {
            throw new ClassTypeExpectException(MessageFormat.format("Profile service ne peut pas créer un profil {0} sans type ou un membre", profile.getClassType()));
        }
        if (profile.getClassType().equals(Member.class.getSimpleName())) {
            Member member = modelMapper.map(profile, Member.class);
            member = memberRepository.save(member);
            return modelMapper.map(member, ProfileDTO.class);
        }
        throw new ClassTypeExpectException(MessageFormat.format("Profile {0} not found", profile.getClassType()));
    }

    @Override
    public void subscribe(User user, String id) throws NotFoundException {
        Member me = memberRepository.findById(id).orElse(null);
        if (me != null) {
            Member followed = memberRepository.findById(user.getId()).orElse(null);
            if (followed != null) {
                me.addFollowed(followed);
                followed.addFollower(me);
                memberRepository.save(me);
                memberRepository.save(followed);
                return;
            }
            Community followedCommunity = communityRepository.findById(user.getId()).orElse(null);
            if (followedCommunity != null) {
                me.addFollowed(followedCommunity);
                memberRepository.save(me);
                communityRepository.save(followedCommunity);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Profile {0} not found", id));
        }
    }

    @Override
    public void unsubscribe(User user, String id) throws NotFoundException {
        Member me = memberRepository.findById(id).orElse(null);
        if (me != null) {
            Member followed = memberRepository.findById(user.getId()).orElse(null);
            if (followed != null) {
                me.removeFollowed(followed);
                followed.removeFollower(me);
                memberRepository.save(me);
                memberRepository.save(followed);
                return;
            }
            Community followedCommunity = communityRepository.findById(user.getId()).orElse(null);
            if (followedCommunity != null) {
                me.removeFollowed(followedCommunity);
                memberRepository.save(me);
                communityRepository.save(followedCommunity);
                return;
            }
            throw new NotFoundException(MessageFormat.format("Profile {0} not found", id));
        }
    }
}
