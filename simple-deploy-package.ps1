# Create Deployment Package Script - Simple Version
Write-Host "=== Creating Deployment Package ===" -ForegroundColor Green

# Create deployment directory
$deployDir = "deployment-package"
if (Test-Path $deployDir) {
    Remove-Item $deployDir -Recurse -Force
}
New-Item -ItemType Directory -Name $deployDir | Out-Null

Write-Host "Creating deployment package in '$deployDir' folder..." -ForegroundColor Yellow

# Create frontend and backend directories
$frontendDir = "$deployDir/frontend"
$backendDir = "$deployDir/backend"
New-Item -ItemType Directory -Path $frontendDir -Force | Out-Null
New-Item -ItemType Directory -Path $backendDir -Force | Out-Null

Write-Host "`nCopying frontend files..." -ForegroundColor Cyan

# Copy frontend files
$frontendFiles = @("index.html", "contact.html", "zplus-forms.js")
foreach ($file in $frontendFiles) {
    if (Test-Path $file) {
        Copy-Item $file $frontendDir -Force
        Write-Host "   Copied: $file" -ForegroundColor Green
    } else {
        Write-Host "   Warning: $file not found" -ForegroundColor Yellow
    }
}

Write-Host "`nCopying backend files..." -ForegroundColor Cyan

# Copy backend files with directory structure
$backendFiles = @(
    "admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/controller/ContactController.java",
    "admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/service/EmailService.java"
)

foreach ($file in $backendFiles) {
    if (Test-Path $file) {
        $targetPath = "$backendDir/$file"
        $targetDir = Split-Path $targetPath -Parent
        
        if (!(Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        
        Copy-Item $file $targetPath -Force
        Write-Host "   Copied: $file" -ForegroundColor Green
    } else {
        Write-Host "   Warning: $file not found" -ForegroundColor Yellow
    }
}

# Create deployment instructions file
$instructions = @"
Z+ Management - Deployment Instructions
======================================

CHANGED FILES IN THIS PACKAGE:

Frontend Files (Upload to public_html):
- index.html (updated footer with social media links)
- contact.html (enhanced form handling) 
- zplus-forms.js (improved form validation)

Backend Files (Upload to backend directory):
- ContactController.java (added email notifications)
- EmailService.java (added contact inquiry notifications)

DEPLOYMENT STEPS:

1. Frontend Deployment:
   - Access cPanel File Manager
   - Navigate to public_html
   - Upload files from frontend folder
   - Replace existing files

2. Backend Deployment:
   - Navigate to /home/omo4uo8j0tcl/repos/z-management
   - Upload files from backend folder maintaining directory structure
   - Run: mvn clean package
   - Restart backend service

3. Verification:
   - Test contact form
   - Verify emails to zpluse47@gmail.com
   - Check social media links

SOCIAL MEDIA LINKS ADDED:
- Instagram: @zpluseindia
- Facebook: Z pluse India  
- LinkedIn: zplusemanagement
- Twitter: @Zpluseindia
- Snapchat: zpluseindia
- WhatsApp: Click-to-chat

UPDATED CONTACT INFO:
- Email: zpluse47@gmail.com
- Phone: +91 7895155847
"@

$instructions | Out-File -FilePath "$deployDir/DEPLOYMENT-INSTRUCTIONS.txt" -Encoding UTF8

Write-Host "`nDeployment package created successfully!" -ForegroundColor Green
Write-Host "Location: $deployDir" -ForegroundColor White
Write-Host "`nPackage contents:" -ForegroundColor Yellow
Write-Host "- frontend/ (HTML, CSS, JS files)" -ForegroundColor White  
Write-Host "- backend/ (Java source files)" -ForegroundColor White
Write-Host "- DEPLOYMENT-INSTRUCTIONS.txt" -ForegroundColor White

Write-Host "`nReady to upload to cPanel!" -ForegroundColor Green