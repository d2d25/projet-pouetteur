package com.pouetteur.messagingservice.modele;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Data @Getter @Setter
@NoArgsConstructor
public class Member implements UserDetails {
    private String id;
    private String username;
    private String name;
    private String profilePhoto;

    public Member(String id, String username, String name, String profilePhoto) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.profilePhoto = profilePhoto;
    }

    public Member(String id, String username) {
        this.id = id;
        this.username = username;
        this.name = null;
        this.profilePhoto = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) && Objects.equals(username, member.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
