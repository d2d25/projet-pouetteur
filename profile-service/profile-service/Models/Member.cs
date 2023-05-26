using System.ComponentModel.DataAnnotations;
using profile_service.DTOs;

namespace profile_service.Models;

public class Member : IProfile
{
    [Key]
    public string? Id { get; set; }
    public string? Username { get; set; }
    public string? Name { get; set; }
    public DateTime? CreationDate { get; set; }
    public string? ProfilePhoto { get; set; }
    public string? ProfileBanner { get; set; }
    public string? Bio { get; set; }
    public string? Email { get; set; }
    public string? Roles { get; set; }
    public DateTime? Birthdate { get; set; }
    public List<Member>? Followers { get; set; }
    public List<Member>? Followings { get; set; }
    public Dictionary<Reaction,int>? Reactions { get; set; }
    
    public Member(){}

    public Member(ProfileDto profileDto)
    {
        Id = "m*" + Guid.NewGuid();
        Username = profileDto.Username;
        Name = profileDto.Name;
        CreationDate = DateTime.Now;
        ProfilePhoto = profileDto.ProfilePhoto;
        ProfileBanner = profileDto.ProfileBanner;
        Bio = Bio == null ? "" : profileDto.Bio;
        Email = profileDto.Email;
        Roles = profileDto.Roles;
        Birthdate = profileDto.Birthdate;
        Followers = new List<Member>();
        Followings = new List<Member>();
        Reactions = InitReactions();
    }
    
    private static Dictionary<Reaction,int> InitReactions()
    {
        return Enum.GetValues(typeof(Reaction)).Cast<Reaction>().ToDictionary(reaction => reaction, reaction => 0);
    }
    
    
}