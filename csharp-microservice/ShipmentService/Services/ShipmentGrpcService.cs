using Grpc.Core;
using ShipmentService.Data;
using ShipmentService.Grpc;
using ShipmentService.Models;

namespace ShipmentService.Services;

/// <summary>
/// gRPC service implementation for shipment operations.
/// This service handles all gRPC calls from the Java server.
/// </summary>
public class ShipmentGrpcService : Grpc.ShipmentService.ShipmentServiceBase
{
    private readonly IShipmentRepository _repository;
    private readonly ILogger<ShipmentGrpcService> _logger;
    
    public ShipmentGrpcService(IShipmentRepository repository, ILogger<ShipmentGrpcService> logger)
    {
        _repository = repository;
        _logger = logger;
    }
    
    /// <summary>
    /// Create a new shipment.
    /// </summary>
    public override async Task<ShipmentResponse> CreateShipment(
        CreateShipmentRequest request, 
        ServerCallContext context)
    {
        _logger.LogInformation("Creating shipment for order: {OrderId}", request.OrderId);
        
        try
        {
            // Generate tracking number
            var trackingNumber = GenerateTrackingNumber();
            
            // Calculate estimated delivery (3-5 business days)
            var estimatedDelivery = DateTime.UtcNow.AddDays(4);
            
            var shipment = new Models.Shipment
            {
                OrderId = request.OrderId,
                TrackingNumber = trackingNumber,
                Status = Models.ShipmentStatus.Pending,
                RecipientName = request.RecipientName,
                RecipientAddress = request.RecipientAddress,
                RecipientCity = request.RecipientCity,
                RecipientPostalCode = request.RecipientPostalCode,
                RecipientCountry = request.RecipientCountry,
                RecipientPhone = request.RecipientPhone,
                WeightKg = request.WeightKg,
                CurrentLocation = "Warehouse",
                EstimatedDelivery = estimatedDelivery,
                Notes = request.Notes
            };
            
            var createdShipment = await _repository.CreateAsync(shipment);
            
            // Add initial history entry
            await _repository.AddHistoryAsync(new ShipmentHistory
            {
                ShipmentId = createdShipment.Id,
                Status = Models.ShipmentStatus.Pending,
                Location = "Warehouse",
                Notes = "Shipment created"
            });
            
            _logger.LogInformation("Shipment created with tracking number: {TrackingNumber}", trackingNumber);
            
            return new ShipmentResponse
            {
                Success = true,
                Message = "Shipment created successfully",
                Shipment = MapToGrpcShipment(createdShipment)
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating shipment for order: {OrderId}", request.OrderId);
            return new ShipmentResponse
            {
                Success = false,
                Message = $"Error creating shipment: {ex.Message}"
            };
        }
    }
    
    /// <summary>
    /// Get shipment by ID.
    /// </summary>
    public override async Task<ShipmentResponse> GetShipment(
        GetShipmentRequest request, 
        ServerCallContext context)
    {
        _logger.LogDebug("Getting shipment: {ShipmentId}", request.ShipmentId);
        
        var shipment = await _repository.GetByIdAsync(request.ShipmentId);
        
        if (shipment == null)
        {
            return new ShipmentResponse
            {
                Success = false,
                Message = $"Shipment with ID {request.ShipmentId} not found"
            };
        }
        
        return new ShipmentResponse
        {
            Success = true,
            Shipment = MapToGrpcShipment(shipment)
        };
    }
    
    /// <summary>
    /// Get shipment by order ID.
    /// </summary>
    public override async Task<ShipmentResponse> GetShipmentByOrderId(
        GetShipmentByOrderIdRequest request, 
        ServerCallContext context)
    {
        _logger.LogDebug("Getting shipment for order: {OrderId}", request.OrderId);
        
        var shipment = await _repository.GetByOrderIdAsync(request.OrderId);
        
        if (shipment == null)
        {
            return new ShipmentResponse
            {
                Success = false,
                Message = $"Shipment for order {request.OrderId} not found"
            };
        }
        
        return new ShipmentResponse
        {
            Success = true,
            Shipment = MapToGrpcShipment(shipment)
        };
    }
    
    /// <summary>
    /// Update shipment status.
    /// </summary>
    public override async Task<ShipmentResponse> UpdateShipmentStatus(
        UpdateShipmentStatusRequest request, 
        ServerCallContext context)
    {
        _logger.LogInformation("Updating shipment {ShipmentId} status to: {Status}", 
            request.ShipmentId, request.Status);
        
        var shipment = await _repository.GetByIdAsync(request.ShipmentId);
        
        if (shipment == null)
        {
            return new ShipmentResponse
            {
                Success = false,
                Message = $"Shipment with ID {request.ShipmentId} not found"
            };
        }
        
        // Update shipment status
        shipment.Status = MapFromGrpcStatus(request.Status);
        if (!string.IsNullOrEmpty(request.Location))
        {
            shipment.CurrentLocation = request.Location;
        }
        
        await _repository.UpdateAsync(shipment);
        
        // Add history entry
        await _repository.AddHistoryAsync(new ShipmentHistory
        {
            ShipmentId = shipment.Id,
            Status = shipment.Status,
            Location = request.Location,
            Notes = request.Notes
        });
        
        _logger.LogInformation("Shipment {ShipmentId} status updated to: {Status}", 
            request.ShipmentId, shipment.Status);
        
        // Reload with history
        shipment = await _repository.GetByIdAsync(request.ShipmentId);
        
        return new ShipmentResponse
        {
            Success = true,
            Message = "Shipment status updated successfully",
            Shipment = MapToGrpcShipment(shipment!)
        };
    }
    
    /// <summary>
    /// Get all shipments.
    /// </summary>
    public override async Task<ShipmentListResponse> GetAllShipments(
        GetAllShipmentsRequest request, 
        ServerCallContext context)
    {
        _logger.LogDebug("Getting all shipments - Page: {Page}, Size: {Size}", request.Page, request.Size);
        
        Models.ShipmentStatus? statusFilter = null;
        if (request.StatusFilter != Grpc.ShipmentStatus.Unspecified)
        {
            statusFilter = MapFromGrpcStatus(request.StatusFilter);
        }
        
        var shipments = await _repository.GetAllAsync(request.Page, request.Size, statusFilter);
        var totalCount = await _repository.GetTotalCountAsync(statusFilter);
        
        var response = new ShipmentListResponse
        {
            Success = true,
            TotalCount = totalCount,
            Page = request.Page,
            Size = request.Size
        };
        
        foreach (var shipment in shipments)
        {
            response.Shipments.Add(MapToGrpcShipment(shipment));
        }
        
        return response;
    }
    
    /// <summary>
    /// Cancel a shipment.
    /// </summary>
    public override async Task<ShipmentResponse> CancelShipment(
        CancelShipmentRequest request, 
        ServerCallContext context)
    {
        _logger.LogInformation("Cancelling shipment: {ShipmentId}", request.ShipmentId);
        
        var shipment = await _repository.GetByIdAsync(request.ShipmentId);
        
        if (shipment == null)
        {
            return new ShipmentResponse
            {
                Success = false,
                Message = $"Shipment with ID {request.ShipmentId} not found"
            };
        }
        
        if (shipment.Status == Models.ShipmentStatus.Delivered)
        {
            return new ShipmentResponse
            {
                Success = false,
                Message = "Cannot cancel a delivered shipment"
            };
        }
        
        shipment.Status = Models.ShipmentStatus.Cancelled;
        await _repository.UpdateAsync(shipment);
        
        // Add history entry
        await _repository.AddHistoryAsync(new ShipmentHistory
        {
            ShipmentId = shipment.Id,
            Status = Models.ShipmentStatus.Cancelled,
            Notes = $"Cancelled: {request.Reason}"
        });
        
        // Reload with history
        shipment = await _repository.GetByIdAsync(request.ShipmentId);
        
        return new ShipmentResponse
        {
            Success = true,
            Message = "Shipment cancelled successfully",
            Shipment = MapToGrpcShipment(shipment!)
        };
    }
    
    /// <summary>
    /// Stream shipment updates (real-time tracking).
    /// </summary>
    public override async Task StreamShipmentUpdates(
        StreamShipmentRequest request,
        IServerStreamWriter<ShipmentUpdate> responseStream,
        ServerCallContext context)
    {
        _logger.LogInformation("Starting shipment update stream for: {ShipmentId}", request.ShipmentId);
        
        var lastStatus = Models.ShipmentStatus.Pending;
        
        while (!context.CancellationToken.IsCancellationRequested)
        {
            var shipment = await _repository.GetByIdAsync(request.ShipmentId);
            
            if (shipment != null && shipment.Status != lastStatus)
            {
                await responseStream.WriteAsync(new ShipmentUpdate
                {
                    ShipmentId = shipment.Id,
                    Status = MapToGrpcStatus(shipment.Status),
                    Location = shipment.CurrentLocation ?? "",
                    Timestamp = shipment.UpdatedAt.ToString("O"),
                    Notes = shipment.Notes ?? ""
                });
                
                lastStatus = shipment.Status;
                
                // Stop streaming if shipment is delivered or cancelled
                if (shipment.Status == Models.ShipmentStatus.Delivered ||
                    shipment.Status == Models.ShipmentStatus.Cancelled)
                {
                    break;
                }
            }
            
            // Wait before next check
            await Task.Delay(5000, context.CancellationToken);
        }
    }
    
    #region Helper Methods
    
    private static string GenerateTrackingNumber()
    {
        var timestamp = DateTime.UtcNow.ToString("yyyyMMddHHmmss");
        var random = new Random().Next(1000, 9999);
        return $"SHP{timestamp}{random}";
    }
    
    private static Grpc.Shipment MapToGrpcShipment(Models.Shipment shipment)
    {
        var grpcShipment = new Grpc.Shipment
        {
            Id = shipment.Id,
            OrderId = shipment.OrderId,
            TrackingNumber = shipment.TrackingNumber,
            Status = MapToGrpcStatus(shipment.Status),
            RecipientName = shipment.RecipientName,
            RecipientAddress = shipment.RecipientAddress,
            RecipientCity = shipment.RecipientCity,
            RecipientPostalCode = shipment.RecipientPostalCode,
            RecipientCountry = shipment.RecipientCountry,
            RecipientPhone = shipment.RecipientPhone ?? "",
            WeightKg = shipment.WeightKg,
            CurrentLocation = shipment.CurrentLocation ?? "",
            EstimatedDelivery = shipment.EstimatedDelivery?.ToString("O") ?? "",
            CreatedAt = shipment.CreatedAt.ToString("O"),
            UpdatedAt = shipment.UpdatedAt.ToString("O"),
            Notes = shipment.Notes ?? ""
        };
        
        foreach (var history in shipment.History.OrderBy(h => h.Timestamp))
        {
            grpcShipment.History.Add(new ShipmentHistoryEntry
            {
                Id = history.Id,
                Status = MapToGrpcStatus(history.Status),
                Location = history.Location ?? "",
                Timestamp = history.Timestamp.ToString("O"),
                Notes = history.Notes ?? ""
            });
        }
        
        return grpcShipment;
    }
    
    private static Grpc.ShipmentStatus MapToGrpcStatus(Models.ShipmentStatus status)
    {
        return status switch
        {
            Models.ShipmentStatus.Pending => Grpc.ShipmentStatus.Pending,
            Models.ShipmentStatus.Processing => Grpc.ShipmentStatus.Processing,
            Models.ShipmentStatus.Shipped => Grpc.ShipmentStatus.Shipped,
            Models.ShipmentStatus.InTransit => Grpc.ShipmentStatus.InTransit,
            Models.ShipmentStatus.OutForDelivery => Grpc.ShipmentStatus.OutForDelivery,
            Models.ShipmentStatus.Delivered => Grpc.ShipmentStatus.Delivered,
            Models.ShipmentStatus.Cancelled => Grpc.ShipmentStatus.Cancelled,
            Models.ShipmentStatus.Returned => Grpc.ShipmentStatus.Returned,
            _ => Grpc.ShipmentStatus.Unspecified
        };
    }
    
    private static Models.ShipmentStatus MapFromGrpcStatus(Grpc.ShipmentStatus status)
    {
        return status switch
        {
            Grpc.ShipmentStatus.Pending => Models.ShipmentStatus.Pending,
            Grpc.ShipmentStatus.Processing => Models.ShipmentStatus.Processing,
            Grpc.ShipmentStatus.Shipped => Models.ShipmentStatus.Shipped,
            Grpc.ShipmentStatus.InTransit => Models.ShipmentStatus.InTransit,
            Grpc.ShipmentStatus.OutForDelivery => Models.ShipmentStatus.OutForDelivery,
            Grpc.ShipmentStatus.Delivered => Models.ShipmentStatus.Delivered,
            Grpc.ShipmentStatus.Cancelled => Models.ShipmentStatus.Cancelled,
            Grpc.ShipmentStatus.Returned => Models.ShipmentStatus.Returned,
            _ => Models.ShipmentStatus.Pending
        };
    }
    
    #endregion
}
