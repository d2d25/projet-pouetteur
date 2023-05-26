package com.pouetteur.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "members")
public class Member implements Profile{
    @Id
    private String id;
    private String username;
    private String name;
    private Date creationDate;
    private String profilePhoto;
    private String profileBanner;
    private String bio;
    private String email;
    @Column
    @Enumerated(EnumType.STRING)
    private List<Role> roles;
    private String birthdate;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "members_followers",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private List<Member> followers;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "members_followers",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> membersFollowed;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Community> communitiesFollowed;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_reactions")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Reaction, Integer> reactions = initReactions();

    public Member(String id, String username, String email, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.email = email;
        this.followers = new ArrayList<>();
        this.membersFollowed = new ArrayList<>();
        this.communitiesFollowed = new ArrayList<>();
    }

    /**
     * Initialize reactions map with 0 for each reaction
     * @return reactions map
     */
    public static Map<Reaction, Integer> initReactions() {
        return Arrays.stream(Reaction.values()).collect(Collectors.toMap(reaction -> reaction, reaction -> 0));
    }

    public void addFollower(Member follower) {
        followers.add(follower);
    }

    public void removeFollower(Member follower) {
        followers.remove(follower);
    }

    public void addFollowed(Member memberFollowed) {
        membersFollowed.add(memberFollowed);
    }

    public void removeFollowed(Member memberFollowed) {
        membersFollowed.remove(memberFollowed);
    }

    public void addFollowed(Community communityFollowed) {
        communitiesFollowed.add(communityFollowed);
    }

    public void removeFollowed(Community communityFollowed) {
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

    public Set<Profile> getFollowedProfiles() {
        Set<Profile> followedProfiles = new HashSet<>();
        if (membersFollowed != null) {
            followedProfiles.addAll(membersFollowed);
        }
        if (communitiesFollowed != null) {
            followedProfiles.addAll(communitiesFollowed);
        }
        return followedProfiles;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
