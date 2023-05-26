namespace profile_service.Models;

public interface  IProfile
{
    public string? Id { get; set; }
    public string? Username { get; set; }
    public string? Name { get; set; }
    public DateTime? CreationDate { get; set; }
    public string? ProfilePhoto { get; set; }
    public string? ProfileBanner { get; set; }
    public string? Bio { get; set; }
}