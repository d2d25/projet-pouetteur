package com.pouetteur.authservice.controller;

import com.pouetteur.authservice.config.TokenGenerator;
import com.pouetteur.authservice.dto.*;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.Role;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.service.IUserService;
import com.pouetteur.authservice.service.exception.EmailAlreadyExistsException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import com.pouetteur.authservice.service.exception.UsernameAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.pouetteur.authservice.model.Role.ROLE_USER;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final UserDetailsManager userDetailsManager;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtAuthenticationProvider refreshTokenAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final ProfileController profileController;

    public AuthController(
            IUserService userService,
            UserDetailsManager userDetailsManager,
            TokenGenerator tokenGenerator,
            DaoAuthenticationProvider daoAuthenticationProvider,
            @Qualifier("jwtRefreshTokenAuthenticationProvider") JwtAuthenticationProvider refreshTokenAuthenticationProvider,
            Validator validator,
            PasswordEncoder passwordEncoder,
            ProfileController profileController) {
        this.userService = userService;
        this.userDetailsManager = userDetailsManager;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.refreshTokenAuthenticationProvider = refreshTokenAuthenticationProvider;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.profileController = profileController;
    }

    /**
     * Méthode qui permet de s'enregistrer
     * @param signupDTO : contient l'username, le password et l'email
     * @return : le token
     * BadRequest si l'username ou l'email existe déjà
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody SignupDTO signupDTO) {
        Set<ConstraintViolation<SignupDTO>> violations = validator.validate(signupDTO);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream().map(v -> "{ Property : " + v.getPropertyPath() + ", Value : " + v.getInvalidValue() + ", Error Message : " + v.getMessage() + "}").toList();
            return ResponseEntity.badRequest().body(new ErrorMessageResponseDTO(errors, HttpStatus.BAD_REQUEST));
        }
        //si l'utilisateur n'est pas connecté et qu'il n'est pas admin alors on set le role à USER
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            signupDTO.setRoles(List.of(ROLE_USER));
        }

        signupDTO.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        try {
            User user = userService.create(signupDTO);
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setId(user.getId());
            profileDTO.setUsername(user.getUsername());
            profileDTO.setEmail(user.getEmail());
            profileDTO.setClassType(Member.class.getSimpleName());
            profileDTO.setRoles(user.getRoles());
            profileController.createMember(profileDTO);
            authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), Collections.emptyList());
            return ResponseEntity.created(URI.create("/auth/user/"+user.getId())).body(tokenGenerator.createToken(authentication));
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponseDTO(List.of("Username already exists"), HttpStatus.BAD_REQUEST));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new ErrorMessageResponseDTO(List.of("Email already exists"), HttpStatus.BAD_REQUEST));
        }


    }

    /**
     * Méthode qui permet de se connecter via l'username et l'email
     * @param loginDTO : contient l'username et le password
     * @return : le token
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        if (!violations.isEmpty()) {
            //recupère une liste du champ qui ne respecte pas la contrainte suivie du message d'erreur
            List<String> errors = violations.stream().map(v -> "{ Property : " + v.getPropertyPath() + ", Value : " + v.getInvalidValue() + ", Error Message : " + v.getMessage() + "}").toList();
            return ResponseEntity.badRequest().body(new ErrorMessageResponseDTO(errors, HttpStatus.BAD_REQUEST));
        }

        String login = loginDTO.getLogin();

        if (login.contains("@")) {
            try {
                login = userService.getByEmail(login).getUsername();
            } catch (NotFoundException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }


        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login, loginDTO.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    /**
     * Méthode qui permet de se connecter via le refresh token
     * @param token : le token
     * @return : le token
     */
    @PostMapping("/token/{token}")
    public ResponseEntity<TokenDTO> token(@PathVariable String token) {
        System.out.println("token method called");
        System.out.println("token : " + token);
        Authentication authentication = refreshTokenAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(token));
        System.out.println("Le principal est : " + authentication.getPrincipal().toString());
        //check if present in db
        if (!userDetailsManager.userExists(authentication.getPrincipal().toString()))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

}
