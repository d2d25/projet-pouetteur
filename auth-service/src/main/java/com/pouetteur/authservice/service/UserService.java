package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.SignupDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.rabbitmq.RabbitMQProducer;
import com.pouetteur.authservice.repository.UserRepository;
import com.pouetteur.authservice.service.exception.EmailAlreadyExistsException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import com.pouetteur.authservice.service.exception.UsernameAlreadyExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.pouetteur.authservice.model.Role.ROLE_USER;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    /**
     * Get all users
     * @return : List of users
     */
    @Override
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    /**
     * Get user by id
     * @param id : User id
     * @return : User
     * @throws NotFoundException : User not found
     */
    @Override
    public UserDTO getById(String id) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageFormat.format("User {0} not found", id)));
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Create user
     * @param signupDTO : User to create
     * @return : User created
     * @throws UsernameAlreadyExistsException : Username already exists
     * @throws EmailAlreadyExistsException : Email already exists
     */
    @Override
    public User create(SignupDTO signupDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (userRepository.existsByUsername(signupDTO.getUsername()))
            throw new UsernameAlreadyExistsException(MessageFormat.format("User {0} already exists", signupDTO.getUsername()));
        if (userRepository.existsByEmail(signupDTO.getEmail()))
            throw new EmailAlreadyExistsException(MessageFormat.format("Email {0} already exists", signupDTO.getEmail()));
        User user = modelMapper.map(signupDTO, User.class);
        if (Objects.isNull(user.getRoles()) || user.getRoles().isEmpty())
            user.setRoles(List.of(ROLE_USER));
        User userSaved = userRepository.save(user);
        this.rabbitMQProducer.send(userSaved);
        return userSaved;
    }

    /**
     * Update user
     * @param id : User id
     * @param userDTO : User to update
     * @return : User updated
     * @throws NotFoundException : User not found
     * @throws UsernameAlreadyExistsException : Username already exists
     * @throws EmailAlreadyExistsException : Email already exists
     */
    @Override
    public UserDTO update(String id, UserDTO userDTO) throws NotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageFormat.format("User {0} not found", id)));
        if (!user.getUsername().equals(userDTO.getUsername()) && userRepository.existsByUsername(userDTO.getUsername()))
            throw new UsernameAlreadyExistsException(MessageFormat.format("User {0} already exists", userDTO.getUsername()));
        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail()))
            throw new EmailAlreadyExistsException(MessageFormat.format("Email {0} already exists", userDTO.getEmail()));

        if (Objects.nonNull(userDTO.getUsername()))
            user.setUsername(userDTO.getUsername());
        if (Objects.nonNull(userDTO.getEmail()))
            user.setEmail(userDTO.getEmail());
        if (Objects.nonNull(userDTO.getRoles()))
            user.setRoles(userDTO.getRoles());

        user = userRepository.save(user);
        this.rabbitMQProducer.send(user);
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Delete user
     * @param id : User id
     * @throws NotFoundException : User not found
     */
    @Override
    public void delete(String id) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(MessageFormat.format("User {0} not found", id)));
        userRepository.delete(user);
    }

    /**
     * Get user by email
     * @param email : User email
     * @return : User
     * @throws NotFoundException : User not found
     */
    @Override
    public UserDTO getByEmail(String email) throws NotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(MessageFormat.format("User with email {0} not found", email)));
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Get user by username
     * @param username : User username
     * @return : User
     * @throws NotFoundException : User not found
     */
    @Override
    public UserDTO getByUsername(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(MessageFormat.format("User with username {0} not found", username)));
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Update user password
     * @param userId : User id
     * @param oldPassword : Old password
     * @param newPassword : New password
     * @return : User updated
     * @throws NotFoundException : User not found
     */
    @Override
    public UserDTO updatePassword(String userId, String oldPassword, String newPassword) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(MessageFormat.format("User {0} not found", userId)));
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new NotFoundException(MessageFormat.format("User {0} not found", userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }
}
