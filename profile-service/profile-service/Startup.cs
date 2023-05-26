using Consul;
using Microsoft.Extensions.Options;
using profile_service.Config;
using profile_service.Data;
using profile_service.Services;

namespace profile_service
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
            services.AddSwaggerGen();
            services.AddDbContext<DataContext>();
            services.AddControllers();
            services.AddCors();
            services.AddRouting(options => options.LowercaseUrls = true);
            services.Configure<ProfileConfiguration>(Configuration.GetSection("Profile"));
            services.Configure<ConsulConfiguration>(Configuration.GetSection("Consul"));

            var consulAddress = Configuration.GetSection("Consul")["Url"];

            // securityConfig = new SecurityConfig();
            //securityConfig.ConfigureServices(services);  
            services.AddSingleton<IConsulClient, ConsulClient>(provider =>
                new ConsulClient(config => config.Address = new Uri(consulAddress ?? throw new InvalidOperationException())));

            services.AddSingleton<IConsulRegistryService, ConsulRegistryService>();
            services.AddHostedService<ConsulRegisterService>();
            services.AddHealthChecks();
            //.AddSingleton<SecurityConfig>();
            services.AddHttpClient<IProfileService, ProfileService>((serviceProvider, client) =>
            {
                var consulRegistryService = serviceProvider.GetRequiredService<IConsulRegistryService>();
                
                client.BaseAddress = consulRegistryService.GetService();
            });
            }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            app.UseRouting();
            app.UseDeveloperExceptionPage();
            app.UseRouting();
            app.UseStaticFiles();
            app.UseSwagger();
            app.UseAuthorization();
            app.UseSwaggerUI(
                ui =>
                {
                    ui.SwaggerEndpoint("swagger/v1/swagger.json", "Profile v1");
                    ui.RoutePrefix = "";
                }
            );
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
    public class ConsulConfiguration
    {
        public string? Url { get; set; }
    }
    
    public class ProfileConfiguration
    {
        public string? Url { get; set; }
        public string? ServiceName { get; set; }
        public string? ServiceId { get; set; }
    }
    
    public class ConsulRegisterService : IHostedService
    {
        private readonly IConsulClient _consulClient;
        private ConsulConfiguration _consulConfiguration;
        private readonly ProfileConfiguration _profileConfiguration;
        private readonly ILogger<ConsulRegisterService> _logger;

        public ConsulRegisterService(IConsulClient consulClient,
            IOptions<ProfileConfiguration> profileConfiguration,
            IOptions<ConsulConfiguration> consulConfiguration,
            ILogger<ConsulRegisterService> logger)
        {
            _consulClient = consulClient;
            _consulConfiguration = consulConfiguration.Value;
            _profileConfiguration = profileConfiguration.Value;
            _logger = logger;
        }

        public async Task StartAsync(CancellationToken cancellationToken)
        {
            var profileUri = new Uri(_profileConfiguration.Url ?? throw new InvalidOperationException());

            var serviceRegistration = new AgentServiceRegistration()
            {
                Address = profileUri.Host,
                Name = _profileConfiguration.ServiceName,
                Port = profileUri.Port,
                ID = _profileConfiguration.ServiceId,
                Tags = new[] { _profileConfiguration.ServiceName }
            };

            await _consulClient.Agent.ServiceDeregister(_profileConfiguration.ServiceId, cancellationToken);
            await _consulClient.Agent.ServiceRegister(serviceRegistration, cancellationToken);

        }

        public async Task StopAsync(CancellationToken cancellationToken)
        {
            try
            {
                await _consulClient.Agent.ServiceDeregister(_profileConfiguration.ServiceId, cancellationToken);
            }
            catch (Exception e)
            {
                _logger.LogError("Error when trying to de-register", e);
            }
        }
    }
}