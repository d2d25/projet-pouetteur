package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.SignupDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.rabbitmq.RabbitMQProducer;
import com.pouetteur.authservice.repository.UserRepository;
import com.pouetteur.authservice.service.exception.EmailAlreadyExistsException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import com.pouetteur.authservice.service.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    UserService userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    private RabbitMQProducer rabbitMQProducer;

    User userWithoutId;
    User userWithId;
    User userUpdated;
    SignupDTO signupDTO;

    UserDTO userDTOWithId;
    UserDTO userDTOUpdated;


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
        signupDTO = new SignupDTO();
        signupDTO.setUsername("username");
        signupDTO.setEmail("email");
        signupDTO.setPassword("password");
        userDTOWithId = new UserDTO();
        userDTOWithId.setId("id");
        userDTOWithId.setUsername("username");
        userDTOWithId.setEmail("email");
        userUpdated = new User();
        userUpdated.setId("id");
        userUpdated.setUsername("usernameUpdated");
        userUpdated.setEmail("emailUpdated");
        userUpdated.setPassword("password");
        userDTOUpdated = new UserDTO();
        userDTOUpdated.setId("id");
        userDTOUpdated.setUsername("usernameUpdated");
        userDTOUpdated.setEmail("emailUpdated");
    }

    /**
     * Test create user
     * Username already exists
     */
    @DisplayName("Test create user - Username already exists")
    @Test
    void createUsernameThrowAlreadyExists() {
        when(userRepository.existsByUsername(signupDTO.getUsername())).thenReturn(true);
        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.create(signupDTO);
        });
    }

    /**
     * Test create user
     * Email already exists
     */
    @DisplayName("Test create user - Email already exists")
    @Test
    void createEmailThrowAlreadyExists() {
        when(userRepository.existsByUsername(signupDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signupDTO.getEmail())).thenReturn(true);
        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.create(signupDTO);
        });
    }

    /**
     * Test create user
     * Success
     */
    @DisplayName("Test create user - Success")
    @Test
    void createSuccess() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        when(userRepository.existsByUsername(signupDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(signupDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(signupDTO, User.class)).thenReturn(userWithoutId);
        when(userRepository.save(userWithoutId)).thenReturn(userWithId);
        this.rabbitMQProducer.send(userWithId);
        Assertions.assertEquals(userWithId, userService.create(signupDTO));
    }

    /**
     * Test get all users
     * Success
     */
    @DisplayName("Test get all users - Success")
    @Test
    void getAllUsersSuccess() {

        //Given
        List<User> users = List.of(userWithId);
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(userWithId, UserDTO.class)).thenReturn(userDTOWithId);

        //When
        List<UserDTO> userDTOS = List.of(userDTOWithId);
        Assertions.assertEquals(userDTOS, userService.getAll());
    }

    /**
     * Test get user by id
     * Success
     */
    @DisplayName("Test get user by id - Success")
    @Test
    void getUserByIdSuccess() throws NotFoundException {
        //given
        when(userRepository.findById(userWithId.getId())).thenReturn(Optional.ofNullable(userWithId));
        when(modelMapper.map(userWithId, UserDTO.class)).thenReturn(userDTOWithId);

        //when
        Assertions.assertEquals(userDTOWithId, userService.getById(userWithId.getId()));
    }

    /**
     * Test get user by id
     * Not found
     */
    @DisplayName("Test get user by id - Not found")
    @Test
    void getUserByIdNotFound() {
        when(userRepository.findById("id")).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getById("id");
        });
    }

    /**
     * Test delete user by id
     * Success
     */
    @DisplayName("Test delete user - Success")
    @Test
    void deleteUserSuccess() throws NotFoundException {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.ofNullable(userWithId));
        userService.delete("id");
        verify(userRepository, times(1)).delete(userWithId);
    }

    /**
     * Test delete user by id
     * Not found
     */
    @DisplayName("Test delete user - Not found")
    @Test
    void deleteUserByIdNotFound() {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.delete("id");
        });
    }

    /**
     * Test update user
     * Success
     */
    @DisplayName("Test update user - Success")
    @Test
    void updateUserSuccess() throws NotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {

        when(userRepository.findById("id")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(userRepository.existsByUsername(userDTOUpdated.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTOUpdated.getEmail())).thenReturn(false);
        when(modelMapper.map(userDTOUpdated, User.class)).thenReturn(userUpdated);

        when(userRepository.save(userUpdated)).thenReturn(userUpdated);
        when(modelMapper.map(userUpdated, UserDTO.class)).thenReturn(userDTOUpdated);
        Assertions.assertEquals(userDTOUpdated, userService.update("id", userDTOUpdated));
    }

    /**
     * Test update user by id
     * Not found
     */
    @DisplayName("Test update user by id - Not found")
    @Test
    void updateUserByIdNotFound() {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.update("id", userDTOWithId);
        });
    }

    /**
     * Test update user by id
     * Username already exists
     */
    @DisplayName("Test update user by id - Username already exists")
    @Test
    void updateUserByIdUsernameAlreadyExists() throws NotFoundException {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(userRepository.existsByUsername(userDTOUpdated.getUsername())).thenReturn(true);
        Assertions.assertThrows(UsernameAlreadyExistsException.class, () -> {
            userService.update("id", userDTOUpdated);
        });
    }

    /**
     * Test update user by id
     * Email already exists
     */
    @DisplayName("Test update user by id - Email already exists")
    @Test
    void updateUserByIdEmailAlreadyExists() throws NotFoundException {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(userRepository.existsByUsername(userDTOUpdated.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTOUpdated.getEmail())).thenReturn(true);
        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.update("id", userDTOUpdated);
        });
    }

    /**
     * Test get user by username
     * Success
     */
    @DisplayName("Test get user by username - Success")
    @Test
    void getUserByUsernameSuccess() throws NotFoundException {
        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(modelMapper.map(userWithId, UserDTO.class)).thenReturn(userDTOWithId);
        Assertions.assertEquals(userDTOWithId, userService.getByUsername("username"));
    }

    /**
     * Test get user by username
     * Not found
     */
    @DisplayName("Test get user by username - Not found")
    @Test
    void getUserByUsernameNotFound() {
        when(userRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getByUsername("username");
        });
    }

    /**
     * Test get user by email
     * Success
     */
    @DisplayName("Test get user by email - Success")
    @Test
    void getUserByEmailSuccess() throws NotFoundException {
        when(userRepository.findByEmail("email")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(modelMapper.map(userWithId, UserDTO.class)).thenReturn(userDTOWithId);
        Assertions.assertEquals(userDTOWithId, userService.getByEmail("email"));
    }

    /**
     * Test get user by email
     * Not found
     */
    @DisplayName("Test get user by email - Not found")
    @Test
    void getUserByEmailNotFound() {
        when(userRepository.findByEmail("email")).thenReturn(java.util.Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getByEmail("email");
        });
    }

    /**
     * Test update user password
     * Success
     */
    @DisplayName("Test update user password - Success")
    @Test
    void updatePasswordSuccess() throws NotFoundException {
        when(userRepository.findById("id")).thenReturn(java.util.Optional.ofNullable(userWithId));
        when(passwordEncoder.matches("password", userWithId.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPassword");
        when(userRepository.save(userWithId)).thenReturn(userWithId);
        when(modelMapper.map(userWithId, UserDTO.class)).thenReturn(userDTOWithId);
        Assertions.assertEquals(userDTOWithId, userService.updatePassword("id", "password", "newPassword"));
    }

}