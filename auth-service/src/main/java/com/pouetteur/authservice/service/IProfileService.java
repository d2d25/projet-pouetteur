package com.pouetteur.authservice.service;

import com.pouetteur.authservice.dto.ProfileDTO;
import com.pouetteur.authservice.model.User;
import com.pouetteur.authservice.service.exception.ClassTypeExpectException;
import com.pouetteur.authservice.service.exception.NotAutorizeToUpdateException;
import com.pouetteur.authservice.service.exception.NotFoundException;

import java.util.List;

public interface IProfileService {
    /**
     * Get all profiles
     * @return : List of profiles
     */
    List<ProfileDTO> getAll();

    /**
     * Get profile by id
     * @param id : Profile id
     * @return : ProfileDTO
     * @throws NotFoundException : Profile not found
     */
    ProfileDTO getById(String id) throws NotFoundException;

    /**
     * Get profile by username
     * @param username : Profile username
     * @return : ProfileDTO
     * @throws NotFoundException : Profile not found
     */
    ProfileDTO getByUsername(String username) throws NotFoundException;

    /**
     * Delete profile
     * @param id : Profile id
     * @throws NotFoundException : Profile not found
     */
    void delete(String id) throws NotFoundException;

    /**
     * Update profile
     * @param id : Profile id
     * @param profile : Profile to update
     * @return : ProfileDTO updated
     * @throws NotFoundException : Profile not found
     */
    ProfileDTO update(String id, ProfileDTO profile) throws NotFoundException, NotAutorizeToUpdateException;

    /**
     * Create profile
     * @param profile : Profile to create
     * @return : ProfileDTO created
     */
    ProfileDTO createCommunity(ProfileDTO profile) throws ClassTypeExpectException;

    /**
     * Get All Community
     * @return : List of community
     */
    List<ProfileDTO> getAllCommunities();

    /**
     * Get All Member
     * @return : List of Profile
     */
    List<ProfileDTO> getAllMembers();

    /**
     * Create Member
     * @param profile : Profile to create
     * @return : ProfileDTO created
     */
    ProfileDTO createMember(ProfileDTO profile) throws ClassTypeExpectException;

    /**
     * Subscribe to a community
     * @param user : User who subscribe
     * @param id : Community id
     */
    void subscribe(User user, String id) throws NotFoundException;

    void unsubscribe(User user, String id) throws NotFoundException;
}
