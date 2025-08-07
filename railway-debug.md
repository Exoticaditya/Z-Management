# Railway Deployment Debugging Guide

## Quick Health Check Tests

### 1. Test Railway URL Directly
```bash
curl -X GET https://z-management-production.up.railway.app/
curl -X GET https://z-management-production.up.railway.app/api/health
```

### 2. Check Railway Logs
- Go to Railway Dashboard → Deployments → View Logs
- Look for Java application startup messages
- Check for any port binding errors

### 3. Common Issues & Solutions

#### Health Check Timeout
- **Symptom**: "Deployment failed during network process"
- **Solution**: App takes too long to start
- **Fix**: Wait for deployment to complete (can take 3-5 minutes)

#### Port Binding Issues
- **Symptom**: "Connection refused"
- **Solution**: Check if app binds to Railway's PORT variable
- **Fix**: Verify Dockerfile uses ${PORT:-8080}

#### Database Connection
- **Symptom**: App starts but APIs fail
- **Solution**: Check PostgreSQL connection
- **Fix**: Verify DATABASE_URL environment variable

### 4. Emergency Rollback
If deployment fails completely:
```bash
# Revert to previous working commit
git log --oneline -5
git reset --hard <previous-commit-hash>
git push --force
```

### 5. Alternative Health Check Paths
If "/" doesn't work, try updating railway.json:
```json
{
  "deploy": {
    "healthcheckPath": "/api/health",
    "healthcheckTimeout": 300
  }
}
```
