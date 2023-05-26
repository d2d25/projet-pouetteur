using AutoMapper;
using profile_service.DTOs;
using profile_service.Models;
using Member = AutoMapper.Execution.Member;

namespace profile_service.Config;

public class MapperConfig
{
    public static Mapper InitializeAutoMapper()
    {
        //Provide all the Mapping Configurations
        var config = new MapperConfiguration(cfg =>
        {
            //Configuring the mapping between DTO and Models
            cfg.CreateMap<ProfileDto, Member>();
            cfg.CreateMap<Member, ProfileDto>();
            cfg.CreateMap<ProfileDto, Community>();
            cfg.CreateMap<Community, ProfileDto>();
        });
        
        //Create the Mapper
        var mapper = new Mapper(config);
        return mapper;
    }
}