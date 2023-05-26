using Microsoft.AspNetCore.Mvc;
using profile_service.DTOs;
using profile_service.Services;
using profile_service.Services.Exception;

namespace profile_service.Controllers;

[ApiController]
[Route("[controller]")]
public class ProfileController : ControllerBase
{
    private IProfileService ProfileService { get; }
    
    public ProfileController(IProfileService profileService)
    {
        ProfileService = profileService;
    }
    
    /**
     * Get a profile by username
     */
    [HttpGet("{username}" ,Name = "GetProfileByUsername")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public ActionResult<ProfileDto> GetProfileByUsername(string username)
    {
        try
        {
            return Ok(ProfileService.GetProfileByUsername(username));
        }
        catch (NotFoundException e)
        {
            return NotFound(e.Message);
        }
        
    }
    
    /**
     * Get a profile by id
     */
    [HttpGet("id/{id}" ,Name = "GetProfileById")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public ActionResult<ProfileDto> GetProfileById(string id)
    {
        try
        {
            return Ok(ProfileService.GetProfileById(id));
        }
        catch (NotFoundException e)
        {
            return NotFound(e.Message);
        }
    }
    
    /**
     * Create a profile
     */
    [HttpPost(Name = "CreateProfile")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public ActionResult<ProfileDto> CreateProfile(ProfileDto profileDto)
    {
        try
        {
            return CreatedAtRoute("GetProfileByUsername", new {username = profileDto.Username}, ProfileService.CreateProfile(profileDto));
        }
        catch (BadRequestException e)
        {
            return BadRequest(e.Message);
        }
    }
    
    /**
     * Update a profile
     */
    [HttpPut("{id}", Name = "UpdateProfile")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public ActionResult<ProfileDto> UpdateProfile(string id, ProfileDto profileDto)
    {
        var userId = User.Claims.FirstOrDefault(c => c.Type == "sub")?.Value;
        if (userId != id)
        {
            return Unauthorized();
        }
        
        try
        {
            return Ok(ProfileService.UpdateProfile(id, profileDto));
        }
        catch (BadRequestException e)
        {
            return BadRequest(e.Message);
        }
        catch (NotFoundException e)
        {
            return NotFound(e.Message);
        }
    }
    
    /**
     * Delete a profile
     */
    [HttpDelete("{id}", Name = "DeleteProfile")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    public ActionResult<ProfileDto> DeleteProfile(string id)
    {
        var userId = User.Claims.FirstOrDefault(c => c.Type == "sub")?.Value;
        if (userId != id)
        {
            return Unauthorized();
        }
        
        try
        {
            ProfileService.DeleteProfile(id);
            return Ok();
        }
        catch (NotFoundException e)
        {
            return NotFound(e.Message);
        }
    }
    
    /**
     * Get all profiles
     */
    
    [HttpGet(Name = "GetAllProfiles")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public ActionResult<IEnumerable<ProfileDto>> GetAllProfiles()
    {
        return Ok(ProfileService.GetAllProfiles());
    }

}