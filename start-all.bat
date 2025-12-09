@echo off
echo ============================================
echo    SEP3 Warehouse Management System
echo    Starting All Services
echo ============================================
echo.

REM Check if databases exist
echo Checking databases...
psql -U postgres -lqt 2>nul | findstr /C:"warehouse_db" >nul
if %errorlevel% neq 0 (
    echo WARNING: Database 'warehouse_db' not found!
    echo Please run setup-database.bat first.
    echo.
    pause
    exit /b 1
)

echo Databases OK. Starting services...
echo.

REM Start C# Microservice (ShipmentService) in new window
echo [1/3] Starting C# Shipment Microservice...
start "C# Shipment Service" cmd /k "cd /d %~dp0csharp-microservice\ShipmentService && echo Starting C# Shipment Service... && dotnet run"

REM Wait for the gRPC service to start
echo       Waiting for gRPC service to initialize...
timeout /t 8 /nobreak > nul

REM Start Java Server in new window
echo [2/3] Starting Java Server (Spring Boot)...
start "Java Server" cmd /k "cd /d %~dp0java-server && echo Starting Java Server... && mvn spring-boot:run"

REM Wait for Java server to start
echo       Waiting for Java server to initialize...
timeout /t 15 /nobreak > nul

REM Start JavaFX Client in new window
echo [3/3] Starting JavaFX Client...
start "JavaFX Client" cmd /k "cd /d %~dp0javafx-client && echo Starting JavaFX Client... && mvn javafx:run"

echo.
echo ============================================
echo All services started in separate windows!
echo ============================================
echo.
echo Services:
echo   - C# Shipment Service: http://localhost:5001 (gRPC)
echo   - Java Server:         http://localhost:8080 (REST API)
echo   - JavaFX Client:       GUI Application
echo.
echo Default login credentials:
echo   Username: admin
echo   Password: password123
echo.
echo Other test users (all use password: password123):
echo   - supervisor1, operator1, customer1
echo.
echo To stop all services, close each terminal window.
echo To reset admin password, run: reset-admin-password.bat
echo ============================================
pause


