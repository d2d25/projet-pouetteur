package com.pouetteur.authservice.service;

import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Spy
    @InjectMocks
    AuthService authService;
    @Mock
    Authentication authentication;
    User userWithoutId;
    User userWithId;

    User userUpdated;

    @BeforeEach
    void setUp() {
        userWithoutId = new User();
        userWithoutId.setUsername("username");
        userWithoutId.setEmail("email");
        userWithoutId.setPassword("password");
        userWithId = new User();
        userWithId.setId("id");
        userWithId.setUsername("username");
        userWithId.setEmail("email");
        userWithId.setPassword("password");
        userUpdated = new User();
        userUpdated.setId("id");
        userUpdated.setUsername("usernameUpdated");
        userUpdated.setEmail("emailUpdated");
        userUpdated.setPassword("passwordUpdated");
    }

    /**
     * Test of createUser method
     * success
     */
    @DisplayName("Test of createUser method success")
    @Test
    void createUserSuccess() {
        when(userRepository.save(userWithoutId)).thenReturn(userWithId);
        authService.createUser(userWithoutId);
        verify(userRepository, times(1)).save(userWithoutId);
    }

    /**
     * Test of updateUser method
     * success
     */
    @DisplayName("Test of updateUser method success")
    @Test
    void updateUserSuccess() {
        when(userRepository.save(userUpdated)).thenReturn(userUpdated);
        authService.updateUser(userUpdated);
        verify(userRepository, times(1)).save(userUpdated);
    }

    /**
     * Test of deleteUser method
     * success
     */
    @DisplayName("Test of deleteUser method success")
    @Test
    void deleteUserSuccess() {
        when(userRepository.existsById(userWithId.getId())).thenReturn(true);
        authService.deleteUser(userWithId.getId());
        verify(userRepository, times(1)).deleteById(userWithId.getId());
    }

    /**
     * Test of loadUserByUsername method
     * success
     */
    @DisplayName("Test of loadUserByUsername method success")
    @Test
    void loadUserByUsernameSuccess() {
        when(userRepository.findByUsername(userWithId.getUsername())).thenReturn(Optional.ofNullable(userWithId));
        authService.loadUserByUsername(userWithId.getUsername());
        verify(userRepository, times(1)).findByUsername(userWithId.getUsername());
    }

    /**
     * Test of loadUserByUsername method
     * UsernameNotFoundException
     */
    @DisplayName("Test of loadUserByUsername method UsernameNotFoundException")
    @Test
    void loadUserByUsernameUsernameNotFoundException() {
        when(userRepository.findByUsername(userWithId.getUsername())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername(userWithId.getUsername()),
                "Expected loadUserByUsername() to throw UsernameNotFoundException");
    }

    /**
     * Test of changePassword method
     * success
     */
    @DisplayName("Test of changePassword method success")
    @Test
    void changePasswordSuccess() {
        SecurityContextHolder.setContext(mock(SecurityContext.class));
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userWithId.getUsername());
        when(userRepository.findByUsername(userWithId.getUsername())).thenReturn(Optional.ofNullable(userWithId));
        String OLD_PASSWORD = "password";
        String NEW_PASSWORD = "passwordUpdated";
        when(authService.loadUserByUsername(userWithId.getUsername())).thenReturn(userWithId);
        when(userRepository.save(userUpdated)).thenReturn(userUpdated);
        authService.changePassword(OLD_PASSWORD, NEW_PASSWORD);
    }

    /**
     * Test of userExists method
     * return true
     */
    @DisplayName("Test of userExists method return true")
    @Test
    void userExistsReturnTrue() {
        when(userRepository.existsById(userWithId.getId())).thenReturn(true);
        assertTrue(authService.userExists(userWithId.getId()));
    }

    /**
     * Test of userExists method
     * return false
     */
    @DisplayName("Test of userExists method return false")
    @Test
    void userExistsReturnFalse() {
        when(userRepository.existsById(userWithId.getId())).thenReturn(false);
        assertFalse(authService.userExists(userWithId.getId()));
    }



}