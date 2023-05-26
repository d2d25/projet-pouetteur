package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.SignupDTO;
import com.pouetteur.authservice.dto.UserDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.service.exception.EmailAlreadyExistsException;
import com.pouetteur.authservice.service.exception.NotFoundException;
import com.pouetteur.authservice.service.exception.UsernameAlreadyExistsException;

import java.util.List;


public interface IUserService {
    /**
     * Get all users
     * @return : List of users
     */
    List<UserDTO> getAll();

    /**
     * Get user by id
     * @param id : User id
     * @return : User
     * @throws NotFoundException : User not found
     */
    UserDTO getById(String id) throws NotFoundException;

    /**
     * Create user
     * @param signupDTO : User to create
     * @return : User created
     * @throws UsernameAlreadyExistsException : Username already exists
     * @throws EmailAlreadyExistsException : Email already exists
     */
    User create(SignupDTO signupDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;

    /**
     * Update user
     * @param id : User id
     * @param userDTO : User to update
     * @return : User updated
     * @throws NotFoundException : User not found
     * @throws UsernameAlreadyExistsException : Username already exists
     * @throws EmailAlreadyExistsException : Email already exists
     */
    UserDTO update(String id, UserDTO userDTO) throws NotFoundException, UsernameAlreadyExistsException, EmailAlreadyExistsException ;

    /**
     * Delete user
     * @param id : User id
     * @throws NotFoundException : User not found
     */
    void delete(String id) throws NotFoundException;

    /**
     * Get user by email
     * @param email : User email
     * @return : User
     * @throws NotFoundException : User not found
     */
    UserDTO getByEmail(String email) throws NotFoundException;

    /**
     * Get user by username
     * @param username : User username
     * @return : User
     * @throws NotFoundException : User not found
     */
    UserDTO getByUsername(String username) throws NotFoundException;

    /**
     * Update user password
     * @param userId : User id
     * @param oldPassword : Old password
     * @param newPassword : New password
     * @return : User updated
     * @throws NotFoundException : User not found
     */
    UserDTO updatePassword(String userId, String oldPassword, String newPassword) throws NotFoundException;
}
