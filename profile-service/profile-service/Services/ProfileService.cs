using AutoMapper;
using profile_service.Data;
using profile_service.DTOs;
using profile_service.Models;
using profile_service.Models.Builder;
using profile_service.Services.Exception;

namespace profile_service.Services;

public class ProfileService : IProfileService
{

    private readonly DataContext _dataContext;
    private readonly Mapper _mapper;
    public ProfileService(DataContext dataContext, Mapper mapper)
    {
        _dataContext = dataContext;
        _mapper = mapper;
    }

    /**
     * Get a profile by username
     * @param username
     * @return ProfileDto
     * @throws NotFoundException
     */
    public ProfileDto GetProfileByUsername(string username)
    {
        IProfile? profile = _dataContext.Members.FirstOrDefault(x => x.Username == username);
        if (profile == null)
            profile = _dataContext.Communities.FirstOrDefault(x => x.Username == username);
        if (profile == null)
            throw new NotFoundException("Profile not found");
        
        return _mapper.Map<ProfileDto>(profile);
    }

    /**
     * Get a profile by id
     * @param id
     * @return ProfileDto
     * @throws NotFoundException
     */
    public ProfileDto GetProfileById(string id)
    {
        IProfile? profile = null;
        if (id.StartsWith("m*"))
        {
            profile = _dataContext.Members.FirstOrDefault(x => x.Id == id);
        }
        else if (id.StartsWith("c*"))
        {
            profile = _dataContext.Communities.FirstOrDefault(x => x.Id == id);
        }
        if (profile == null)
            throw new NotFoundException("Profile not found");
        
        return _mapper.Map<ProfileDto>(profile);
    }

    /**
     * Create a profile
     * @param profileDto
     * @return ProfileDto
     * @throws BadRequestException
     */
    public ProfileDto CreateProfile(ProfileDto profileDto)
    {
        IProfile? returnedProfile = null;
        var username = profileDto.Username;
        //Si le username existe déjà
        if (_dataContext.Members.FirstOrDefault(x => x.Username == username) != null ||
            _dataContext.Communities.FirstOrDefault(x => x.Username == username) != null)
        {
            throw new BadRequestException("Username already exists");
        }
        
        //Si l'id commence par "m*" alors c'est un membre
        if (profileDto.Id != null && profileDto.Id.StartsWith("m*"))
        {
            var member = ProfileBuilder.BuildMember(profileDto);
            _dataContext.Members.Add(member);
            returnedProfile = member;
        }//Si l'id commence par "c*" alors c'est une communaute
        else if (profileDto.Id != null && profileDto.Id.StartsWith("c*"))
        {
            var community = ProfileBuilder.BuildCommunity(profileDto);
            _dataContext.Communities.Add(community);
            returnedProfile = community;
        }
        _dataContext.SaveChanges();
        if (returnedProfile == null)
        {
            throw new BadRequestException("Profile not created");
        }

        return _mapper.Map<ProfileDto>(returnedProfile);
    }

    /**
     * Update a profile
     * @param id
     * @param profileDto
     * @return ProfileDto
     * @throws NotFoundException
     * @throws BadRequestException
     */
    public ProfileDto UpdateProfile(string dto, ProfileDto profileDto)
    {
        IProfile? returnedProfile = null;
        //Si l'id commence par "m*" alors c'est un membre
        if (profileDto.Id == null)
        {
            throw new NotFoundException("Profile not found");
        }

        if (profileDto.Id.StartsWith("m*"))
        {
            var member = _mapper.Map<Member>(profileDto);
            _dataContext.Members.Update(member);
            returnedProfile = member;
        }//Si l'id commence par "c*" alors c'est une communaute
        else if (profileDto.Id.StartsWith("c*"))
        {
            var community = _mapper.Map<Community>(profileDto);
            _dataContext.Communities.Update(community);
            returnedProfile = community;
        }

        _dataContext.SaveChanges();
        
        if (returnedProfile == null)
        {
            throw new BadRequestException("Profile not updated");
        }
        
        return _mapper.Map<ProfileDto>(returnedProfile);
    }

    /**
     * Delete a profile
     * @param id
     * @throws NotFoundException
     */
    public void DeleteProfile(string id)
    {
        if (id.StartsWith("m*"))
        {
            var member = _dataContext.Members.FirstOrDefault(x => x.Id == id);
            if (member == null)
                throw new NotFoundException("Profile not found");
            _dataContext.Members.Remove(member);
        }
        else if (id.StartsWith("c*"))
        {
            var community = _dataContext.Communities.FirstOrDefault(x => x.Id == id);
            if (community == null)
                throw new NotFoundException("Profile not found");
            _dataContext.Communities.Remove(community);
        }
        _dataContext.SaveChanges();
    }

    /**
     * Get all profiles
     * @return List<ProfileDto>
     */
    public IEnumerable<ProfileDto> GetAllProfiles()
    {
        var profiles = new List<IProfile>();
        profiles.AddRange(_dataContext.Members);
        profiles.AddRange(_dataContext.Communities);
        return _mapper.Map<IEnumerable<ProfileDto>>(profiles);
    }
}