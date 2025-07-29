# Railway PostgreSQL Connection for pgAdmin 4

## Step 1: Get Railway Connection Details
1. Go to Railway Dashboard
2. Click on your Postgres service
3. Go to "Connect" tab
4. Copy these details:
   - Host: (external hostname)
   - Port: 5432
   - Database: railway
   - Username: postgres
   - Password: (from Railway)

## Step 2: Add Server in pgAdmin 4
1. Right-click "Servers" in pgAdmin 4
2. Register → Server
3. General Tab:
   - Name: "Railway PostgreSQL"
4. Connection Tab:
   - Host: [from Railway dashboard]
   - Port: 5432
   - Maintenance database: railway
   - Username: postgres
   - Password: [from Railway dashboard]
   - Save password: ✅ Check this

## Step 3: Test Connection
1. Click "Save"
2. If connection fails, check:
   - Railway host is correct
   - Port 5432 is open
   - Password is correct

## Example Connection Details:
Host: containers-us-west-1.railway.app (yours may be different)
Port: 5432
Database: railway
Username: postgres
Password: ocHfgptFatbBzfKxgNMmBMGQgZqIyGYc
