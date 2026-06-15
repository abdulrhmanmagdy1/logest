# Edham Logistics Backend API Documentation

## 📋 Overview

The Edham Logistics Backend API provides comprehensive REST endpoints for managing shipments, users, tracking, invoices, and more. This documentation covers all available endpoints, authentication, and integration guidelines.

## 🔌 Base URL

- **Development**: `http://localhost:8080/api/v1`
- **Staging**: `https://staging-api.edham-logistics.com/api/v1`
- **Production**: `https://api.edham-logistics.com/api/v1`

## 🔌 Authentication

### JWT Token Structure
```json
{
  "sub": "user_id",
  "email": "user@example.com",
  "role": "CUSTOMER|DRIVER|SUPERVISOR|ACCOUNTANT|WORKSHOP|ADMIN",
  "permissions": ["SHIPMENT_READ", "SHIPMENT_CREATE", ...],
  "organizationId": "org_id",
  "iat": 1640995200,
  "exp": 1640998800
}
```

### Authorization Header
```
Authorization: Bearer <jwt_token>
```

## 📊 API Endpoints

### Authentication Endpoints

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "deviceInfo": {
    "platform": "android|ios|web",
    "deviceId": "unique_device_id",
    "appVersion": "2.0.0"
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "CUSTOMER",
      "active": true,
      "organization": {
        "id": 1,
        "name": "Edham Logistics"
      }
    },
    "permissions": ["SHIPMENT_READ", "SHIPMENT_CREATE"],
    "roleConfig": {
      "allowedScreens": ["home", "shipments", "tracking"],
      "defaultRoute": "/home"
    }
  }
}
```

#### Register
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+966501234567",
  "role": "CUSTOMER",
  "organizationId": 1
}
```

#### Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json
Authorization: Bearer <refresh_token>

{
  "refreshToken": "refresh_token_here"
}
```

#### Logout
```http
POST /api/v1/auth/logout
Authorization: Bearer <jwt_token>
```

### User Management Endpoints

#### Get Current User
```http
GET /api/v1/users/me
Authorization: Bearer <jwt_token>
```

#### Get All Users (Admin Only)
```http
GET /api/v1/users?page=0&size=20&role=CUSTOMER&active=true
Authorization: Bearer <jwt_token>
```

#### Create User (Admin Only)
```http
POST /api/v1/users
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "email": "newuser@example.com",
  "password": "password123",
  "firstName": "New",
  "lastName": "User",
  "phone": "+966501234567",
  "role": "DRIVER",
  "organizationId": 1,
  "active": true
}
```

#### Update User
```http
PUT /api/v1/users/{id}
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "firstName": "Updated",
  "lastName": "Name",
  "phone": "+966501234568"
}
```

#### Change Password
```http
PUT /api/v1/users/{id}/password
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "currentPassword": "old_password",
  "newPassword": "new_password"
}
```

### Shipment Management Endpoints

#### Get Shipments
```http
GET /api/v1/shipments?page=0&size=20&status=PENDING&customerId=1&driverId=2
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "trackingNumber": "EDH202312011234",
        "customer": {
          "id": 1,
          "firstName": "John",
          "lastName": "Doe"
        },
        "driver": {
          "id": 2,
          "firstName": "Ahmed",
          "lastName": "Mohammed"
        },
        "originAddress": "123 Main St, Riyadh, Saudi Arabia",
        "destinationAddress": "456 Oak Ave, Jeddah, Saudi Arabia",
        "status": "PENDING",
        "weight": 10.5,
        "dimensions": {
          "length": 10,
          "width": 5,
          "height": 3
        },
        "cost": 150.00,
        "currency": "SAR",
        "estimatedDeliveryDate": "2023-12-05T10:00:00Z",
        "createdAt": "2023-12-01T08:00:00Z",
        "updatedAt": "2023-12-01T08:00:00Z"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

#### Create Shipment
```http
POST /api/v1/shipments
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "originAddress": "123 Main St, Riyadh, Saudi Arabia",
  "originCity": "Riyadh",
  "originState": "Riyadh Province",
  "originCountry": "Saudi Arabia",
  "originPostalCode": "11564",
  "originLatitude": 24.7136,
  "originLongitude": 46.6753,
  "destinationAddress": "456 Oak Ave, Jeddah, Saudi Arabia",
  "destinationCity": "Jeddah",
  "destinationState": "Makkah Province",
  "destinationCountry": "Saudi Arabia",
  "destinationPostalCode": "21442",
  "destinationLatitude": 21.3891,
  "destinationLongitude": 39.8579,
  "weight": 10.5,
  "dimensions": {
    "length": 10,
    "width": 5,
    "height": 3
  },
  "specialInstructions": "Handle with care",
  "estimatedDeliveryDate": "2023-12-05T10:00:00Z",
  "notes": "Fragile items"
}
```

#### Get Shipment Details
```http
GET /api/v1/shipments/{id}
Authorization: Bearer <jwt_token>
```

#### Update Shipment
```http
PUT /api/v1/shipments/{id}
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "status": "ASSIGNED",
  "driverId": 2,
  "vehicleId": 1,
  "estimatedDeliveryDate": "2023-12-05T10:00:00Z"
}
```

#### Assign Driver to Shipment
```http
PUT /api/v1/shipments/{id}/assign
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "driverId": 2,
  "vehicleId": 1
}
```

#### Update Shipment Status
```http
PUT /api/v1/shipments/{id}/status
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "status": "IN_TRANSIT",
  "notes": "Shipment picked up and in transit"
}
```

### Tracking Endpoints

#### Get Shipment Tracking
```http
GET /api/v1/tracking/shipment/{shipmentId}
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "shipment": {
      "id": 1,
      "trackingNumber": "EDH202312011234",
      "status": "IN_TRANSIT",
      "currentLocation": "On route to Jeddah",
      "currentLatitude": 23.8859,
      "currentLongitude": 45.0792
    },
    "trackingEvents": [
      {
        "id": 1,
        "eventType": "CREATED",
        "description": "Shipment created",
        "location": "Riyadh, Saudi Arabia",
        "latitude": 24.7136,
        "longitude": 46.6753,
        "timestamp": "2023-12-01T08:00:00Z",
        "createdBy": {
          "id": 1,
          "firstName": "System",
          "lastName": "Admin"
        }
      },
      {
        "id": 2,
        "eventType": "ASSIGNED",
        "description": "Driver assigned",
        "location": "Riyadh, Saudi Arabia",
        "latitude": 24.7136,
        "longitude": 46.6753,
        "timestamp": "2023-12-01T09:00:00Z",
        "createdBy": {
          "id": 3,
          "firstName": "Supervisor",
          "lastName": "User"
        }
      }
    ]
  }
}
```

#### Add Tracking Event
```http
POST /api/v1/tracking/shipment/{shipmentId}
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "eventType": "PICKED_UP",
  "description": "Package picked up from origin",
  "location": "Riyadh, Saudi Arabia",
  "latitude": 24.7136,
  "longitude": 46.6753
}
```

#### Update Driver Location
```http
PUT /api/v1/tracking/driver/location
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "latitude": 24.7136,
  "longitude": 46.6753,
  "timestamp": "2023-12-01T10:30:00Z"
}
```

### Invoice Management Endpoints

#### Get Invoices
```http
GET /api/v1/invoices?page=0&size=20&status=PENDING&customerId=1
Authorization: Bearer <jwt_token>
```

#### Create Invoice
```http
POST /api/v1/invoices
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "customerId": 1,
  "shipmentIds": [1, 2, 3],
  "amount": 450.00,
  "tax": 67.50,
  "totalAmount": 517.50,
  "currency": "SAR",
  "dueDate": "2023-12-15",
  "description": "Monthly invoice for December 2023",
  "notes": "Payment due within 30 days"
}
```

#### Get Invoice PDF
```http
GET /api/v1/invoices/{id}/pdf
Authorization: Bearer <jwt_token>
```

#### Process Payment
```http
POST /api/v1/invoices/{id}/pay
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "paymentMethod": "CREDIT_CARD",
  "amount": 517.50,
  "transactionId": "txn_123456789",
  "gatewayResponse": {
    "status": "succeeded",
    "id": "pi_123456789"
  }
}
```

### Mobile-Specific Endpoints

#### Customer Mobile Endpoints

##### Create Order
```http
POST /api/v1/mobile/customer/orders
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "originAddress": "123 Main St, Riyadh",
  "destinationAddress": "456 Oak Ave, Jeddah",
  "weight": 10.5,
  "dimensions": {
    "length": 10,
    "width": 5,
    "height": 3
  },
  "specialInstructions": "Handle with care"
}
```

##### Get Customer Orders
```http
GET /api/v1/mobile/customer/orders?page=0&size=20
Authorization: Bearer <jwt_token>
```

##### Track Order
```http
GET /api/v1/mobile/customer/orders/{id}/tracking
Authorization: Bearer <jwt_token>
```

#### Driver Mobile Endpoints

##### Get Active Task
```http
GET /api/v1/mobile/driver/active-task
Authorization: Bearer <jwt_token>
```

##### Update Location
```http
PUT /api/v1/mobile/driver/location
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "latitude": 24.7136,
  "longitude": 46.6753,
  "accuracy": 10.0,
  "timestamp": "2023-12-01T10:30:00Z"
}
```

##### Update Order Status
```http
PATCH /api/v1/mobile/driver/orders/{id}/status
Content-Type: application/json
Authorization: Bearer <jwt_token>

{
  "status": "DELIVERED",
  "notes": "Delivered successfully",
  "deliveryTime": "2023-12-01T14:30:00Z"
}
```

##### Upload Proof of Delivery
```http
POST /api/v1/mobile/driver/orders/{id}/proof
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

Form Data:
- photos: [File, File, File]
- signature: File
- notes: "Delivered to customer"
- timestamp: "2023-12-01T14:30:00Z"
```

### File Management Endpoints

#### Upload File
```http
POST /api/v1/files/upload
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

Form Data:
- file: File
- type: "SHIPMENT_PHOTO|DELIVERY_PROOF|PROFILE_PICTURE|DOCUMENT"
- entityId: 123
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "file_123456",
    "filename": "photo.jpg",
    "originalName": "shipment_photo.jpg",
    "mimeType": "image/jpeg",
    "size": 2048576,
    "url": "https://api.edham-logistics.com/uploads/file_123456",
    "thumbnailUrl": "https://api.edham-logistics.com/uploads/thumbnails/file_123456"
  }
}
```

### Analytics Endpoints

#### Get Dashboard Data
```http
GET /api/v1/analytics/dashboard?dateRange=30
Authorization: Bearer <jwt_token>
```

#### Get Shipment Statistics
```http
GET /api/v1/analytics/shipments?startDate=2023-12-01&endDate=2023-12-31
Authorization: Bearer <jwt_token>
```

#### Get Revenue Analytics
```http
GET /api/v1/analytics/revenue?startDate=2023-12-01&endDate=2023-12-31
Authorization: Bearer <jwt_token>
```

## 🔄 WebSocket Events

### Connection
```javascript
const socket = io('wss://api.edham-logistics.com/ws/tracking', {
  auth: {
    token: 'jwt_token_here'
  }
});
```

### Events

#### Shipment Status Update
```json
{
  "type": "SHIPMENT_STATUS_UPDATE",
  "data": {
    "shipmentId": 123,
    "oldStatus": "IN_TRANSIT",
    "newStatus": "DELIVERED",
    "timestamp": "2023-12-01T14:30:00Z"
  }
}
```

#### Driver Location Update
```json
{
  "type": "DRIVER_LOCATION_UPDATE",
  "data": {
    "driverId": 456,
    "shipmentId": 123,
    "latitude": 24.7136,
    "longitude": 46.6753,
    "timestamp": "2023-12-01T10:30:00Z"
  }
}
```

#### Notification
```json
{
  "type": "NOTIFICATION",
  "data": {
    "userId": 789,
    "type": "SHIPMENT_DELIVERED",
    "title": "Shipment Delivered",
    "message": "Your shipment has been delivered successfully",
    "data": {
      "shipmentId": 123,
      "trackingNumber": "EDH202312011234"
    }
  }
}
```

## 🚨 Error Responses

### Standard Error Format
```json
{
  "success": false,
  "error": {
    "code": "SHIPMENT_NOT_FOUND",
    "message": "Shipment not found",
    "details": {
      "shipmentId": 12345
    }
  },
  "timestamp": "2023-12-01T10:30:00Z",
  "path": "/api/v1/shipments/12345"
}
```

### Error Codes

#### Authentication Errors
- `INVALID_CREDENTIALS` - Invalid email or password
- `TOKEN_EXPIRED` - JWT token has expired
- `TOKEN_INVALID` - JWT token is invalid
- `ACCESS_DENIED` - Insufficient permissions

#### Validation Errors
- `VALIDATION_ERROR` - Request validation failed
- `MISSING_REQUIRED_FIELD` - Required field is missing
- `INVALID_FORMAT` - Invalid data format

#### Business Logic Errors
- `SHIPMENT_NOT_FOUND` - Shipment not found
- `SHIPMENT_ALREADY_DELIVERED` - Cannot update delivered shipment
- `INVALID_STATUS_TRANSITION` - Invalid status transition
- `DRIVER_NOT_AVAILABLE` - Driver is not available

#### System Errors
- `INTERNAL_SERVER_ERROR` - Internal server error
- `DATABASE_ERROR` - Database operation failed
- `EXTERNAL_SERVICE_ERROR` - External service error

## 📊 Rate Limiting

### Rate Limit Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

### Rate Limits by Endpoint
- **Authentication**: 5 requests per minute
- **General API**: 100 requests per minute
- **Location Updates**: 5000 requests per 15 minutes
- **File Upload**: 10 requests per minute

## 🔍 Search and Filtering

### Shipment Search
```http
GET /api/v1/shipments/search?q=riyadh&status=IN_TRANSIT&dateFrom=2023-12-01&dateTo=2023-12-31
Authorization: Bearer <jwt_token>
```

### Advanced Filtering
```http
GET /api/v1/shipments?filter[status]=PENDING,ASSIGNED&filter[weight][gt]=10&filter[cost][lte]=200
Authorization: Bearer <jwt_token>
```

### Sorting
```http
GET /api/v1/shipments?sort=createdAt,desc&sort=cost,asc
Authorization: Bearer <jwt_token>
```

## 📄 Pagination

### Pagination Parameters
- `page` - Page number (0-based)
- `size` - Page size (default: 20, max: 100)
- `sort` - Sort field and direction (field,direction)

### Pagination Response
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

## 🧪 Testing

### Test Environment
- **URL**: `https://test-api.edham-logistics.com/api/v1`
- **Test Credentials**: 
  - Customer: `test.customer@example.com` / `password123`
  - Driver: `test.driver@example.com` / `password123`
  - Admin: `test.admin@example.com` / `password123`

### Postman Collection
A complete Postman collection is available at:
`https://api.edham-logistics.com/postman-collection`

## 📞 Support

### Documentation
- **API Reference**: https://docs.edham-logistics.com
- **Interactive Docs**: https://api.edham-logistics.com/swagger-ui.html
- **OpenAPI Spec**: https://api.edham-logistics.com/v3/api-docs

### Status and Monitoring
- **API Status**: https://status.edham-logistics.com
- **Uptime Monitor**: https://uptime.edham-logistics.com
- **Performance Metrics**: https://metrics.edham-logistics.com

### Contact
- **Technical Support**: api-support@edham-logistics.com
- **Business Support**: business@edham-logistics.com
- **Security Issues**: security@edham-logistics.com

## 📝 Changelog

### v2.0.0 (Current)
- Added mobile-specific endpoints
- Enhanced WebSocket support
- Improved error handling
- Added rate limiting
- Enhanced search and filtering

### v1.0.0
- Initial release
- Basic CRUD operations
- Authentication and authorization
- WebSocket tracking

---

This API documentation provides comprehensive information for integrating with the Edham Logistics backend system. For additional support or questions, please contact our support team.
