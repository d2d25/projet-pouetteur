package com.pouetteur.pouetservice.config;


import com.pouetteur.pouetservice.modele.Member;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
//        User user = new User();
//        user.setId(source.getSubject());
//        user.setUsername(source.getClaim("username"));
//        user.setEmail(source.getClaim("email"));
        List<String> roles = source.getClaim("roles");

//        List<Role> roleList = new ArrayList<>();
//        roles.forEach(role -> roleList.add(Role.valueOf(role)));
//        user.setRoles(roleList);
        Member member = new Member();
        member.setId(source.getSubject());
        member.setUsername(source.getClaim("username"));
        return new UsernamePasswordAuthenticationToken(member, source, getAuthorities(roles));

//        return new UsernamePasswordAuthenticationToken(user, source, user.getAuthorities());
    }

    public Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }
}
