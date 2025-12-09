using Microsoft.EntityFrameworkCore;
using ShipmentService.Models;

namespace ShipmentService.Data;

/// <summary>
/// Database context for the Shipment microservice.
/// </summary>
public class ShipmentDbContext : DbContext
{
    public ShipmentDbContext(DbContextOptions<ShipmentDbContext> options) : base(options)
    {
    }
    
    public DbSet<Shipment> Shipments { get; set; } = null!;
    public DbSet<ShipmentHistory> ShipmentHistories { get; set; } = null!;
    
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        
        // Configure Shipment entity
        modelBuilder.Entity<Shipment>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.HasIndex(e => e.OrderId);
            entity.HasIndex(e => e.TrackingNumber).IsUnique();
            entity.HasIndex(e => e.Status);
            
            entity.Property(e => e.Status)
                .HasConversion<string>();
            
            entity.HasMany(e => e.History)
                .WithOne(h => h.Shipment)
                .HasForeignKey(h => h.ShipmentId)
                .OnDelete(DeleteBehavior.Cascade);
        });
        
        // Configure ShipmentHistory entity
        modelBuilder.Entity<ShipmentHistory>(entity =>
        {
            entity.HasKey(e => e.Id);
            
            entity.HasIndex(e => e.ShipmentId);
            
            entity.Property(e => e.Status)
                .HasConversion<string>();
        });
    }
}
