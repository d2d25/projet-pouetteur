using Microsoft.EntityFrameworkCore;
using profile_service.Models;

namespace profile_service.Data;

public class DataContext : DbContext
{
    public DataContext(DbContextOptions<DataContext> options, DbSet<Member> members, DbSet<Community> communities, DbSet<IProfile> profiles) : base(options)
    {
        Members = members;
        Communities = communities;
    }

    
    //Chaîne de connection : Server=localhost\SQLEXPRESS;Database=master;Trusted_Connection=True;
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        base.OnConfiguring(optionsBuilder);
        optionsBuilder.UseSqlServer("Server=localhost\\SQLEXPRESS;Database=master;Trusted_Connection=True;TrustServerCertificate=True;");
    }
    
    public DbSet<Member> Members { get; set; }
    public DbSet<Community> Communities { get; set; }
}