package com.pouetteur.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.pouetteur.authservice.AuthServiceApplication;
import com.pouetteur.authservice.dto.LoginDTO;
import com.pouetteur.authservice.dto.TokenDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.repository.MemberRepository;
import com.pouetteur.authservice.repository.UserRepository;
import org.hibernate.Hibernate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pouetteur.authservice.model.Role.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthServiceApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    public static final String USER_URL = "/auth/users";
    public static final String AUTH_URL = "/auth";
    public static final String USER_ID_URL = USER_URL + "/{id}";
    public static final String USER_ALL_URL = USER_URL + "";
    public static final String LOGIN_URL = AUTH_URL + "/login";

    public static final String ADMIN_PASSWORD = "password";
    public static final String USER_PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private List<User> users;
    private Member member;
    private User user;
    private TokenDTO userToken;
    private User admin;
    private TokenDTO adminToken;



    @BeforeEach
    public void setUp() throws Exception {
        Faker faker = new Faker();
        users = new ArrayList<>();
        //Generate users with random data
        for (int i = 0; i < 10; i++) {
            User user = new User(faker.name().username(), faker.internet().emailAddress(), passwordEncoder.encode(faker.internet().password()));
            users.add(userRepository.save(user));
        }

        //Generate admin user
        admin = new User("admin", passwordEncoder.encode(ADMIN_PASSWORD), "admin@admin.com");
        admin.setRoles(List.of(ROLE_ADMIN, ROLE_USER));
        admin = userRepository.save(admin);

        //Generate user user
        user = new User("user",  passwordEncoder.encode(USER_PASSWORD), "user@user.com");
        user.setRoles(List.of(ROLE_USER));
        user = userRepository.save(user);
        member =memberRepository.save(new Member(user.getId(),user.getUsername(),user.getEmail(), user.getRoles()));


        //Generate admin token
        mockMvc.perform(
                post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDTO(admin.getUsername(), ADMIN_PASSWORD)))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(admin.getId())))
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.refreshToken", is(notNullValue())))
                .andDo(result -> adminToken = objectMapper.readValue(result.getResponse().getContentAsString(), TokenDTO.class));

        //Generate user token
        mockMvc.perform(
                post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDTO(user.getUsername(), USER_PASSWORD)))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user.getId())))
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.refreshToken", is(notNullValue())))
                .andDo(result -> userToken = objectMapper.readValue(result.getResponse().getContentAsString(), TokenDTO.class));

    }

    /**
     * Test to get all users
     * Success
     */
    @DisplayName("Test to get all users - Success")
    @Test
    void GetAllSuccess() throws Exception {
        mockMvc.perform(
                get(USER_ALL_URL)
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(users.size() + 2)));
    }

    /**
     * Test to get all users
     * Fail - Unauthorized
     */
    @DisplayName("Test to get all users - Fail - Unauthorized")
    @Test
    void GetAllFailUnauthorized() throws Exception {
        mockMvc.perform(
                get(USER_ALL_URL)
        ).andExpect(status().isUnauthorized());
    }

    /**
     * Test to get all users
     * Fail - Forbidden
     */
    @DisplayName("Test to get all users - Fail - Forbidden")
    @Test
    void GetAllFailForbidden() throws Exception {
        mockMvc.perform(
                get(USER_ALL_URL)
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isForbidden());
    }

    /**
     * Test to get user by id with admin role
     * Success
     */
    @DisplayName("Test to get user by id with admin role - Success")
    @Test
    void GetByIdSuccess() throws Exception {
        mockMvc.perform(
                get(USER_ID_URL, user.getId())
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]", is(ROLE_USER.name())));
    }

    /**
     * Test to get myself with user role
     * Success
     */
    @DisplayName("Test to get myself with user role - Success")
    @Test
    void GetMyselfSuccess() throws Exception {
        mockMvc.perform(
                get(USER_ID_URL, user.getId())
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]", is(ROLE_USER.name())));
    }

    /**
     * Test to get user by id with user role
     * Fail - Forbidden
     */
    @DisplayName("Test to get user by id with user role - Fail - Forbidden")
    @Test
    void GetByIdFailForbidden() throws Exception {
        mockMvc.perform(
                get(USER_ID_URL, admin.getId())
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isForbidden());
    }

    /**
     * Test to get user by id
     * Fail - Not Found
     */
    @DisplayName("Test to get user by id - Fail - Not Found")
    @Test
    void GetByIdFailNotFound() throws Exception {
        mockMvc.perform(
                get(USER_ID_URL, 1000)
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isNotFound());
    }

    /**
     * Delete user by id
     * Success
     */
    @DisplayName("Delete user by id - Success")
    @Test
    void DeleteByIdSuccess() throws Exception {
        mockMvc.perform(
                delete(USER_ID_URL, user.getId())
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isOk());

        mockMvc.perform(
                get(USER_ID_URL, user.getId())
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isNotFound());
    }

    /**
     * Delete user by id
     * Fail - Forbidden
     */
    @DisplayName("Delete user by id - Fail - Forbidden")
    @Test
    void DeleteByIdFailForbidden() throws Exception {
        mockMvc.perform(
                delete(USER_ID_URL, admin.getId())
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isForbidden());
    }

    /**
     * Delete user by id
     * Fail - Not Found
     */
    @DisplayName("Delete user by id - Fail - Not Found")
    @Test
    void DeleteByIdFailNotFound() throws Exception {
        mockMvc.perform(
                delete(USER_ID_URL, 1000)
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isNotFound());
    }

    /**
     * Update user self update by id
     * Success
     */
    @DisplayName("Update user by id - Success")
    @Test
    void UpdateByIdSuccess() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newemail.@test.com");
        userDTO.setId(user.getId());

        mockMvc.perform(
                patch(USER_ID_URL, user.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(userDTO.getUsername())))
                .andExpect(jsonPath("$.email", is(userDTO.getEmail())))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]", is(ROLE_USER.name())));

        mockMvc.perform(
                get(USER_ID_URL, user.getId())
                                .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.username", is(userDTO.getUsername())))
                .andExpect(jsonPath("$.email", is(userDTO.getEmail())))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]", is(ROLE_USER.name())));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        Assertions.assertEquals(user, userRepository.findById(user.getId()).orElse(null));
    }

    /**
     * Update user by id
     * Fail - Forbidden - User can't update other user
     */
    @DisplayName("Update user by id - Fail - Forbidden - User can't update other user")
    @Test
    void UpdateByIdFailForbidden() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newemail.@test.com");
        userDTO.setId(user.getId());

        mockMvc.perform(
                patch(USER_ID_URL, admin.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("Authorization", "Bearer " + userToken.getAccessToken())
        ).andExpect(status().isForbidden());
    }

    /**
     * Update user by id
     * Fail - Not Found
     */
    @DisplayName("Update user by id - Fail - Not Found")
    @Test
    void UpdateByIdFailNotFound() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newemail.@test.com");
        userDTO.setId(UUID.randomUUID().toString());

        mockMvc.perform(
                patch(USER_ID_URL, 1000)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isNotFound());
    }

    /**
     * Update user by id
     * Fail - Bad Request - Username already exists
     */
    @DisplayName("Update user by id - Fail - Bad Request - Username already exists")
    @Test
    void UpdateByIdFailBadRequestUsernameExists() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(admin.getUsername());
        userDTO.setEmail("newemail.@test.com");
        userDTO.setId(user.getId());

        mockMvc.perform(
                patch(USER_ID_URL, user.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isBadRequest());
    }

    /**
     * Update user by id
     * Fail - Bad Request - Email already exists
     */
    @DisplayName("Update user by id - Fail - Bad Request - Email already exists")
    @Test
    void UpdateByIdFailBadRequestEmailExists() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail(admin.getEmail());
        userDTO.setId(user.getId());

        mockMvc.perform(
                patch(USER_ID_URL, user.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("Authorization", "Bearer " + adminToken.getAccessToken())
        ).andExpect(status().isBadRequest());
    }
}
