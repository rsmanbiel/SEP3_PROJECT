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
echo [1/5] Dropping existing databases (if they exist)...
psql -U postgres -c "DROP DATABASE IF EXISTS warehouse_db;" 2>nul
psql -U postgres -c "DROP DATABASE IF EXISTS shipment_db;" 2>nul
echo       Existing databases dropped.

echo [2/5] Creating warehouse_db database...
psql -U postgres -c "CREATE DATABASE warehouse_db;" 2>nul
if %errorlevel% equ 0 (
    echo       Database warehouse_db created successfully.
) else (
    echo       ERROR: Failed to create warehouse_db database.
    pause
    exit /b 1
)

echo [3/5] Creating shipment_db database...
psql -U postgres -c "CREATE DATABASE shipment_db;" 2>nul
if %errorlevel% equ 0 (
    echo       Database shipment_db created successfully.
) else (
    echo       ERROR: Failed to create shipment_db database.
    pause
    exit /b 1
)

echo [4/5] Running schema.sql...
psql -U postgres -d warehouse_db -f "%~dp0database\schema.sql"
if %errorlevel% neq 0 (
    echo       ERROR: Failed to run schema.sql
    pause
    exit /b 1
)

echo [5/5] Running seed.sql...
psql -U postgres -d warehouse_db -f "%~dp0database\seed.sql"
if %errorlevel% neq 0 (
    echo       WARNING: Some seed data may have failed (duplicates are OK).
)

echo.
echo ============================================
echo Database setup complete!
echo ============================================
echo.
echo Default users created (all use password: password123):
echo   - admin        (ADMIN role)
echo   - supervisor1  (SUPERVISOR role)
echo   - operator1    (WAREHOUSE_OPERATOR role)
echo   - customer1    (CUSTOMER role)
echo.

pause
pause

pause


