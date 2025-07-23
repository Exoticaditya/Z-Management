# 🚀 GitHub Deployment Guide for Z+ Management Platform

## Quick GitHub Deployment Steps

### 1. Create GitHub Repository

1. **Go to GitHub** and click "New Repository"
2. **Repository name**: `z-plus-management` (or your preferred name)
3. **Description**: "Z+ Management Platform - Multi-dashboard contact management system"
4. **Visibility**: Choose Public or Private
5. **DO NOT** initialize with README, .gitignore, or license (we already have these)
6. **Click "Create repository"**

### 2. Connect Local Repository to GitHub

```bash
# Add your GitHub repository as remote origin
git remote add origin https://github.com/YOUR_USERNAME/z-plus-management.git

# Push your code to GitHub
git branch -M main
git push -u origin main
```

### 3. Verify Deployment

After pushing, your repository should contain:
- ✅ Complete source code
- ✅ Docker configuration
- ✅ Build scripts
- ✅ Documentation
- ✅ CI/CD workflows

## 🔧 Post-Deployment Setup

### Configure GitHub Secrets (for CI/CD)

Go to your repository → Settings → Secrets and variables → Actions

Add these secrets:
```
DOCKERHUB_USERNAME=your_dockerhub_username
DOCKERHUB_TOKEN=your_dockerhub_access_token
```

### Enable GitHub Actions

1. Go to your repository → Actions tab
2. Enable GitHub Actions if prompted
3. Workflows will automatically run on push/PR

## 🌐 Deploy to Production

### Option 1: Docker Deployment (Recommended)

```bash
# Clone your repository
git clone https://github.com/YOUR_USERNAME/z-plus-management.git
cd z-plus-management

# Copy environment template
cp .env.template .env

# Edit .env with your configuration
# DB_HOST=localhost
# DB_PORT=5432
# DB_NAME=zplus_management
# DB_USERNAME=your_db_user
# DB_PASSWORD=your_secure_password
# JWT_SECRET=your_jwt_secret_key

# Deploy with Docker
chmod +x deploy.sh
./deploy.sh docker
```

### Option 2: Standalone Deployment

```bash
# Clone and build
git clone https://github.com/YOUR_USERNAME/z-plus-management.git
cd z-plus-management

# Build the application
chmod +x build.sh
./build.sh

# Deploy standalone
chmod +x deploy.sh
./deploy.sh standalone
```

### Option 3: Heroku Deployment

```bash
# Install Heroku CLI and login
heroku login

# Create Heroku app
heroku create your-app-name

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your_jwt_secret_here

# Deploy
git push heroku main
```

### Option 4: AWS/Azure/GCP Deployment

#### AWS EC2:
```bash
# On your EC2 instance
sudo yum update -y
sudo yum install -y docker git

# Clone and deploy
git clone https://github.com/YOUR_USERNAME/z-plus-management.git
cd z-plus-management
sudo ./deploy.sh docker
```

#### Azure Container Instances:
```bash
az container create \
  --resource-group myResourceGroup \
  --name zplus-management \
  --image your-dockerhub-username/zplus-management:latest \
  --ports 8080 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    DB_HOST=your-db-host \
    DB_USERNAME=your-db-user \
    DB_PASSWORD=your-db-password
```

## 📋 Verification Checklist

After deployment, verify these URLs work:

- [ ] **Application Home**: `http://your-domain:8080`
- [ ] **Admin Panel**: `http://your-domain:8080/admin/`
- [ ] **Employee Dashboard**: `http://your-domain:8080/employee-dashboard/`
- [ ] **Client Portal**: `http://your-domain:8080/client/`
- [ ] **API Health**: `http://your-domain:8080/actuator/health`
- [ ] **API Documentation**: `http://your-domain:8080/swagger-ui/`

## 🔒 Security Configuration

### Production Security Settings

Update your `.env` file for production:

```env
# Database (use secure credentials)
DB_PASSWORD=very_secure_password_here

# JWT (generate strong secret)
JWT_SECRET=super_long_jwt_secret_key_minimum_256_bits

# SSL (enable for production)
SSL_ENABLED=true
SSL_KEYSTORE_PATH=keystore.p12
SSL_KEYSTORE_PASSWORD=keystore_password

# CORS (restrict to your domains)
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### Generate Secure JWT Secret

```bash
# Generate a secure JWT secret
openssl rand -base64 32
# Use this output as your JWT_SECRET
```

## 🏷️ Creating Releases

### Tag and Release

```bash
# Create a new tag
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# This triggers the release workflow automatically
```

### Manual Release

Go to GitHub → Releases → "Create a new release"
- Tag: `v1.0.0`
- Title: `Z+ Management Platform v1.0.0`
- Description: Add release notes
- Attach built JAR file

## 🔧 Troubleshooting

### Common Issues

**Issue**: Build fails on GitHub Actions
**Solution**: Check Java version compatibility in `.github/workflows/ci-cd.yml`

**Issue**: Database connection fails
**Solution**: Verify DATABASE_URL and credentials in environment variables

**Issue**: Authentication not working
**Solution**: Check JWT_SECRET is set and CORS origins are configured

**Issue**: Static files not served
**Solution**: Verify SecurityConfig.java permits static resource paths

### Support

- **Documentation**: Check README.md, API.md, SECURITY.md
- **Issues**: Create GitHub issue with error details
- **Email**: support@yourcompany.com

## 📈 Monitoring and Maintenance

### Health Monitoring

```bash
# Check application health
curl http://your-domain:8080/actuator/health

# Check logs
docker logs zplus-management-app
```

### Updates

```bash
# Pull latest changes
git pull origin main

# Rebuild and redeploy
./build.sh
./deploy.sh docker
```

---

## 🎉 Congratulations!

Your Z+ Management Platform is now deployed and ready for production use!

**Next Steps:**
1. Configure your domain and SSL certificates
2. Set up monitoring and alerting
3. Configure backups for your database
4. Set up user accounts and permissions
5. Customize the platform for your needs

**Access your application at**: `http://your-domain:8080`

---

**Questions?** Check our [Contributing Guide](CONTRIBUTING.md) or create an issue on GitHub.
