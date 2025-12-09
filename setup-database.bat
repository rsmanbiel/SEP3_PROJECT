@echo off
echo ============================================
echo    SEP3 Database Setup
echo ============================================
echo.

echo This will create and seed the databases.
echo PostgreSQL password will be required.
echo.

set /p "PGPASSWORD=Enter PostgreSQL password: "

echo.
echo [1/4] Creating warehouse_db database...
psql -U postgres -c "CREATE DATABASE warehouse_db;" 2>nul
if %errorlevel% equ 0 (
    echo       Database warehouse_db created.
) else (
    echo       Database warehouse_db already exists or error occurred.
)

echo [2/4] Creating shipment_db database...
psql -U postgres -c "CREATE DATABASE shipment_db;" 2>nul
if %errorlevel% equ 0 (
    echo       Database shipment_db created.
) else (
    echo       Database shipment_db already exists or error occurred.
)

echo [3/4] Running schema.sql...
psql -U postgres -d warehouse_db -f "%~dp0database\schema.sql"

echo [4/4] Running seed.sql...
psql -U postgres -d warehouse_db -f "%~dp0database\seed.sql"

echo.
echo ============================================
echo Database setup complete!
echo ============================================
echo.
echo Default users created:
echo   admin / password123
echo   supervisor1 / password123
echo   operator1 / password123
echo   customer1 / password123
echo.
pause


