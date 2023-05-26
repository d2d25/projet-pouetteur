package com.pouetteur.authservice.dto;

public class CommunityDTO {
    String id;
    String name;
    String description;
    String creationDate;
    String banner;
    String photo;
    String owner;
    String members;
    String posts;
    String followers;
    String membersFollowed;
    String communitiesFollowed;
    String reactions;

    public CommunityDTO() {
    }

    public CommunityDTO(String id, String name, String description, String creationDate, String banner, String photo, String owner, String members, String posts, String followers, String membersFollowed, String communitiesFollowed, String reactions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.banner = banner;
        this.photo = photo;
        this.owner = owner;
        this.members = members;
        this.posts = posts;
        this.followers = followers;
        this.membersFollowed = membersFollowed;
        this.communitiesFollowed = communitiesFollowed;
        this.reactions = reactions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }
}
