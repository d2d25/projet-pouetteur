using Consul;

namespace profile_service;

public class ConsulRegistryService : IConsulRegistryService
{
    private readonly IConsulClient _consulClient;
    public const string ServiceName = "profile-service";
    
    public ConsulRegistryService(IConsulClient consulClient)
    {
        _consulClient = consulClient;
        
    }
    
    public Uri GetService()
    {
        var serviceQueryResult = _consulClient.Health.Service(ServiceName).Result;

        if (serviceQueryResult.Response == null ||
            serviceQueryResult.Response.Length <= 0) return null!;
        var service = serviceQueryResult.Response[0];
        return new Uri($"{service.Service.Address}:{service.Service.Port}");

    }

}