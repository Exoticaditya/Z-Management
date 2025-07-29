# Railway PostgreSQL Setup Guide

## Method 1: Add PostgreSQL via Dashboard

1. In your Railway project dashboard
2. Click "Add Service" or "+"
3. Select "Database" → "PostgreSQL"
4. Railway will automatically create the database

## Method 2: Add PostgreSQL via Railway CLI

```bash
# Install Railway CLI (if not installed)
npm install -g @railway/cli

# Login to Railway
railway login

# Link to your project
railway link

# Add PostgreSQL
railway add postgresql
```

## Method 3: Environment Variables (Manual)

If you have PostgreSQL elsewhere, set these variables:
- DATABASE_URL: Full PostgreSQL connection string
- PGHOST: Database host
- PGPORT: Database port (usually 5432)
- PGDATABASE: Database name
- PGUSER: Username
- PGPASSWORD: Password

## Method 4: Via railway.toml (Not recommended for PostgreSQL)

```toml
[build]
builder = "DOCKERFILE"

[deploy]
healthcheckPath = "/"
healthcheckTimeout = 180
restartPolicyType = "ON_FAILURE"
```
