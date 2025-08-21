# PowerShell script to fix Java environment variables
# Run as Administrator

Write-Host "🔧 Java Environment Variables Fixer" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""

# Check if running as administrator
if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "❌ This script requires Administrator privileges!" -ForegroundColor Red
    Write-Host "Right-click on PowerShell and select 'Run as Administrator'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Press any key to exit..." -ForegroundColor Gray
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
}

Write-Host "✅ Running with Administrator privileges" -ForegroundColor Green
Write-Host ""

# Current Java setup
Write-Host "📋 Current Java Configuration:" -ForegroundColor Cyan
Write-Host "Java Version: " -NoNewline
java -version 2>&1 | Select-String "java version" | ForEach-Object { Write-Host $_.ToString() -ForegroundColor White }

$currentJavaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
Write-Host "Current JAVA_HOME (Machine): $currentJavaHome" -ForegroundColor White

$currentPath = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
$javaInPath = $currentPath -split ';' | Where-Object { $_ -like '*java*' }
Write-Host "Java in PATH: $javaInPath" -ForegroundColor White

Write-Host ""

# Set JAVA_HOME
$javaHome = "C:\Program Files\Java\jdk-21"
if (Test-Path $javaHome) {
    Write-Host "🔧 Setting JAVA_HOME to: $javaHome" -ForegroundColor Yellow
    try {
        [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "Machine")
        Write-Host "✅ JAVA_HOME set successfully" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to set JAVA_HOME: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "❌ JDK not found at: $javaHome" -ForegroundColor Red
}

# Update PATH to include JDK bin directory
$jdkBinPath = "$javaHome\bin"
if (Test-Path $jdkBinPath) {
    $currentPath = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")
    
    # Remove old java paths
    $pathArray = $currentPath -split ';' | Where-Object { $_ -notlike '*java*' -and $_ -ne '' }
    
    # Add new JDK bin path at the beginning
    $newPath = @($jdkBinPath) + $pathArray -join ';'
    
    Write-Host "🔧 Updating PATH to include JDK bin directory" -ForegroundColor Yellow
    try {
        [System.Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
        Write-Host "✅ PATH updated successfully" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to update PATH: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "❌ JDK bin directory not found: $jdkBinPath" -ForegroundColor Red
}

Write-Host ""
Write-Host "📋 Updated Java Configuration:" -ForegroundColor Cyan

# Refresh environment for current session
$env:JAVA_HOME = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
$env:PATH = [System.Environment]::GetEnvironmentVariable("PATH", "Machine")

Write-Host "New JAVA_HOME (Machine): $([System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine"))" -ForegroundColor White

$newJavaInPath = ([System.Environment]::GetEnvironmentVariable("PATH", "Machine") -split ';') | Where-Object { $_ -like '*java*' }
Write-Host "New Java in PATH: $newJavaInPath" -ForegroundColor White

Write-Host ""
Write-Host "✅ Java environment configuration completed!" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Important Notes:" -ForegroundColor Cyan
Write-Host "   • You may need to restart your IDE/terminal for changes to take effect" -ForegroundColor White
Write-Host "   • New PowerShell sessions will automatically use the updated environment" -ForegroundColor White
Write-Host "   • Your Spring Boot application should now use the correct Java version" -ForegroundColor White

Write-Host ""
Write-Host "🧪 Testing updated configuration..." -ForegroundColor Yellow

# Test Java
Write-Host "Java version test:" -ForegroundColor White
& "$jdkBinPath\java.exe" -version

Write-Host ""
Write-Host "Maven test:" -ForegroundColor White
mvn -version

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
