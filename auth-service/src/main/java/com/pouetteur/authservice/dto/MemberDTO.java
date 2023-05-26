package com.pouetteur.authservice.dto;

import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.Reaction;
import com.pouetteur.authservice.model.Role;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class MemberDTO {
    String id;
    String username;
    String name;
    String creationDate;
    String profilePhoto;
    String profileBanner;
    String bio;
    String email;
    List<Role> roles;
    String birthdate;

    private List<MemberDTO> followers;
    private List<MemberDTO> membersFollowed;
    private List<Community> communitiesFollowed;
    private Map<Reaction, Integer> reactions = initReactions();

    public MemberDTO() {
    }
    public MemberDTO(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.name = member.getName();
        this.creationDate = member.getCreationDate().toString();
        this.profilePhoto = member.getProfilePhoto();
        this.profileBanner = member.getProfileBanner();
        this.bio = member.getBio();
        this.email = member.getEmail();
        this.roles = member.getRoles();
        this.birthdate = member.getBirthdate();
        this.followers = member.getFollowers().stream().map(MemberDTO::new).toList();
        this.membersFollowed = member.getMembersFollowed().stream().map(MemberDTO::new).toList();
        this.communitiesFollowed = member.getCommunitiesFollowed();
        this.reactions = member.getReactions();
    }

     /**
     * Initialize reactions map with 0 for each reaction
     * @return reactions map
     */
    private static Map<Reaction, Integer> initReactions() {
        return Arrays.stream(Reaction.values()).collect(Collectors.toMap(reaction -> reaction, reaction -> 0));
    }

    public void addFollower(MemberDTO follower) {
        followers.add(follower);
    }

    public void removeFollower(MemberDTO follower) {
        followers.remove(follower);
    }

    public void addMemberFollowed(MemberDTO memberFollowed) {
        membersFollowed.add(memberFollowed);
    }

    public void removeMemberFollowed(MemberDTO memberFollowed) {
        membersFollowed.remove(memberFollowed);
    }

    public void addCommunityFollowed(Community communityFollowed) {
        communitiesFollowed.add(communityFollowed);
    }

    public void removeCommunityFollowed(Community communityFollowed) {
        communitiesFollowed.remove(communityFollowed);
    }

    public void addReaction(Reaction reaction) {
        reactions.put(reaction, reactions.get(reaction) + 1);
    }

    public void removeReaction(Reaction reaction) {
        reactions.put(reaction, reactions.get(reaction) - 1);
    }

    public void addReactions(Map<Reaction, Integer> reactions) {
        reactions.forEach((reaction, count) -> this.reactions.put(reaction, this.reactions.get(reaction) + count));
    }

    public void removeReactions(Map<Reaction, Integer> reactions) {
        reactions.forEach((reaction, count) -> this.reactions.put(reaction, this.reactions.get(reaction) - count));
    }

}
