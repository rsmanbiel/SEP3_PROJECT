@echo off
echo ============================================
echo    SEP3 Warehouse Management System
echo ============================================
echo.

echo Starting all services...
echo.

REM Start C# Microservice (ShipmentService) in new window
echo [1/3] Starting C# Shipment Microservice...
start "C# Shipment Service" cmd /k "cd /d %~dp0csharp-microservice\ShipmentService && dotnet run"

REM Wait a few seconds for the gRPC service to start
timeout /t 5 /nobreak > nul

REM Start Java Server in new window
echo [2/3] Starting Java Server (Spring Boot)...
start "Java Server" cmd /k "cd /d %~dp0java-server && mvn spring-boot:run"

REM Wait for Java server to start
timeout /t 10 /nobreak > nul

REM Start JavaFX Client in new window
echo [3/3] Starting JavaFX Client...
start "JavaFX Client" cmd /k "cd /d %~dp0javafx-client && mvn javafx:run"

echo.
echo ============================================
echo All services started in separate windows!
echo ============================================
echo.
echo Services:
echo   - C# Shipment Service: http://localhost:5001 (gRPC)
echo   - Java Server:         http://localhost:8080 (REST)
echo   - JavaFX Client:       GUI Application
echo.
echo Default login credentials:
echo   Username: admin
echo   Password: password123
echo.
echo To stop all services, close each terminal window.
echo ============================================
pause


