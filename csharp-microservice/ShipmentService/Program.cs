using ShipmentService.Data;
using ShipmentService.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddGrpc();

// Add DbContext
builder.Services.AddDbContext<ShipmentDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Add application services
builder.Services.AddScoped<IShipmentRepository, ShipmentRepository>();

// Configure Kestrel for HTTP/2 (required for gRPC)
builder.WebHost.ConfigureKestrel(options =>
{
    // Setup HTTP/2 endpoint without TLS for development
    options.ListenLocalhost(5001, o => o.Protocols = Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http2);
});

var app = builder.Build();

// Configure the HTTP request pipeline
app.MapGrpcService<ShipmentGrpcService>();

app.MapGet("/", () => "Shipment gRPC Service is running. Use a gRPC client to communicate.");

// Apply migrations and seed data on startup
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<ShipmentDbContext>();
    try
    {
        dbContext.Database.Migrate();
    }
    catch (Exception ex)
    {
        var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();
        logger.LogError(ex, "An error occurred while migrating the database.");
    }
}

app.Run();
