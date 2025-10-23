# Create Deployment Package Script
# This script creates a package with only the changed files for cPanel upload

Write-Host "=== Creating Deployment Package ===" -ForegroundColor Green

# Create deployment directory
$deployDir = "deployment-package"
if (Test-Path $deployDir) {
    Remove-Item $deployDir -Recurse -Force
}
New-Item -ItemType Directory -Name $deployDir | Out-Null

Write-Host "📦 Creating deployment package in '$deployDir' folder..." -ForegroundColor Yellow

# Frontend files that changed
$frontendFiles = @(
    "index.html",
    "contact.html", 
    "zplus-forms.js"
)

# Backend files that changed
$backendFiles = @(
    "admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/controller/ContactController.java",
    "admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/service/EmailService.java"
)

Write-Host "`n🌐 Copying frontend files..." -ForegroundColor Cyan
$frontendDir = "$deployDir/frontend"
New-Item -ItemType Directory -Path $frontendDir -Force | Out-Null

foreach ($file in $frontendFiles) {
    if (Test-Path $file) {
        Copy-Item $file $frontendDir -Force
        Write-Host "   ✅ Copied: $file" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Not found: $file" -ForegroundColor Yellow
    }
}

Write-Host "`n⚙️  Copying backend files..." -ForegroundColor Cyan
foreach ($file in $backendFiles) {
    if (Test-Path $file) {
        $targetPath = "$deployDir/backend/$file"
        $targetDir = Split-Path $targetPath -Parent
        
        if (!(Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        
        Copy-Item $file $targetPath -Force
        Write-Host "   ✅ Copied: $file" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Not found: $file" -ForegroundColor Yellow
    }
}

# Create deployment instructions
$instructions = @"
# Z+ Management - Deployment Instructions

## 📋 Changed Files in This Package

### Frontend Files (Upload to public_html):
- index.html (updated footer with social media links)
- contact.html (enhanced form handling)
- zplus-forms.js (improved form validation)

### Backend Files (Upload to backend directory):
- ContactController.java (added email notifications)
- EmailService.java (added contact inquiry notifications)

## 🚀 Deployment Steps

### 1. Frontend Deployment:
1. Access your cPanel File Manager
2. Navigate to public_html
3. Upload files from the 'frontend' folder:
   - Replace existing index.html
   - Replace existing contact.html
   - Replace existing zplus-forms.js

### 2. Backend Deployment:
1. Navigate to: /home/omo4uo8j0tcl/repos/z-management
2. Upload files from the 'backend' folder maintaining directory structure:
   - admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/controller/ContactController.java
   - admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/service/EmailService.java

3. Rebuild the backend:
   ```bash
   cd /home/omo4uo8j0tcl/repos/z-management/admin-panel/admin-backend/backend
   ./build.sh
   # or
   mvn clean package
   ```

4. Restart your backend service

### 3. Verification:
- Test the contact form on your website
- Verify email notifications are sent to zpluse47@gmail.com
- Check all social media links in the footer work correctly

## 📧 Email Configuration
Make sure your Railway backend has proper email configuration in application.properties:
- spring.mail.host
- spring.mail.port
- spring.mail.username
- spring.mail.password

## 🔗 Social Media Links Added:
- Instagram: @zpluseindia
- Facebook: Z pluse India
- LinkedIn: zplusemanagement
- Twitter: @Zpluseindia
- Snapchat: zpluseindia
- WhatsApp: Click-to-chat integration

## 📞 Updated Contact Info:
- Email: zpluse47@gmail.com
- Phone: +91 7895155847
"@

$instructions | Out-File -FilePath "$deployDir/DEPLOYMENT-INSTRUCTIONS.txt" -Encoding UTF8

# Create a simple ZIP file listing
Write-Host "`n📋 Creating file manifest..." -ForegroundColor Cyan
$manifest = @"
Deployment Package Contents:
============================

Frontend Files:
"@

Get-ChildItem "$frontendDir" -File | ForEach-Object {
    $manifest += "`n- $($_.Name)"
}

$manifest += "`n`nBackend Files:"
Get-ChildItem "$deployDir/backend" -Recurse -File | ForEach-Object {
    $relativePath = $_.FullName.Replace("$deployDir\backend\", "").Replace("\", "/")
    $manifest += "`n- $relativePath"
}

$manifest | Out-File -FilePath "$deployDir/FILE-MANIFEST.txt" -Encoding UTF8

Write-Host "`n✅ Deployment package created successfully!" -ForegroundColor Green
Write-Host "📁 Location: $deployDir" -ForegroundColor White
Write-Host "📋 Files included:" -ForegroundColor White

Write-Host "`n🌐 Frontend files:" -ForegroundColor Cyan
Get-ChildItem "$frontendDir" -File | ForEach-Object {
    Write-Host "   - $($_.Name)" -ForegroundColor White
}

Write-Host "`n⚙️  Backend files:" -ForegroundColor Cyan
Get-ChildItem "$deployDir/backend" -Recurse -File | ForEach-Object {
    $relativePath = $_.FullName.Replace("$deployDir\backend\", "").Replace("\", "/")
    Write-Host "   - $relativePath" -ForegroundColor White
}

Write-Host "`n📖 Instructions: Check DEPLOYMENT-INSTRUCTIONS.txt in the package" -ForegroundColor Yellow
Write-Host "🚀 Ready to upload to cPanel!" -ForegroundColor Green