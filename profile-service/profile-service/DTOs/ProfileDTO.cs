using System.ComponentModel.DataAnnotations;
using profile_service.Models;

namespace profile_service.DTOs;

public class ProfileDto
{
    [Key]
    public string? Id { get; set; }
    [Required]
    public string? Username { get; set; }
    public string? Name { get; set; }
    public DateTime? CreationDate { get; set; }
    public string? ProfilePhoto { get; set; }
    public string? ProfileBanner { get; set; }
    public string? Bio { get; set; }
    public List<ProfileDto>? Followings { get; set; }
    public List<ProfileDto>? Followers { get; set; }
    public Dictionary<Reaction,int>? Reactions { get; set; }
    public string? Email { get; set; }
    public string? Roles { get; set; }
    public DateTime? Birthdate { get; set; }
    public List<ProfileDto>? Members { get; set; }
    public ProfileDto? Owner { get; set; }
}