@echo off
echo ============================================
echo    Generate and Update Password Hash
echo ============================================
echo.

echo Checking if server is running...
curl -s http://localhost:8080/api/auth/generate-hash > hash-response.json 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Server is not running or endpoint is not available.
    echo Please start the Java server first, then run this script again.
    echo.
    pause
    exit /b 1
)

echo Server is running. Generating hash...
echo.

REM Extract hash from JSON response (simple approach - using PowerShell)
powershell -Command "$json = Get-Content hash-response.json | ConvertFrom-Json; $hash = $json.hash; Write-Host 'Generated Hash:'; Write-Host $hash; Write-Host ''; Write-Host 'Updating database...'; $sql = \"UPDATE users SET password_hash = '$hash' WHERE username = 'admin';\"; Write-Host $sql; $sql | Out-File -Encoding ASCII update-hash.sql"

echo.
echo Please enter PostgreSQL password when prompted:
psql -U postgres -d warehouse_db -f update-hash.sql

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo Password hash updated successfully!
    echo ============================================
    echo.
    echo You can now login with:
    echo   Username: admin
    echo   Password: password123
    echo.
) else (
    echo.
    echo ERROR: Failed to update password hash.
    echo.
)

del hash-response.json 2>nul
del update-hash.sql 2>nul
pause

