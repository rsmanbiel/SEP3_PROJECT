using Microsoft.EntityFrameworkCore;
using ShipmentService.Models;

namespace ShipmentService.Data;

/// <summary>
/// Repository implementation for shipment data operations.
/// </summary>
public class ShipmentRepository : IShipmentRepository
{
    private readonly ShipmentDbContext _context;
    private readonly ILogger<ShipmentRepository> _logger;
    
    public ShipmentRepository(ShipmentDbContext context, ILogger<ShipmentRepository> logger)
    {
        _context = context;
        _logger = logger;
    }
    
    public async Task<Shipment?> GetByIdAsync(long id)
    {
        _logger.LogDebug("Getting shipment by id: {Id}", id);
        return await _context.Shipments
            .Include(s => s.History.OrderByDescending(h => h.Timestamp))
            .FirstOrDefaultAsync(s => s.Id == id);
    }
    
    public async Task<Shipment?> GetByOrderIdAsync(long orderId)
    {
        _logger.LogDebug("Getting shipment by order id: {OrderId}", orderId);
        return await _context.Shipments
            .Include(s => s.History.OrderByDescending(h => h.Timestamp))
            .FirstOrDefaultAsync(s => s.OrderId == orderId);
    }
    
    public async Task<Shipment?> GetByTrackingNumberAsync(string trackingNumber)
    {
        _logger.LogDebug("Getting shipment by tracking number: {TrackingNumber}", trackingNumber);
        return await _context.Shipments
            .Include(s => s.History.OrderByDescending(h => h.Timestamp))
            .FirstOrDefaultAsync(s => s.TrackingNumber == trackingNumber);
    }
    
    public async Task<IEnumerable<Shipment>> GetAllAsync(int page, int size, ShipmentStatus? statusFilter = null)
    {
        _logger.LogDebug("Getting all shipments - Page: {Page}, Size: {Size}, Filter: {Filter}", page, size, statusFilter);
        
        var query = _context.Shipments
            .Include(s => s.History.OrderByDescending(h => h.Timestamp))
            .AsQueryable();
        
        if (statusFilter.HasValue)
        {
            query = query.Where(s => s.Status == statusFilter.Value);
        }
        
        return await query
            .OrderByDescending(s => s.CreatedAt)
            .Skip(page * size)
            .Take(size)
            .ToListAsync();
    }
    
    public async Task<int> GetTotalCountAsync(ShipmentStatus? statusFilter = null)
    {
        var query = _context.Shipments.AsQueryable();
        
        if (statusFilter.HasValue)
        {
            query = query.Where(s => s.Status == statusFilter.Value);
        }
        
        return await query.CountAsync();
    }
    
    public async Task<Shipment> CreateAsync(Shipment shipment)
    {
        _logger.LogInformation("Creating new shipment for order: {OrderId}", shipment.OrderId);
        
        shipment.CreatedAt = DateTime.UtcNow;
        shipment.UpdatedAt = DateTime.UtcNow;
        
        _context.Shipments.Add(shipment);
        await _context.SaveChangesAsync();
        
        _logger.LogInformation("Shipment created with id: {Id}", shipment.Id);
        return shipment;
    }
    
    public async Task<Shipment> UpdateAsync(Shipment shipment)
    {
        _logger.LogInformation("Updating shipment: {Id}", shipment.Id);
        
        shipment.UpdatedAt = DateTime.UtcNow;
        
        _context.Shipments.Update(shipment);
        await _context.SaveChangesAsync();
        
        return shipment;
    }
    
    public async Task<ShipmentHistory> AddHistoryAsync(ShipmentHistory history)
    {
        _logger.LogDebug("Adding history entry for shipment: {ShipmentId}", history.ShipmentId);
        
        history.Timestamp = DateTime.UtcNow;
        
        _context.ShipmentHistories.Add(history);
        await _context.SaveChangesAsync();
        
        return history;
    }
    
    public async Task<IEnumerable<ShipmentHistory>> GetHistoryAsync(long shipmentId)
    {
        return await _context.ShipmentHistories
            .Where(h => h.ShipmentId == shipmentId)
            .OrderByDescending(h => h.Timestamp)
            .ToListAsync();
    }
}
