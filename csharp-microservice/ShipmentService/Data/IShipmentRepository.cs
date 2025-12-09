using ShipmentService.Models;

namespace ShipmentService.Data;

/// <summary>
/// Repository interface for shipment data operations.
/// </summary>
public interface IShipmentRepository
{
    Task<Shipment?> GetByIdAsync(long id);
    Task<Shipment?> GetByOrderIdAsync(long orderId);
    Task<Shipment?> GetByTrackingNumberAsync(string trackingNumber);
    Task<IEnumerable<Shipment>> GetAllAsync(int page, int size, ShipmentStatus? statusFilter = null);
    Task<int> GetTotalCountAsync(ShipmentStatus? statusFilter = null);
    Task<Shipment> CreateAsync(Shipment shipment);
    Task<Shipment> UpdateAsync(Shipment shipment);
    Task<ShipmentHistory> AddHistoryAsync(ShipmentHistory history);
    Task<IEnumerable<ShipmentHistory>> GetHistoryAsync(long shipmentId);
}
