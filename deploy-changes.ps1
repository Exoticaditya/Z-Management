# Z+ Management - Selective Deployment Script
# This script identifies changed files and helps deploy only updates to cPanel

Write-Host "=== Z+ Management Deployment Script ===" -ForegroundColor Green
Write-Host "Identifying changed files for selective deployment..." -ForegroundColor Yellow

# Get the last deployment commit (you can update this hash after each deployment)
$lastDeployCommit = "9136283"  # Update this after each successful deployment
$currentCommit = "7da1ffd"

Write-Host "`nComparing commits: $lastDeployCommit -> $currentCommit" -ForegroundColor Cyan

# Get list of changed files
Write-Host "`nChanged files:" -ForegroundColor Yellow
$changedFiles = git diff --name-only $lastDeployCommit $currentCommit
$changedFiles | ForEach-Object { Write-Host "  📄 $_" -ForegroundColor White }

# Categorize files by deployment location
$frontendFiles = @()
$backendFiles = @()
$configFiles = @()

foreach ($file in $changedFiles) {
    if ($file -match '\.(html|css|js)$' -and $file -notmatch 'admin-panel/admin-backend') {
        $frontendFiles += $file
    }
    elseif ($file -match 'admin-panel/admin-backend/backend/src') {
        $backendFiles += $file
    }
    elseif ($file -match '\.(xml|properties|yml|yaml)$') {
        $configFiles += $file
    }
}

Write-Host "`n=== DEPLOYMENT PLAN ===" -ForegroundColor Green

# Frontend files deployment
if ($frontendFiles.Count -gt 0) {
    Write-Host "`n🌐 FRONTEND FILES (Copy to public_html):" -ForegroundColor Cyan
    foreach ($file in $frontendFiles) {
        Write-Host "   ➤ $file" -ForegroundColor White
        Write-Host "     📁 Destination: /public_html/$file" -ForegroundColor Gray
    }
}

# Backend files deployment
if ($backendFiles.Count -gt 0) {
    Write-Host "`n⚙️  BACKEND FILES (Update and rebuild):" -ForegroundColor Cyan
    foreach ($file in $backendFiles) {
        Write-Host "   ➤ $file" -ForegroundColor White
        Write-Host "     📁 Destination: /home/omo4uo8j0tcl/repos/z-management/$file" -ForegroundColor Gray
    }
    Write-Host "   🔧 Action Required: Rebuild backend after copying files" -ForegroundColor Yellow
}

# Config files
if ($configFiles.Count -gt 0) {
    Write-Host "`n📋 CONFIG FILES:" -ForegroundColor Cyan
    foreach ($file in $configFiles) {
        Write-Host "   ➤ $file" -ForegroundColor White
    }
}

Write-Host "`n=== SPECIFIC FILE CHANGES ===" -ForegroundColor Green

# Create individual file copy commands
Write-Host "`n📝 FILE COPY COMMANDS FOR CPANEL:" -ForegroundColor Yellow

foreach ($file in $frontendFiles) {
    Write-Host "`n🔸 $file" -ForegroundColor Cyan
    Write-Host "   Local:  $(Get-Location)\$file" -ForegroundColor Gray
    Write-Host "   Remote: /public_html/$file" -ForegroundColor Gray
    Write-Host "   Action: Upload via File Manager or SFTP" -ForegroundColor Yellow
}

foreach ($file in $backendFiles) {
    Write-Host "`n🔸 $file" -ForegroundColor Cyan
    Write-Host "   Local:  $(Get-Location)\$file" -ForegroundColor Gray
    Write-Host "   Remote: /home/omo4uo8j0tcl/repos/z-management/$file" -ForegroundColor Gray
    Write-Host "   Action: Upload and rebuild backend" -ForegroundColor Yellow
}

Write-Host "`n=== DEPLOYMENT STEPS ===" -ForegroundColor Green

Write-Host "`n1️⃣  FRONTEND DEPLOYMENT:" -ForegroundColor Cyan
Write-Host "   • Access cPanel File Manager" -ForegroundColor White
Write-Host "   • Navigate to public_html" -ForegroundColor White
Write-Host "   • Upload the following files:" -ForegroundColor White
foreach ($file in $frontendFiles) {
    Write-Host "     - $file" -ForegroundColor Gray
}

Write-Host "`n2️⃣  BACKEND DEPLOYMENT:" -ForegroundColor Cyan
Write-Host "   • Access your backend directory: /home/omo4uo8j0tcl/repos/z-management" -ForegroundColor White
Write-Host "   • Upload the following files:" -ForegroundColor White
foreach ($file in $backendFiles) {
    Write-Host "     - $file" -ForegroundColor Gray
}
Write-Host "   • Run: ./build.sh or mvn clean package" -ForegroundColor Yellow
Write-Host "   • Restart the backend service" -ForegroundColor Yellow

Write-Host "`n3️⃣  VERIFICATION:" -ForegroundColor Cyan
Write-Host "   • Test contact form submission" -ForegroundColor White
Write-Host "   • Verify email notifications to zpluse47@gmail.com" -ForegroundColor White
Write-Host "   • Check all social media links in footer" -ForegroundColor White

Write-Host "`n=== QUICK COPY SCRIPT ===" -ForegroundColor Green

# Create a quick copy script for the changed files
$copyScript = @"
# Quick file copy script - Run this in your local directory
# Copy these files to your cPanel hosting

Write-Host "Creating deployment package..." -ForegroundColor Yellow

# Create deployment directory
if (!(Test-Path "deployment-package")) {
    New-Item -ItemType Directory -Name "deployment-package"
}

# Copy frontend files
"@

foreach ($file in $frontendFiles) {
    $copyScript += "`nCopy-Item `"$file`" `"deployment-package\`" -Force"
}

$copyScript += @"

# Copy backend files maintaining directory structure
"@

foreach ($file in $backendFiles) {
    $dir = Split-Path $file -Parent
    $copyScript += "`nif (!(Test-Path `"deployment-package\$dir`")) { New-Item -ItemType Directory -Path `"deployment-package\$dir`" -Force }"
    $copyScript += "`nCopy-Item `"$file`" `"deployment-package\$file`" -Force"
}

$copyScript += @"

Write-Host "Deployment package created in 'deployment-package' folder" -ForegroundColor Green
Write-Host "Upload contents to your cPanel hosting" -ForegroundColor Yellow
"@

# Save the copy script
$copyScript | Out-File -FilePath "create-deployment-package.ps1" -Encoding UTF8

Write-Host "`n📦 Created: create-deployment-package.ps1" -ForegroundColor Green
Write-Host "   Run this script to create a deployment package with only changed files" -ForegroundColor Yellow

Write-Host "`n=== SUMMARY ===" -ForegroundColor Green
Write-Host "📊 Total changed files: $($changedFiles.Count)" -ForegroundColor White
Write-Host "🌐 Frontend files: $($frontendFiles.Count)" -ForegroundColor White
Write-Host "⚙️  Backend files: $($backendFiles.Count)" -ForegroundColor White
Write-Host "📋 Config files: $($configFiles.Count)" -ForegroundColor White

Write-Host "`n✅ Ready for selective deployment!" -ForegroundColor Green
Write-Host "💡 Tip: Update `$lastDeployCommit variable after successful deployment" -ForegroundColor Yellow