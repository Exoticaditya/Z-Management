# 🚂 Railway Deployment Guide for Z+ Management Platform

## Quick Setup (5 minutes)

### 1. Create Railway Account
1. Go to [railway.app](https://railway.app)
2. Click "Login with GitHub"
3. Authorize Railway to access your repositories

### 2. Deploy Your Backend
1. Click "New Project" in Railway dashboard
2. Choose "Deploy from GitHub repo"
3. Select your repository: `Exoticaditya/Z-Management`
4. Railway will detect it's a Java project automatically

### 3. Add PostgreSQL Database
1. In your Railway project, click "New Service"
2. Choose "PostgreSQL"
3. Railway will create and link the database automatically

### 4. Configure Environment Variables
In Railway dashboard, go to your service → Variables tab, add:

```
SPRING_PROFILES_ACTIVE=railway
CORS_ALLOWED_ORIGINS=https://zpluse.com
JWT_SECRET=your-super-secret-jwt-key-12345
```

### 5. Deploy Settings
1. Go to Settings → Build
2. Set Build Command: `cd admin-panel/admin-backend/backend && mvn clean package -DskipTests`
3. Set Start Command: `cd admin-panel/admin-backend/backend && java -jar target/admin-panel-backend-1.0.0.jar`

### 6. Connect Frontend to Backend
Once deployed, Railway gives you a URL like: `https://your-app.railway.app`

Update your frontend to use this API URL instead of localhost.

## Expected Costs
- **Free Tier**: 500 hours/month (about 20 days)
- **Hobby Plan**: $5/month for unlimited usage
- **Database**: Included in both plans

## What You Get
✅ Live backend API at `https://your-app.railway.app`
✅ PostgreSQL database (managed)
✅ Automatic deployments on GitHub pushes
✅ SSL certificates (HTTPS)
✅ Monitoring and logs

## Testing Your Deployment
1. Backend API: `https://your-app.railway.app/api/health`
2. Admin Panel: Connect your frontend to the new backend URL
3. Database: Railway provides connection details

## Troubleshooting
- **Build fails**: Check Java version (should be 17+)
- **Database connection**: Railway auto-provides DATABASE_URL
- **CORS errors**: Make sure CORS_ALLOWED_ORIGINS includes https://zpluse.com

---
🎉 **Your complete system will be live:**
- Frontend: https://zpluse.com (already working)
- Backend: https://your-app.railway.app (new)
- Database: Managed by Railway (new)
