package com.pouetteur.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.pouetteur.authservice.AuthServiceApplication;
import com.pouetteur.authservice.dto.TokenDTO;
import com.pouetteur.authservice.model.Community;
import com.pouetteur.authservice.model.Member;
import com.pouetteur.authservice.repository.CommunityRepository;
import com.pouetteur.authservice.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AuthServiceApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProfileControllerTest {

    public static final String PROFILE_URL = "/profile";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommunityRepository communityRepository;

    private List<Community> communities;
    private List<Member> members;
    private TokenDTO userToken;
    private TokenDTO adminToken;


    @BeforeEach
    public void setUp() {
        Faker faker = new Faker();
        members = new ArrayList<>();
        communities = new ArrayList<>();
    }
}
