# Z+ Management Platform API Documentation

## Overview

The Z+ Management Platform provides a comprehensive REST API for managing contacts, users, and administrative functions. This document outlines all available endpoints, authentication requirements, and usage examples.

## Base URL

```
Production: https://your-domain.com/api
Development: http://localhost:8080/api
```

## Authentication

### JWT Token Authentication

The API uses JWT (JSON Web Token) for authentication. Include the token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

### Getting a Token

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "your_username",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "your_username",
  "roles": ["ROLE_ADMIN"],
  "expiresIn": 86400
}
```

### Refreshing a Token

**Endpoint:** `POST /api/auth/refresh`

**Headers:**
```http
Authorization: Bearer <your-current-token>
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresIn": 86400
}
```

## User Roles

- **ADMIN**: Full access to all endpoints
- **EMPLOYEE**: Access to contact management and client information
- **CLIENT**: Limited access to own information and inquiries

## API Endpoints

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

#### Register User
```http
POST /api/auth/register
Content-Type: application/json
Authorization: Bearer <admin-token>

{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "role": "EMPLOYEE"
}
```

### Contact Management

#### Get All Contacts
```http
GET /api/contacts?page=0&size=10&sort=createdAt,desc
Authorization: Bearer <token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort criteria (default: createdAt,desc)
- `search` (optional): Search term for filtering

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "+1234567890",
      "subject": "General Inquiry",
      "message": "Hello, I have a question...",
      "status": "NEW",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:00:00Z",
      "sharedWith": [],
      "sharedAt": null,
      "sharedBy": null,
      "shareNotes": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "direction": "DESC",
      "orderBy": "createdAt"
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

#### Get Contact by ID
```http
GET /api/contacts/{id}
Authorization: Bearer <token>
```

#### Create Contact
```http
POST /api/contacts
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "subject": "General Inquiry",
  "message": "Hello, I have a question about your services."
}
```

#### Update Contact
```http
PUT /api/contacts/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "subject": "Updated Subject",
  "message": "Updated message content.",
  "status": "IN_PROGRESS"
}
```

#### Delete Contact
```http
DELETE /api/contacts/{id}
Authorization: Bearer <admin-token>
```

#### Share Contact
```http
POST /api/contacts/{id}/share
Content-Type: application/json
Authorization: Bearer <token>

{
  "sharedWith": ["employee1", "employee2"],
  "shareNotes": "Please review and follow up"
}
```

#### Export Contacts
```http
GET /api/contacts/export?format=csv&startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

**Query Parameters:**
- `format`: Export format (csv, excel, pdf)
- `startDate` (optional): Start date filter (ISO date)
- `endDate` (optional): End date filter (ISO date)
- `status` (optional): Status filter

### User Management

#### Get All Users
```http
GET /api/users?page=0&size=10
Authorization: Bearer <admin-token>
```

#### Get User Profile
```http
GET /api/users/profile
Authorization: Bearer <token>
```

#### Update User Profile
```http
PUT /api/users/profile
Content-Type: application/json
Authorization: Bearer <token>

{
  "email": "newemail@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

#### Change Password
```http
POST /api/users/change-password
Content-Type: application/json
Authorization: Bearer <token>

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

#### Delete User
```http
DELETE /api/users/{id}
Authorization: Bearer <admin-token>
```

### Dashboard Endpoints

#### Admin Dashboard Stats
```http
GET /api/admin/dashboard/stats
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "totalUsers": 150,
  "totalContacts": 500,
  "newContactsToday": 15,
  "pendingInquiries": 25,
  "resolvedInquiries": 450,
  "systemHealth": "HEALTHY"
}
```

#### Employee Dashboard Stats
```http
GET /api/employee/dashboard/stats
Authorization: Bearer <employee-token>
```

#### Recent Activities
```http
GET /api/dashboard/activities?limit=10
Authorization: Bearer <token>
```

### File Upload Endpoints

#### Upload File
```http
POST /api/files/upload
Content-Type: multipart/form-data
Authorization: Bearer <token>

file: <binary-data>
```

#### Download File
```http
GET /api/files/{fileId}
Authorization: Bearer <token>
```

### System Endpoints

#### Health Check
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

#### Application Info
```http
GET /actuator/info
```

## Error Handling

### Error Response Format

```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/contacts",
  "details": {
    "email": "Email format is invalid",
    "name": "Name is required"
  }
}
```

### HTTP Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Request successful, no content returned
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `422 Unprocessable Entity` - Validation errors
- `500 Internal Server Error` - Server error

## Rate Limiting

API requests are rate-limited to prevent abuse:

- **Authenticated users**: 1000 requests per hour
- **Unauthenticated users**: 100 requests per hour

Rate limit headers are included in responses:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1640995200
```

## Pagination

All list endpoints support pagination using query parameters:

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 10, max: 100)
- `sort`: Sort criteria (format: `property,direction`)

**Example:**
```http
GET /api/contacts?page=1&size=20&sort=name,asc&sort=createdAt,desc
```

## Filtering and Search

### Search Contacts
```http
GET /api/contacts?search=john&status=NEW&fromDate=2024-01-01
```

**Supported filters:**
- `search`: Text search in name, email, subject, message
- `status`: Filter by status (NEW, IN_PROGRESS, RESOLVED, CLOSED)
- `fromDate`: Filter contacts created after date
- `toDate`: Filter contacts created before date

### Advanced Search
```http
POST /api/contacts/search
Content-Type: application/json
Authorization: Bearer <token>

{
  "criteria": [
    {
      "field": "name",
      "operator": "CONTAINS",
      "value": "john"
    },
    {
      "field": "status",
      "operator": "EQUALS",
      "value": "NEW"
    },
    {
      "field": "createdAt",
      "operator": "GREATER_THAN",
      "value": "2024-01-01"
    }
  ],
  "logic": "AND",
  "page": 0,
  "size": 10,
  "sort": "createdAt,desc"
}
```

## WebSocket Endpoints

### Real-time Notifications
```javascript
// Connect to WebSocket
const socket = new WebSocket('ws://localhost:8080/ws/notifications');

// Authentication
socket.send(JSON.stringify({
  type: 'auth',
  token: 'your-jwt-token'
}));

// Listen for notifications
socket.onmessage = (event) => {
  const notification = JSON.parse(event.data);
  console.log('New notification:', notification);
};
```

### Notification Types
- `NEW_CONTACT` - New contact inquiry received
- `CONTACT_SHARED` - Contact shared with user
- `STATUS_UPDATED` - Contact status changed
- `USER_LOGIN` - User logged in
- `SYSTEM_ALERT` - System maintenance or alerts

## SDK and Client Libraries

### JavaScript/Node.js
```javascript
const ZPlusAPI = require('zplus-api-client');

const client = new ZPlusAPI({
  baseURL: 'http://localhost:8080/api',
  token: 'your-jwt-token'
});

// Get contacts
const contacts = await client.contacts.getAll({
  page: 0,
  size: 10,
  search: 'john'
});

// Create contact
const newContact = await client.contacts.create({
  name: 'John Doe',
  email: 'john@example.com',
  subject: 'Inquiry',
  message: 'Hello world'
});
```

### Python
```python
from zplus_api import ZPlusClient

client = ZPlusClient(
    base_url='http://localhost:8080/api',
    token='your-jwt-token'
)

# Get contacts
contacts = client.contacts.get_all(page=0, size=10, search='john')

# Create contact
new_contact = client.contacts.create({
    'name': 'John Doe',
    'email': 'john@example.com',
    'subject': 'Inquiry',
    'message': 'Hello world'
})
```

## Testing

### cURL Examples

#### Login
```bash
curl -X POST \
  http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### Get Contacts
```bash
curl -X GET \
  'http://localhost:8080/api/contacts?page=0&size=10' \
  -H 'Authorization: Bearer your-jwt-token'
```

#### Create Contact
```bash
curl -X POST \
  http://localhost:8080/api/contacts \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer your-jwt-token' \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "subject": "General Inquiry",
    "message": "Hello, I have a question about your services."
  }'
```

### Postman Collection

A Postman collection with all API endpoints is available:
- [Download Postman Collection](./docs/ZPlus-API.postman_collection.json)
- [Download Environment](./docs/ZPlus-Local.postman_environment.json)

## Changelog

### Version 1.0.0
- Initial API release
- Contact management endpoints
- User authentication and management
- Dashboard statistics
- File upload functionality
- Real-time notifications via WebSocket

## Support

For API support and questions:
- **Documentation**: [API Docs](https://your-domain.com/api-docs)
- **Issues**: [GitHub Issues](https://github.com/your-org/zplus/issues)
- **Email**: api-support@zplus.com

---

**Note**: This API is currently in version 1.0. Breaking changes will increment the major version number. Always check the API version before upgrading your client applications.
