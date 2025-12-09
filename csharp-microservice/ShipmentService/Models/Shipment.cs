using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ShipmentService.Models;

/// <summary>
/// Entity representing a shipment in the system.
/// </summary>
[Table("shipments")]
public class Shipment
{
    [Key]
    [Column("id")]
    public long Id { get; set; }
    
    [Required]
    [Column("order_id")]
    public long OrderId { get; set; }
    
    [Required]
    [MaxLength(50)]
    [Column("tracking_number")]
    public string TrackingNumber { get; set; } = string.Empty;
    
    [Required]
    [Column("status")]
    public ShipmentStatus Status { get; set; } = ShipmentStatus.Pending;
    
    [Required]
    [MaxLength(100)]
    [Column("recipient_name")]
    public string RecipientName { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(255)]
    [Column("recipient_address")]
    public string RecipientAddress { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(100)]
    [Column("recipient_city")]
    public string RecipientCity { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(20)]
    [Column("recipient_postal_code")]
    public string RecipientPostalCode { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(100)]
    [Column("recipient_country")]
    public string RecipientCountry { get; set; } = string.Empty;
    
    [MaxLength(20)]
    [Column("recipient_phone")]
    public string? RecipientPhone { get; set; }
    
    [Column("weight_kg")]
    public double WeightKg { get; set; }
    
    [MaxLength(100)]
    [Column("current_location")]
    public string? CurrentLocation { get; set; }
    
    [Column("estimated_delivery")]
    public DateTime? EstimatedDelivery { get; set; }
    
    [Column("notes")]
    public string? Notes { get; set; }
    
    [Column("created_at")]
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    
    [Column("updated_at")]
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
    
    // Navigation property
    public ICollection<ShipmentHistory> History { get; set; } = new List<ShipmentHistory>();
}

/// <summary>
/// Enum representing shipment status.
/// </summary>
public enum ShipmentStatus
{
    Pending = 1,
    Processing = 2,
    Shipped = 3,
    InTransit = 4,
    OutForDelivery = 5,
    Delivered = 6,
    Cancelled = 7,
    Returned = 8
}
