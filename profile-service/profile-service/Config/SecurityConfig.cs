using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

namespace profile_service.Config;

public class SecurityConfig
{
    private static readonly string? BasePath = Path.GetDirectoryName(System.Reflection.Assembly.GetEntryAssembly()?.Location);
    private static readonly string PublicKeyPath = Path.Combine(Directory.GetCurrentDirectory(), "access-refresh-token-keys", "access-token-public.key");
    private static readonly string AbsolutePublicKeyPath = Path.Combine(BasePath!, PublicKeyPath);
    private static readonly string PublicKey = File.ReadAllText(AbsolutePublicKeyPath);

    public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
    {
        app.UseRouting();
        
        // Add authentication middleware to the pipeline
        app.UseAuthentication();
        app.UseAuthorization();

        app.UseEndpoints(endpoints =>
        {
            endpoints.MapControllers();
        });
    }
    
    public void ConfigureServices(IServiceCollection services)
    {
        // Configure authentication using JWT token
        services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
            .AddJwtBearer(options =>
            {
                // Set the authority URL and client ID to retrieve OAuth2 tokens
                options.Authority = "https://localhost:5001";
                options.Audience = "profile_service";
                
                // Configure token validation parameters
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true,
                    ValidIssuer = "https://localhost:5001",
                    ValidateAudience = true,
                    ValidAudience = "profile_service",
                    ValidateLifetime = true,
                    ClockSkew = TimeSpan.Zero,
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(PublicKey))
                };
            });
        
        services.AddControllers();
    }
}