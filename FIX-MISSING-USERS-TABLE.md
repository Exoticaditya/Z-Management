# 🔧 Fix Railway Database - Missing Users Table

## 🚨 Problem Identified
The error "relation 'users' does not exist" means your Railway PostgreSQL database is missing the required tables for the admin panel.

## ✅ Solution: Initialize Railway Database

You need to run the database initialization script on your Railway PostgreSQL database.

### Method 1: Using Railway CLI (Recommended)

1. **Install Railway CLI** (if not already installed):
   ```bash
   npm install -g @railway/cli
   ```

2. **Login to Railway**:
   ```bash
   railway login
   ```

3. **Connect to your project**:
   ```bash
   railway link
   ```

4. **Run the database initialization script**:
   ```bash
   railway run -- psql $DATABASE_URL -f admin-panel/admin-backend/backend/init-scripts/01-init-database.sql
   ```

### Method 2: Using psql directly

1. **Connect to your Railway PostgreSQL using the DATABASE_URL**:
   ```bash
   psql "postgresql://postgres:ocHfgptFatbBzfKxgNMmBMGQgZqIyGYc@postgres.railway.internal:5432/railway" -f init-scripts/01-init-database.sql
   ```

### Method 3: Using Railway Web Console

1. Go to **Railway Dashboard** → **PostgreSQL Service** → **Data** tab
2. **Copy and paste the SQL commands** from `init-scripts/01-init-database.sql`
3. **Execute the commands**

## 📋 What the Script Will Create

### Tables:
- ✅ `users` table with all required fields
- ✅ `contact_inquiries` table (already working)
- ✅ Enum types: `user_type`, `registration_status`
- ✅ Indexes for performance
- ✅ Views for data access

### Default Admin User:
- **Email**: `adityamalik5763@gmail.com`
- **Password**: `admin123`
- **Status**: APPROVED
- **User Type**: ADMIN

## 🎯 After Running the Script

1. **Test admin login**:
   - Go to: `https://z-management-production.up.railway.app/admin/`
   - Email: `adityamalik5763@gmail.com`
   - Password: `admin123`

2. **Verify contact inquiries**:
   - Should see the 2 existing contact inquiries in the admin panel

3. **Test contact form**:
   - Contact forms should now work without 500 errors

## 🚀 Quick Fix Command

If you have Railway CLI installed:
```bash
railway run -- psql $DATABASE_URL -f admin-panel/admin-backend/backend/init-scripts/01-init-database.sql
```

## ⚡ Alternative: Force Hibernate to Create Tables

If you can't run SQL scripts, update `application-railway.yml`:

```yaml
  jpa:
    hibernate:
      ddl-auto: create-drop  # This will recreate all tables
```

**Warning**: This will delete existing data! Only use for testing.

## 📞 Expected Result

After initialization:
- ✅ Admin login works
- ✅ Contact forms save successfully  
- ✅ Admin panel shows contact inquiries
- ✅ No more "relation 'users' does not exist" errors

The database connection is perfect - we just need to create the missing tables! 🎯
