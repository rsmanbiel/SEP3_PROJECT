using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ShipmentService.Models;

/// <summary>
/// Entity representing a history entry for shipment tracking.
/// </summary>
[Table("shipment_history")]
public class ShipmentHistory
{
    [Key]
    [Column("id")]
    public long Id { get; set; }
    
    [Required]
    [Column("shipment_id")]
    public long ShipmentId { get; set; }
    
    [Required]
    [Column("status")]
    public ShipmentStatus Status { get; set; }
    
    [MaxLength(100)]
    [Column("location")]
    public string? Location { get; set; }
    
    [Column("timestamp")]
    public DateTime Timestamp { get; set; } = DateTime.UtcNow;
    
    [Column("notes")]
    public string? Notes { get; set; }
    
    // Navigation property
    [ForeignKey("ShipmentId")]
    public Shipment? Shipment { get; set; }
}
