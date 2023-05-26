package com.pouetteur.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pouetteur.authservice.AuthServiceApplication;
import com.pouetteur.authservice.dto.LoginDTO;
import com.pouetteur.authservice.dto.SignupDTO;
import com.pouetteur.authservice.dto.TokenDTO;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.repository.MemberRepository;
import com.pouetteur.authservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.pouetteur.authservice.model.Role.ROLE_USER;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AuthServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

    public static final String AUTH_URL = "/auth";
    public static final String REGISTER_URL = AUTH_URL + "/register";
    private static final String LOGIN_URL = AUTH_URL + "/login";
    private static final String PASSWORD_FOR_EXISTING_USER = "password";
    private static final String TOKEN_URL = AUTH_URL + "/token";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User existingUser;


    @BeforeEach
    void setUp() {
        existingUser = new User("username", passwordEncoder.encode(PASSWORD_FOR_EXISTING_USER), "email@exemple.com");
        existingUser.setRoles(List.of(ROLE_USER));
        existingUser = userRepository.save(existingUser);
        memberRepository.save(new Member(existingUser.getId(),existingUser.getUsername(),existingUser.getEmail(), existingUser.getRoles()));
    }

    /**
     * Test of register method
     * success
     */
    @DisplayName("Test d'inscription d'un nouvel utilisateur réussi")
    @Test
    void registerTestSuccess() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", startsWith("m*")))
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));

        Assertions.assertEquals(2, userRepository.count());
        Assertions.assertEquals(2, memberRepository.count());

    }

    /**
     * Test of register method
     * Bad Request because username already exists
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username déjà existant")
    @Test
    void registerTestBadRequestUsernameAlreadyExists() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO(existingUser.getUsername(), "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", contains("Username already exists")))
                .andExpect(jsonPath("$.messages", hasSize(1)));

    }

    /**
     * Test of register method
     * Bad Request because email already exists
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email déjà existant")
    @Test
    void registerTestBadRequestEmailAlreadyExists() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername", "newPassword", existingUser.getEmail());

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", contains("Email already exists")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because username is null
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username null")
    @Test
    void registerTestBadRequestUsernameNull() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO(null, "newPassword", "newEmail@exemple.com");
        
        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : null, Error Message : must not be null}","{ Property : username, Value : null, Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    /**
     * Test of register method
     * Bad Request because email is null
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email null")
    @Test
    void registerTestBadRequestEmailNull() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername", "newPassword", null);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : email, Value : null, Error Message : must not be null}","{ Property : email, Value : null, Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));

    }

    /**
     * Test of register method
     * Bad Request because password is null
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Password null")
    @Test
    void registerTestBadRequestPasswordNull() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername", null, "newEmail@exemple.com" );

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : null, Error Message : must not be null}","{ Property : password, Value : null, Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    /**
     * Test of register method
     * Bad Request because username is empty
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username vide")
    @Test
    void registerTestBadRequestUsernameEmpty() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("", "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : , Error Message : must not be empty}","{ Property : username, Value : , Error Message : size must be between 4 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));


    }

    /**
     * Test of register method
     * Bad Request because email is empty
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email vide")
    @Test
    void registerTestBadRequestEmailEmpty() throws Exception{
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "newPassword", "");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : email, Value : , Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because password is empty
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Password vide")
    @Test
    void registerTestBadRequestPasswordEmpty() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : , Error Message : must not be empty}","{ Property : password, Value : , Error Message : size must be between 8 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    /**
     * Test of register method
     * Bad Request because username is too short
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username trop court")
    @Test
    void registerTestBadRequestUsernameTooShort() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("123",  "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : 123, Error Message : size must be between 4 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because email is not a valid email
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email non valide")
    @Test
    void registerTestBadRequestEmailNotValid() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "newPassword", "newEmailexemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : email, Value : newEmailexemple.com, Error Message : must be a well-formed email address}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because password is too short
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Password trop court")
    @Test
    void registerTestBadRequestPasswordTooShort() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "1234567", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : 1234567, Error Message : size must be between 8 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because username is too long
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username trop long")
    @Test
    void registerTestBadRequestUsernameTooLong() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("012345678901234567890",  "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : 012345678901234567890, Error Message : size must be between 4 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because email is too long
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email trop long")
    @Test
    void registerTestBadRequestEmailTooLong() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "newPassword", "azertyuiopqsdfghqdqsdfqsfqsfqjklmwxcvbn1234567890@domaine-example.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : email, Value : azertyuiopqsdfghqdqsdfqsfqsfqjklmwxcvbn1234567890@domaine-example.com, Error Message : size must be between 0 and 50}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because password is too long
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Password trop long")
    @Test
    void registerTestBadRequestPasswordTooLong() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "012345678901234567890", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : 012345678901234567890, Error Message : size must be between 8 and 20}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because username contains special characters
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username contenant des caractères spéciaux")
    @Test
    void registerTestBadRequestUsernameContainsSpecialCharacters() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newU%sername",  "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : newU%sername, Error Message : must match \"^[a-zA-Z0-9_]*$\"}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because username contains spaces
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Username contenant des espaces")
    @Test
    void registerTestBadRequestUsernameContainsSpaces() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newU sername",  "newPassword", "newEmail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : username, Value : newU sername, Error Message : must match \"^[a-zA-Z0-9_]*$\"}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of register method
     * Bad Request because email contains spaces
     */
    @DisplayName("Test d'inscription d'un utilisateur avec un Email contenant des espaces")
    @Test
    void registerTestBadRequestEmailContainsSpaces() throws Exception {
        // Given
        SignupDTO signupDTO = new SignupDTO("newUsername",  "newPassword", "newEm ail@exemple.com");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : email, Value : newEm ail@exemple.com, Error Message : must be a well-formed email address}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));

    }


    /**
     * Test of login method
     * Success login with username
     */
    @DisplayName("Test de connexion d'un utilisateur")
    @Test
    void loginTestSuccess() throws Exception {
        // Given
        userRepository.delete(existingUser);
        LoginDTO loginDTO = new LoginDTO(existingUser.getUsername(), PASSWORD_FOR_EXISTING_USER);
        SignupDTO signupDTO = new SignupDTO(existingUser.getUsername(), PASSWORD_FOR_EXISTING_USER, existingUser.getEmail());
        AtomicReference<String> userId = new AtomicReference<>();
        mockMvc.perform(
                post(REGISTER_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDTO)
        )).andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(notNullValue())))
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.refreshToken", is(notNullValue())))
                // Save userId for next test
                .andDo(result -> userId.set(objectMapper.readTree(result.getResponse().getContentAsString()).get("userId").asText()));


        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId.get())))
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.refreshToken", is(notNullValue())));
    }

    /**
     * Test of login method
     * Success login with email
     */
    @DisplayName("Test de connexion d'un utilisateur")
    @Test
    void loginTestSuccessEmail() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail(), PASSWORD_FOR_EXISTING_USER);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(existingUser.getId())))
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.refreshToken", is(notNullValue())));
    }

    /**
     * Test of login method
     * Bad Request because login username not found
     */
    @DisplayName("Test de connexion avec un login non trouvé")
    @Test
    void loginTestUserNotFound() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getUsername()+"1", PASSWORD_FOR_EXISTING_USER);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isUnauthorized());
    }

    /**
     * Test of login method
     * Bad Request because login email not found
     */
    @DisplayName("Test de connexion avec un login non trouvé")
    @Test
    void loginTestUserNotFoundEmail() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail()+"1", PASSWORD_FOR_EXISTING_USER);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isUnauthorized());
    }

    /**
     * Test of login method
     * Password is incorrect
     */
    @DisplayName("Test de connexion d'un utilisateur avec un mauvais mot de passe")
    @Test
    void loginTestBadRequestPasswordIncorrect() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail(), PASSWORD_FOR_EXISTING_USER+"1");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isUnauthorized());
    }

    /**
     * Test of login method
     * Bad Request because login is null
     */
    @DisplayName("Test de connexion avec un login null")
    @Test
    void loginTestBadRequestUsernameNull() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(null, PASSWORD_FOR_EXISTING_USER);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : login, Value : null, Error Message : must not be null}", "{ Property : login, Value : null, Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    /**
     * Test of login method
     * Bad Request because password is null
     */
    @DisplayName("Test de connexion d'un utilisateur avec un Password null")
    @Test
    void loginTestBadRequestPasswordNull() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail(), null);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : null, Error Message : must not be null}", "{ Property : password, Value : null, Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(2)));
    }

    /**
     * Test of login method
     * Bad Request because login are empty
     */
    @DisplayName("Test de connexion d'un utilisateur avec un Email et Username vide")
    @Test
    void loginTestBadRequestLoginEmpty() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO("", PASSWORD_FOR_EXISTING_USER);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : login, Value : , Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));
    }

    /**
     * Test of login method
     * Bad Request because password is empty
     */
    @DisplayName("Test de connexion d'un utilisateur avec un Password vide")
    @Test
    void loginTestBadRequestPasswordEmpty() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail(), "");

        // When
        ResultActions resultActions = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        );

        // Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.name())))
                .andExpect(jsonPath("$.messages", containsInAnyOrder("{ Property : password, Value : , Error Message : must not be empty}")))
                .andExpect(jsonPath("$.messages", hasSize(1)));

    }

    /**
     * Test of token method
     * Success token
     */
    @DisplayName("Test de récupération d'un token")
    void tokenTestSuccess() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO(existingUser.getEmail(), PASSWORD_FOR_EXISTING_USER);
        String content = mockMvc.perform(
                post(LOGIN_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
        ).andReturn().getResponse().getContentAsString();

        TokenDTO tokenDTO = objectMapper.readValue(content, TokenDTO.class);

        // When
        ResultActions resultActions = mockMvc.perform(
                post(TOKEN_URL + "/" + tokenDTO.getRefreshToken())
                        .contentType(APPLICATION_JSON)
        );

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(existingUser.getId())))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }

    /**
     * Test of token method
     * Bad Request because token is null
     */
    @DisplayName("Test de récupération d'un token avec un refreshToken null")
    @Test
    void tokenTestBadRequestTokenNull() throws Exception {

    }

    /**
     * Test of token method
     * Bad Request because token is empty
     */
    @DisplayName("Test de récupération d'un token avec un refreshToken vide")
    @Test
    void tokenTestBadRequestTokenEmpty() throws Exception {

    }

    /**
     * Test of token method
     * Bad Request because token is incorrect
     */
    @DisplayName("Test de récupération d'un token avec un refreshToken incorrect")
    @Test
    void tokenTestBadRequestTokenIncorrect() throws Exception {

    }

}
