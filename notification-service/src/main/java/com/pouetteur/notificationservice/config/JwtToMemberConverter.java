package com.pouetteur.notificationservice.config;

import com.pouetteur.notificationservice.modele.Member;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtToMemberConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
       List<String> roles = source.getClaim("roles");
       Member member = new Member();
       member.setId(source.getSubject());
       return new UsernamePasswordAuthenticationToken(member, source, getAuthorities(roles));

    }

    public Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }
}
