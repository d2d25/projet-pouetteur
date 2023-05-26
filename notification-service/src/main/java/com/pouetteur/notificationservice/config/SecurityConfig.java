package com.pouetteur.notificationservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtToMemberConverter jwtToMemberConverter;

    private final KeyUtils keyUtils;

    public SecurityConfig(JwtToMemberConverter jwtToMemberConverter, KeyUtils keyUtils) {
        this.jwtToMemberConverter = jwtToMemberConverter;
        this.keyUtils = keyUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //TODO 3
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/auth/*").permitAll()
                                .requestMatchers("/actuator/health").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf().disable()
                .cors().disable()
                .httpBasic().disable()
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtToMemberConverter))
                )
                .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint( new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler( new BearerTokenAccessDeniedHandler())
                );
        return http.build();
    }

    @Bean
    @Primary
    JwtDecoder jwtAccessTokenDecoder() {
        System.out.println("SecurityConfig.jwtAccessTokenDecoder ADBC");
        return NimbusJwtDecoder.withPublicKey(keyUtils.getPublicKey()).build();
    }

  /*  @Bean
    @Primary
    JwtEncoder jwtAccessTokenEncoder() { //TODO 4
        System.out.println("SecurityConfig.jwtAccessTokenEncoder ADBC");

        JWK jwk = new RSAKey
                .Builder(keyUtils.getPublicKey())
                .privateKey(keyUtils.getPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }*/

    @Bean
    @Qualifier("jwtRefreshTokenDecoder")
    JwtDecoder jwtRefreshTokenDecoder() { //TODO 5
        System.out.println("SecurityConfig.jwtRefreshTokenDecoder ADBC");
        return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshPublicKey()).build();
    }

 /*   @Bean
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder jwtRefreshTokenEncoder() { //TODO 6
        System.out.println("SecurityConfig.jwtRefreshTokenEncoder ADBC");
        JWK jwk = new RSAKey
                .Builder(keyUtils.getRefreshPublicKey())
                .privateKey(keyUtils.getRefreshPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }*/

    @Bean
    @Qualifier("jwtRefreshTokenAuthenticationProvider")
    JwtAuthenticationProvider jwtRefreshTokenAuthenticationProvider() {  //TODO 1
        System.out.println("SecurityConfig.jwtRefreshTokenAuthenticationProvider ADBC");
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtAccessTokenDecoder());
        jwtAuthenticationProvider.setJwtAuthenticationConverter(jwtToMemberConverter);
        return jwtAuthenticationProvider;
    }
}