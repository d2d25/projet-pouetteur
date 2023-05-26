package com.pouetteur.authservice.config;

import com.pouetteur.authservice.model.Role;
import com.pouetteur.authservice.model.User;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {
        User user = new User();
        user.setId(source.getSubject());
        user.setUsername(source.getClaim("username"));
        user.setEmail(source.getClaim("email"));
        List<String> roles = source.getClaim("roles");
        List<Role> roleList = new ArrayList<>();
        roles.forEach(role -> roleList.add(Role.valueOf(role)));
        user.setRoles(roleList);
        return new UsernamePasswordAuthenticationToken(user, source, user.getAuthorities());
    }

}
