using profile_service.DTOs;

namespace profile_service.Models.Builder;

public abstract class ProfileBuilder
{
    public static Member BuildMember(ProfileDto profileDto)
    {
        return new Member(profileDto);
    }
    
    public static Community BuildCommunity(ProfileDto profileDto)
    {
        return new Community(profileDto);
    }
}