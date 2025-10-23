# Simple Deployment Analysis Script
Write-Host "=== Z+ Management Changed Files Analysis ===" -ForegroundColor Green

# Show files changed between last deployment and current commit
Write-Host "`nFiles changed since last deployment:" -ForegroundColor Yellow

# Frontend files that need to be updated on cPanel
Write-Host "`n🌐 FRONTEND FILES (Upload to public_html):" -ForegroundColor Cyan
Write-Host "   📄 index.html - Updated social media links in footer" -ForegroundColor White
Write-Host "   📄 contact.html - Enhanced form field mapping" -ForegroundColor White  
Write-Host "   📄 zplus-forms.js - Improved form validation" -ForegroundColor White

# Backend files that need to be updated
Write-Host "`n⚙️ BACKEND FILES (Upload to backend directory):" -ForegroundColor Cyan
Write-Host "   📄 ContactController.java - Added email notification trigger" -ForegroundColor White
Write-Host "   📄 EmailService.java - Added contact inquiry notification method" -ForegroundColor White

Write-Host "`n=== DEPLOYMENT PLAN ===" -ForegroundColor Green

Write-Host "`n1️⃣ Frontend Deployment:" -ForegroundColor Yellow
Write-Host "   • Login to cPanel File Manager" -ForegroundColor White
Write-Host "   • Navigate to public_html directory" -ForegroundColor White
Write-Host "   • Upload and replace:" -ForegroundColor White
Write-Host "     - index.html" -ForegroundColor Gray
Write-Host "     - contact.html" -ForegroundColor Gray
Write-Host "     - zplus-forms.js" -ForegroundColor Gray

Write-Host "`n2️⃣ Backend Deployment:" -ForegroundColor Yellow
Write-Host "   • Navigate to: /home/omo4uo8j0tcl/repos/z-management" -ForegroundColor White
Write-Host "   • Upload and replace:" -ForegroundColor White
Write-Host "     - admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/controller/ContactController.java" -ForegroundColor Gray
Write-Host "     - admin-panel/admin-backend/backend/src/main/java/com/zplus/adminpanel/service/EmailService.java" -ForegroundColor Gray
Write-Host "   • Rebuild backend: mvn clean package" -ForegroundColor White
Write-Host "   • Restart backend service" -ForegroundColor White

Write-Host "`n3️⃣ Testing:" -ForegroundColor Yellow
Write-Host "   • Test contact form submission" -ForegroundColor White
Write-Host "   • Verify email sent to zpluse47@gmail.com" -ForegroundColor White
Write-Host "   • Check social media links work" -ForegroundColor White

Write-Host "`n✅ Ready for deployment!" -ForegroundColor Green