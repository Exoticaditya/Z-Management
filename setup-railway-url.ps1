# Railway Deployment Quick Setup Script (PowerShell)
# Run this after getting your Railway URL

Write-Host "🚂 Railway Backend Setup Helper" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green

# Get Railway URL from user
$RAILWAY_URL = Read-Host "Please enter your Railway URL (example: https://z-management-production-abc123.railway.app)"

# Validate URL format
if ($RAILWAY_URL -notmatch "^https://.*\.railway\.app$") {
    Write-Host "❌ Invalid URL format. Must be: https://your-app.railway.app" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Valid Railway URL: $RAILWAY_URL" -ForegroundColor Green

# Update api-config.js
Write-Host "📝 Updating api-config.js..." -ForegroundColor Yellow
try {
    (Get-Content api-config.js) -replace 'https://z-management-production.railway.app', $RAILWAY_URL | Set-Content api-config.js
    Write-Host "✅ api-config.js updated successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Error updating api-config.js: $_" -ForegroundColor Red
    exit 1
}

# Test the backend
Write-Host "🔍 Testing backend connection..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$RAILWAY_URL/api/health" -UseBasicParsing -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Backend is responding!" -ForegroundColor Green
        Write-Host "🎉 Setup complete!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Cyan
        Write-Host "1. Upload updated api-config.js to GoDaddy" -ForegroundColor White
        Write-Host "2. Test contact form at https://zpluse.com/contact.html" -ForegroundColor White
        Write-Host "3. Your full-stack application is now live!" -ForegroundColor White
    }
} catch {
    Write-Host "⚠️  Backend not responding yet. Please wait a few minutes for Railway deployment to complete." -ForegroundColor Yellow
    Write-Host "Then test manually: $RAILWAY_URL/api/health" -ForegroundColor White
}

Write-Host ""
Write-Host "📱 Your complete system:" -ForegroundColor Cyan
Write-Host "Frontend: https://zpluse.com" -ForegroundColor White
Write-Host "Backend:  $RAILWAY_URL" -ForegroundColor White
Write-Host "Database: PostgreSQL (Railway managed)" -ForegroundColor White
