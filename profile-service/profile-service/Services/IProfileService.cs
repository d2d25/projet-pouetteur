using profile_service.DTOs;

namespace profile_service.Services;

public interface IProfileService
{
    /**
     * Get a profile by username
     * @param username
     * @return ProfileDto
     * @throws NotFoundException
     */
    ProfileDto GetProfileByUsername(string username);
    /**
     * Get a profile by id
     * @param id
     * @return ProfileDto
     * @throws NotFoundException
     */
    ProfileDto GetProfileById(string id);
    /**
     * Create a profile
     * @param profileDto
     * @return ProfileDto
     * @throws BadRequestException
     */
    ProfileDto CreateProfile(ProfileDto profileDto);
    /**
     * Update a profile
     * @param id
     * @param profileDto
     * @return ProfileDto
     * @throws NotFoundException
     * @throws BadRequestException
     */
    ProfileDto UpdateProfile(string dto, ProfileDto profileDto);
    /**
     * Delete a profile
     * @param id
     * @throws NotFoundException
     */
    void DeleteProfile(string id);
    /**
     * Get all profiles
     * @return List<ProfileDto>
     */
    IEnumerable<ProfileDto> GetAllProfiles();
}