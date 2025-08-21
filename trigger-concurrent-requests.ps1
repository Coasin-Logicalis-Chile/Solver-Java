# PowerShell script to trigger concurrent exception errors in your Solver API
# This will send multiple simultaneous requests to endpoints that are vulnerable to concurrency issues

Write-Host "🚀 Starting Concurrent API Request Generator..." -ForegroundColor Green
Write-Host "This will attempt to trigger concurrent exceptions in your Solver API" -ForegroundColor Yellow
Write-Host ""

# Configuration
$baseUrl = "http://localhost:6051"
$concurrentRequests = 20
$maxRetries = 3

# Test endpoints that are likely to have concurrency issues
$endpoints = @(
    @{
        Name = "Attachments Endpoint"
        Url = "$baseUrl/api/v1/attachments/test-integration-id"
        Method = "GET"
        Description = "Tests the ArrayList concurrency issue in SnAttachmentController"
    },
    @{
        Name = "Business Rules Endpoint"  
        Url = "$baseUrl/api/v1/businessRules"
        Method = "GET"
        Description = "Tests concurrent access to business rules"
    },
    @{
        Name = "Incidents by Filter"
        Url = "$baseUrl/api/v1/incidentsByFilter?company=1&state=open"
        Method = "GET"
        Description = "Tests concurrent filtering operations"
    },
    @{
        Name = "Count Incidents"
        Url = "$baseUrl/api/v1/countIncidentsByFilters?company=1&open=true"
        Method = "GET"
        Description = "Tests concurrent counting operations"
    }
)

function Test-ApiEndpoint {
    param(
        [string]$Url,
        [string]$Method,
        [int]$RequestId
    )
    
    try {
        $response = Invoke-RestMethod -Uri $Url -Method $Method -TimeoutSec 30 -ErrorAction Stop
        return @{
            RequestId = $RequestId
            Success = $true
            StatusCode = 200
            Error = $null
        }
    }
    catch {
        return @{
            RequestId = $RequestId
            Success = $false
            StatusCode = $_.Exception.Response.StatusCode.Value__
            Error = $_.Exception.Message
        }
    }
}

function Start-ConcurrentRequests {
    param(
        [hashtable]$Endpoint,
        [int]$RequestCount
    )
    
    Write-Host "🎯 Testing: $($Endpoint.Name)" -ForegroundColor Cyan
    Write-Host "   URL: $($Endpoint.Url)" -ForegroundColor Gray
    Write-Host "   Description: $($Endpoint.Description)" -ForegroundColor Gray
    Write-Host "   Sending $RequestCount concurrent requests..." -ForegroundColor Yellow
    
    $jobs = @()
    $successCount = 0
    $errorCount = 0
    $errors = @()
    
    # Start concurrent requests
    for ($i = 1; $i -le $RequestCount; $i++) {
        $job = Start-Job -ScriptBlock ${function:Test-ApiEndpoint} -ArgumentList $Endpoint.Url, $Endpoint.Method, $i
        $jobs += $job
    }
    
    # Wait for all jobs to complete
    $results = $jobs | Wait-Job | Receive-Job
    $jobs | Remove-Job
    
    # Analyze results
    foreach ($result in $results) {
        if ($result.Success) {
            $successCount++
        } else {
            $errorCount++
            $errors += "Request $($result.RequestId): $($result.Error)"
        }
    }
    
    Write-Host "   ✅ Successful requests: $successCount" -ForegroundColor Green
    Write-Host "   ❌ Failed requests: $errorCount" -ForegroundColor Red
    
    if ($errorCount -gt 0) {
        Write-Host "   🔍 Errors detected (possible concurrency issues):" -ForegroundColor Magenta
        foreach ($error in $errors | Select-Object -First 5) {
            Write-Host "      $error" -ForegroundColor Red
        }
        if ($errors.Count -gt 5) {
            Write-Host "      ... and $($errors.Count - 5) more errors" -ForegroundColor Red
        }
    }
    
    Write-Host ""
    return @{
        SuccessCount = $successCount
        ErrorCount = $errorCount
        Errors = $errors
    }
}
}

# Main execution
Write-Host "Checking if API is running at $baseUrl..." -ForegroundColor Yellow

try {
    $healthCheck = Invoke-RestMethod -Uri "$baseUrl/health" -Method GET -TimeoutSec 10 -ErrorAction Stop
    Write-Host "✅ API is running and accessible" -ForegroundColor Green
}
catch {
    Write-Host "❌ Cannot reach API at $baseUrl" -ForegroundColor Red
    Write-Host "Please make sure your Spring Boot API is running on port 6051" -ForegroundColor Yellow
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🚀 Starting concurrent load testing to trigger concurrency issues..." -ForegroundColor Green
Write-Host "Each endpoint will receive $concurrentRequests simultaneous requests" -ForegroundColor Yellow
Write-Host ""

$totalResults = @{
    TotalSuccess = 0
    TotalErrors = 0
    AllErrors = @()
}

foreach ($endpoint in $endpoints) {
    $result = Start-ConcurrentRequests -Endpoint $endpoint -RequestCount $concurrentRequests
    $totalResults.TotalSuccess += $result.SuccessCount
    $totalResults.TotalErrors += $result.ErrorCount
    $totalResults.AllErrors += $result.Errors
    
    Start-Sleep -Seconds 2  # Brief pause between endpoint tests
}

Write-Host "===========================================" -ForegroundColor Blue
Write-Host "🏁 FINAL RESULTS" -ForegroundColor Blue
Write-Host "===========================================" -ForegroundColor Blue
Write-Host "Total successful requests: $($totalResults.TotalSuccess)" -ForegroundColor Green
Write-Host "Total failed requests: $($totalResults.TotalErrors)" -ForegroundColor Red

if ($totalResults.TotalErrors -gt 0) {
    Write-Host ""
    Write-Host "🔥 CONCURRENCY ISSUES DETECTED!" -ForegroundColor Red
    Write-Host "The failed requests indicate potential concurrency problems in your API:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Common concurrent exceptions you might see:" -ForegroundColor Cyan
    Write-Host "  • ConcurrentModificationException" -ForegroundColor Red
    Write-Host "  • Race conditions in data access" -ForegroundColor Red  
    Write-Host "  • Deadlocks in database operations" -ForegroundColor Red
    Write-Host "  • Session state corruption" -ForegroundColor Red
    Write-Host "  • Memory consistency errors" -ForegroundColor Red
    Write-Host ""
    Write-Host "Check your application logs for specific error details!" -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "✅ No obvious concurrency issues detected in this test run." -ForegroundColor Green
    Write-Host "However, concurrency issues can be intermittent." -ForegroundColor Yellow
    Write-Host "Try running the test multiple times or increasing the request count." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "💡 To investigate further:" -ForegroundColor Cyan
Write-Host "  1. Check your application logs at: logs/solver.log" -ForegroundColor White
Write-Host "  2. Look for ConcurrentModificationException, deadlocks, or race conditions" -ForegroundColor White
Write-Host "  3. Run the ConcurrentExceptionReproducer test class for more detailed analysis" -ForegroundColor White
Write-Host "  4. Monitor CPU and memory usage during high concurrent load" -ForegroundColor White

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
