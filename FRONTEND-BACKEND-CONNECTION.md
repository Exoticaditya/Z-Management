# 🔗 Complete Frontend-Backend Connection Guide

## Overview
Your Z+ Management Platform will have:
- **Frontend**: https://zpluse.com (GoDaddy hosting)
- **Backend**: https://your-app.railway.app (Railway hosting)
- **Database**: PostgreSQL (Railway managed)

## Step-by-Step Connection Process

### 1. Deploy Backend on Railway
1. Go to [railway.app](https://railway.app) → Login with GitHub
2. **New Project** → **Deploy from GitHub repo** → `Exoticaditya/Z-Management`
3. **Add PostgreSQL service** to project
4. **Set Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=railway
   CORS_ALLOWED_ORIGINS=https://zpluse.com,https://www.zpluse.com
   JWT_SECRET=zplus-super-secret-jwt-key-12345
   ```
5. **Wait for deployment** (5-10 minutes)
6. **Copy your Railway URL**: `https://your-app-name.railway.app`

### 2. Update Frontend Configuration
1. **Edit `api-config.js`** (line 9):
   ```javascript
   PRODUCTION_URL: 'https://YOUR-ACTUAL-RAILWAY-URL.railway.app',
   ```
   Replace with your actual Railway URL

### 3. Upload Updated Files to GoDaddy
Upload these files to your GoDaddy hosting:
- ✅ `api-config.js` (new file)
- ✅ `contact.html` (updated)
- ✅ Any other pages that use the backend

### 4. Test the Connection
1. **Backend Health**: Visit `https://your-railway-url.railway.app/api/health`
2. **Frontend Form**: Go to `https://zpluse.com/contact.html` and submit a test form
3. **CORS**: Ensure no CORS errors in browser console

## Files Modified
- ✅ `api-config.js` - New configuration system
- ✅ `contact.html` - Updated to use Railway backend
- ✅ `godaddy-upload/api-config.js` - Production version
- ✅ `godaddy-upload/contact.html` - Production version

## Environment Detection
The system automatically detects:
- **Production** (zpluse.com): Uses Railway backend
- **Development** (localhost): Uses localhost:8080
- **Other domains**: Defaults to Railway backend

## Testing Checklist
- [ ] Railway backend deployed and running
- [ ] PostgreSQL database connected
- [ ] Frontend files uploaded to GoDaddy
- [ ] Contact form submits successfully
- [ ] No CORS errors in browser console
- [ ] Backend API responds to health checks

## Troubleshooting
- **CORS Error**: Check CORS_ALLOWED_ORIGINS includes your domain
- **404 Error**: Verify Railway URL is correct in api-config.js
- **Form Fails**: Check browser console for error messages
- **Backend Down**: Check Railway deployment status

## Next Steps
1. **Deploy backend on Railway**
2. **Get your Railway URL**
3. **Update api-config.js with real URL**
4. **Upload updated files to GoDaddy**
5. **Test complete system**

Your complete system will be live with frontend and backend connected!
