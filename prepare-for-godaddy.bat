@echo off
echo 🚀 Preparing Z+ Website for GoDaddy Static Hosting...

REM Create deployment folder
mkdir godaddy-upload 2>NUL

echo 📁 Copying website files...

REM Copy all HTML files
copy *.html godaddy-upload\ >NUL 2>&1

REM Copy CSS files
copy *.css godaddy-upload\ >NUL 2>&1

REM Copy asset folder
xcopy /E /I asset godaddy-upload\asset >NUL 2>&1

REM Copy employee dashboard
xcopy /E /I employee-dashboard godaddy-upload\employee-dashboard >NUL 2>&1

echo ✅ Files prepared in 'godaddy-upload' folder
echo 📤 Upload contents of 'godaddy-upload' folder to GoDaddy public_html

REM Show what was copied
echo.
echo 📋 Files ready for upload:
dir godaddy-upload /b

echo.
echo 🌐 Next steps:
echo 1. Login to GoDaddy cPanel
echo 2. Open File Manager
echo 3. Go to public_html folder
echo 4. Upload all files from 'godaddy-upload' folder
echo 5. Visit https://zpluse.com to test

pause
