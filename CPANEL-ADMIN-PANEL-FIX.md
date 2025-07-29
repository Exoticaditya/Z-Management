# Files to Update in cPanel for Admin Panel Access

## Issue Analysis
The error `ERR_NAME_NOT_RESOLVED` indicates that the admin panel is trying to connect to wrong URLs. Here are the files that need to be updated in your cPanel:

## 1. **Root Directory Files (Update these in cPanel File Manager)**

### Update: `api-config.js`
**Location**: Root directory of your website
**Current Issue**: May be missing or outdated
**Fix**: Replace with this content:

```javascript
/**
 * API Configuration for Z+ Management Platform
 * Automatically detects environment and uses appropriate backend URL
 */

// Configuration object
const API_CONFIG = {
    // Railway Production URL - YOUR CURRENT RAILWAY URL
    PRODUCTION_URL: 'https://i1corir7.up.railway.app',
    
    // Local development URL
    DEVELOPMENT_URL: 'http://localhost:8080',
    
    // Auto-detect environment
    getBaseUrl() {
        // If running on zpluse.com (production), use Railway
        if (window.location.hostname === 'zpluse.com' || 
            window.location.hostname === 'www.zpluse.com') {
            return this.PRODUCTION_URL;
        }
        
        // If running locally, use localhost
        if (window.location.hostname === 'localhost' || 
            window.location.hostname === '127.0.0.1' ||
            window.location.protocol === 'file:') {
            return this.DEVELOPMENT_URL;
        }
        
        // Default to production for any other domain
        return this.PRODUCTION_URL;
    },
    
    // Get full API URL
    getApiUrl(endpoint = '') {
        const baseUrl = this.getBaseUrl();
        const apiPath = '/api';
        
        // Remove leading slash from endpoint if present
        const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
        
        if (cleanEndpoint) {
            return `${baseUrl}${apiPath}/${cleanEndpoint}`;
        }
        return `${baseUrl}${apiPath}`;
    }
};

// Global API base URL for backward compatibility
const API_BASE_URL = API_CONFIG.getApiUrl();

// Debug logging
console.log('API Configuration loaded:');
console.log('Current hostname:', window.location.hostname);
console.log('Selected base URL:', API_CONFIG.getBaseUrl());
console.log('API base URL:', API_BASE_URL);
```

## 2. **Admin Panel Files (Upload these to cPanel)**

### Update: `admin-panel/admin-backend/admin/admin-style.css`
**Location**: `admin-panel/admin-backend/admin/` directory in cPanel
**Action**: Upload the latest version from your local project

### Update: `admin-panel/admin-backend/backend/src/main/resources/static/admin/js/admin-script.js`
**Location**: You need to extract this from your backend and upload to cPanel
**Current Issue**: Still using localhost:8080
**Fix**: Replace the first few lines with:

```javascript
document.addEventListener('DOMContentLoaded', function() {
    // Use the global API configuration instead of hardcoded localhost
    const API_BASE_URL = window.API_CONFIG ? window.API_CONFIG.getApiUrl() : 'https://i1corir7.up.railway.app/api';
    
    console.log('[ADMIN DEBUG] Using API URL:', API_BASE_URL);
    
    const adminDashboard = document.querySelector('.admin-dashboard');
    const contentTitle = document.getElementById('contentTitle');
    const contentBody = document.getElementById('contentBody');
    const backButton = document.getElementById('backButton');
    const loginFormContainer = document.getElementById('login-form-container');
    let activeLink = null;
    let navigationHistory = [];
    // ... rest of the code remains the same
```

### Update: `admin-panel/admin-backend/backend/src/main/resources/static/admin/index.html`
**Location**: Extract from backend and upload to cPanel admin directory
**Fix**: Add this script tag before the admin-script.js:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Z+ Admin Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="css/admin-style.css">
    <link rel="stylesheet" href="css/animations.css">
</head>
<body>
    <!-- Your existing HTML content -->
    
    <!-- ADD THIS BEFORE OTHER SCRIPTS -->
    <script src="/api-config.js"></script>
    <script src="js/admin-script.js"></script>
    <script src="js/animations.js"></script>
</body>
</html>
```

## 3. **cPanel Directory Structure Should Be:**

```
public_html/
├── api-config.js                          ← UPDATE THIS
├── index.html
├── style.css
├── admin-panel/
│   ├── admin-backend/
│   │   ├── admin/
│   │   │   └── admin-style.css            ← UPDATE THIS
│   │   └── index/
│   │       └── style.css
│   └── admin/                             ← CREATE THIS DIRECTORY
│       ├── index.html                     ← UPLOAD FROM BACKEND
│       ├── css/
│       │   ├── admin-style.css           ← UPLOAD FROM BACKEND
│       │   └── animations.css            ← UPLOAD FROM BACKEND
│       └── js/
│           ├── admin-script.js           ← UPLOAD MODIFIED VERSION
│           └── animations.js             ← UPLOAD FROM BACKEND
```

## 4. **Steps to Fix in cPanel:**

### Step 1: Update API Configuration
1. Open cPanel File Manager
2. Navigate to `public_html`
3. Edit or create `api-config.js` with the content above
4. Save the file

### Step 2: Extract Admin Panel Files
1. Go to your local project: `admin-panel/admin-backend/backend/src/main/resources/static/admin/`
2. Upload all these files to cPanel under `public_html/admin/`
3. Modify `admin-script.js` as shown above

### Step 3: Update Railway URL
1. Check your Railway dashboard for the current URL
2. Update the `PRODUCTION_URL` in `api-config.js` if it has changed

### Step 4: Test Access
1. Go to `https://zpluse.com/admin/`
2. Check browser console for any errors
3. Verify API calls are going to Railway URL

## 5. **Common Issues and Fixes:**

### Issue: "Failed to load resource: net::ERR_NAME_NOT_RESOLVED"
**Fix**: Update Railway URL in api-config.js

### Issue: "404 favicon.ico"
**Fix**: Add favicon.ico to root directory or ignore this error

### Issue: Admin panel not loading
**Fix**: Ensure all admin files are uploaded to correct cPanel directory

### Issue: Login not working
**Fix**: Check Railway backend is running and API URL is correct

## 6. **Quick Test Commands:**

Open browser console on your admin page and run:
```javascript
console.log('API URL:', API_CONFIG.getApiUrl());
fetch(API_CONFIG.getApiUrl('dashboard/stats')).then(r => console.log('API Response:', r));
```

This should help you identify and fix all the connection issues!
