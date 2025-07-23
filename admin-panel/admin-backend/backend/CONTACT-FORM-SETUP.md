# Contact Form Database Integration Setup Guide

This guide will help you connect your frontend contact form to the PostgreSQL database and set up the complete backend infrastructure.

## 📋 What We've Created

### Backend Components:
1. **ContactInquiry Entity** - Database model for storing contact form data
2. **ContactStatus Enum** - Status tracking for inquiries
3. **ContactInquiryRequest DTO** - Data transfer object for form submissions
4. **ContactInquiryRepository** - Database operations interface
5. **ContactInquiryService** - Business logic service
6. **ContactController** - REST API endpoints
7. **Database Schema** - PostgreSQL tables and indexes

### Database Tables:
- `contact_inquiries` - Main table for contact form data
- `contact_service_interests` - Many-to-many relationship for service interests

## 🚀 Step-by-Step Setup

### Step 1: Update Database Schema
Run the updated database setup script to create the contact_inquiries table:

```bash
cd admin-panel/admin-backend/backend
setup-database.bat
```

### Step 2: Test Database Connection
Verify the database and tables are properly created:

```bash
test-database-connection.bat
```

### Step 3: Build and Start the Application
```bash
# Build the project
mvn clean install

# Start the application
mvn spring-boot:run
```

### Step 4: Test the Contact Form
1. Open your contact form: `contact.html`
2. Fill out the form and submit
3. Check the browser console for any errors
4. Verify data is saved in the database

## 🔧 API Endpoints Available

### Contact Form Submission
- **POST** `/api/contact` - Submit new contact inquiry

### Admin Management Endpoints
- **GET** `/api/contact` - Get all inquiries (with pagination)
- **GET** `/api/contact/{id}` - Get specific inquiry
- **PUT** `/api/contact/{id}/status` - Update inquiry status
- **PUT** `/api/contact/{id}/assign` - Assign inquiry to team member
- **PUT** `/api/contact/{id}/notes` - Add notes to inquiry
- **PUT** `/api/contact/{id}/respond` - Mark as responded
- **GET** `/api/contact/statistics` - Get inquiry statistics
- **GET** `/api/contact/recent` - Get recent inquiries
- **GET** `/api/contact/unassigned` - Get unassigned inquiries
- **DELETE** `/api/contact/{id}` - Delete inquiry

## 📊 Database Schema

### contact_inquiries Table
```sql
CREATE TABLE contact_inquiries (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    organization VARCHAR(200),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address TEXT,
    business_duration VARCHAR(50),
    project_timeline VARCHAR(50),
    business_challenge TEXT NOT NULL,
    contact_method VARCHAR(20) NOT NULL,
    preferred_time VARCHAR(100),
    hear_about VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    assigned_to VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    response_notes TEXT
);
```

### contact_service_interests Table
```sql
CREATE TABLE contact_service_interests (
    contact_inquiry_id BIGINT NOT NULL,
    service_interest VARCHAR(100) NOT NULL,
    PRIMARY KEY (contact_inquiry_id, service_interest),
    FOREIGN KEY (contact_inquiry_id) REFERENCES contact_inquiries(id) ON DELETE CASCADE
);
```

## 🔍 Testing the Integration

### 1. Test Contact Form Submission
```bash
# Using curl to test the API
curl -X POST http://localhost:8080/api/contact \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "country": "United States",
    "state": "California",
    "city": "San Francisco",
    "businessChallenge": "Need help with digital transformation",
    "contactMethod": "Email",
    "serviceInterest": ["Tech Solutions", "Operational Efficiency"]
  }'
```

### 2. Check Database
```sql
-- Connect to database
psql -h localhost -p 5432 -U postgres -d zplus_admin_panel

-- Check if data was saved
SELECT * FROM contact_inquiries ORDER BY created_at DESC LIMIT 5;

-- Check service interests
SELECT ci.full_name, csi.service_interest 
FROM contact_inquiries ci 
JOIN contact_service_interests csi ON ci.id = csi.contact_inquiry_id;
```

## 🛠️ Troubleshooting

### Common Issues:

1. **Database Connection Error**
   - Ensure PostgreSQL is running
   - Check if database `zplus_admin_panel` exists
   - Verify username/password in `application.yml`

2. **CORS Error**
   - The controller already has CORS configured for common origins
   - If using a different port, add it to the `@CrossOrigin` annotation

3. **Validation Errors**
   - Check that all required fields are filled
   - Ensure email format is valid
   - Verify at least one service interest is selected

4. **Email Service Error**
   - Configure email settings in `application.yml`
   - Check SMTP credentials
   - Email service is optional - the form will still work without it

### Debug Commands:
```bash
# Check application logs
tail -f logs/application.log

# Test database connection
psql -h localhost -p 5432 -U postgres -d zplus_admin_panel -c "SELECT COUNT(*) FROM contact_inquiries;"

# Check if application is running
curl http://localhost:8080/api/contact/health
```

## 📈 Next Steps

1. **Admin Dashboard Integration**
   - Create admin interface to view and manage inquiries
   - Add filtering and search capabilities
   - Implement status management

2. **Email Notifications**
   - Configure email service for admin notifications
   - Send confirmation emails to users
   - Set up automated follow-up reminders

3. **Analytics Dashboard**
   - Create charts and graphs for inquiry statistics
   - Track conversion rates
   - Monitor response times

4. **Advanced Features**
   - File uploads for attachments
   - Integration with CRM systems
   - Automated lead scoring

## ✅ Verification Checklist

- [ ] Database setup completed successfully
- [ ] Contact form submits data to backend
- [ ] Data is saved in PostgreSQL database
- [ ] Admin can view inquiries via API
- [ ] Status updates work correctly
- [ ] Email notifications are configured (optional)
- [ ] CORS is properly configured
- [ ] Validation is working correctly

## 🆘 Support

If you encounter any issues:

1. Check the application logs in `logs/application.log`
2. Verify database connection with `test-database-connection.bat`
3. Test API endpoints with curl or Postman
4. Ensure all required dependencies are installed

The contact form is now fully integrated with your PostgreSQL database and ready for production use! 