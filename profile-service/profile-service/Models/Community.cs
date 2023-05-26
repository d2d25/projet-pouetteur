using profile_service.DTOs;

namespace profile_service.Models;

public class Community : IProfile
{
    public string? Id { get; set; }
    public string? Username { get; set; }
    public string? Name { get; set; }
    public DateTime? CreationDate { get; set; }
    public string? ProfilePhoto { get; set; }
    public string? ProfileBanner { get; set; }
    public string? Bio { get; set; }
    public List<Member>? Members { get; set; }
    public Member? Owner { get; set; }
    
    public Community(string username, string name, DateTime creationDate, Member owner)
    {
        Id = "m*" + Guid.NewGuid();
        Username = username;
        Name = name;
        CreationDate = creationDate;
        Owner = owner;
        Members = new List<Member>();
    }

    public Community(ProfileDto profileDto)
    {
        Id = "m*" + Guid.NewGuid();
        Username = profileDto.Username;
        Name = profileDto.Name;
        CreationDate = DateTime.Now;
        ProfilePhoto = profileDto.ProfilePhoto;
        ProfileBanner = profileDto.ProfileBanner;
        Bio = Bio == null ? "" : profileDto.Bio;
        Members = new List<Member>();
    }

    public Community(){}

}