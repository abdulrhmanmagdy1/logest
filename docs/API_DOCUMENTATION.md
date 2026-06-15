# 📚 API Documentation - نظام إدهام

## Edham Logistics API Documentation

### Base URL
```
Development: http://localhost:5000/api
Production: https://api.edham.com/api
```

### Authentication
Most endpoints require JWT token in the header:
```
Authorization: Bearer <token>
```

---

## 🔐 Authentication Endpoints

### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "string",
  "email": "string",
  "password": "string",
  "phone": "string",
  "role": "admin|manager|driver|client"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "jwt_token",
    "user": {
      "_id": "string",
      "name": "string",
      "email": "string",
      "role": "string"
    }
  }
}
```

### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <token>
```

---

## 📦 Shipment Endpoints

### Get All Shipments
```http
GET /api/shipments
Authorization: Bearer <token>
Query Parameters:
- page: number (default: 1)
- limit: number (default: 10)
- status: string (optional)
- city: string (optional)
```

### Get Shipment by ID
```http
GET /api/shipments/:id
Authorization: Bearer <token>
```

### Create Shipment
```http
POST /api/shipments
Authorization: Bearer <token>
Content-Type: application/json

{
  "description": "string",
  "weight": number,
  "quantity": number,
  "pickupLocation": {
    "address": "string",
    "city": "string",
    "contactName": "string",
    "contactPhone": "string"
  },
  "deliveryLocation": {
    "address": "string",
    "city": "string",
    "contactName": "string",
    "contactPhone": "string"
  },
  "estimatedCost": number,
  "specialInstructions": "string"
}
```

### Update Shipment Status
```http
PATCH /api/shipments/:id/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "pending|processing|in_transit|delivered|cancelled|delayed"
}
```

### Assign Shipment to Truck
```http
POST /api/shipments/:id/assign
Authorization: Bearer <token>
Content-Type: application/json

{
  "truckId": "string",
  "driverId": "string"
}
```

---

## 🚚 Truck Endpoints

### Get All Trucks
```http
GET /api/trucks
Authorization: Bearer <token>
```

### Create Truck
```http
POST /api/trucks
Authorization: Bearer <token>
Content-Type: application/json

{
  "truckNumber": "string",
  "plateNumber": "string",
  "capacity": number,
  "type": "refrigerated|standard",
  "status": "active|maintenance|inactive"
}
```

### Update Truck Location
```http
PATCH /api/trucks/:id/location
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": number,
  "longitude": number
}
```

### Get Fleet Statistics
```http
GET /api/trucks/statistics
Authorization: Bearer <token>
```

---

## 💰 Invoice Endpoints

### Get All Invoices
```http
GET /api/invoices
Authorization: Bearer <token>
```

### Create Invoice
```http
POST /api/invoices
Authorization: Bearer <token>
Content-Type: application/json

{
  "clientId": "string",
  "items": [
    {
      "description": "string",
      "quantity": number,
      "unitPrice": number
    }
  ],
  "subtotal": number,
  "tax": number,
  "total": number
}
```

### Record Payment
```http
POST /api/invoices/:id/payment
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": number,
  "method": "cash|bank_transfer|credit_card",
  "reference": "string"
}
```

### Generate Invoice PDF
```http
GET /api/invoices/:id/pdf
Authorization: Bearer <token>
```

---

## 🔧 Maintenance Endpoints

### Get All Maintenance Records
```http
GET /api/maintenance
Authorization: Bearer <token>
```

### Create Maintenance Record
```http
POST /api/maintenance
Authorization: Bearer <token>
Content-Type: application/json

{
  "truckId": "string",
  "type": "routine|repair|oil_change",
  "description": "string",
  "estimatedCost": number,
  "scheduledDate": "date"
}
```

### Complete Maintenance
```http
PATCH /api/maintenance/:id/complete
Authorization: Bearer <token>
Content-Type: application/json

{
  "actualCost": number,
  "technician": "string",
  "notes": "string"
}
```

### Get Maintenance Alerts
```http
GET /api/maintenance/alerts
Authorization: Bearer <token>
```

---

## 📊 Analytics Endpoints

### Get Dashboard Metrics
```http
GET /api/analytics/dashboard
Authorization: Bearer <token>
Query Parameters:
- timeRange: week|month|year (default: month)
```

**Response:**
```json
{
  "success": true,
  "data": {
    "shipments": {
      "total": number,
      "completed": number,
      "active": number,
      "completionRate": number
    },
    "revenue": {
      "totalRevenue": number,
      "totalPaid": number,
      "totalPending": number
    },
    "fleet": {
      "total": number,
      "active": number,
      "utilization": number
    },
    "delivery": {
      "avgTime": number,
      "onTimeRate": number
    }
  }
}
```

### Get Monthly Report
```http
GET /api/analytics/monthly-report
Authorization: Bearer <token>
```

### Get Driver Performance
```http
GET /api/analytics/driver-performance
Authorization: Bearer <token>
```

---

## 📍 Location Endpoints

### Update Truck Location (Socket.IO)
```javascript
socket.emit('updateLocation', {
  truckId: 'string',
  latitude: number,
  longitude: number,
  speed: number,
  heading: number
});
```

### Get Shipment Tracking
```http
GET /api/locations/shipment/:id/tracking
Authorization: Bearer <token>
```

---

## Error Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 429 | Too Many Requests |
| 500 | Internal Server Error |

---

## Rate Limiting

- General API: 100 requests per 15 minutes
- Auth endpoints: 5 requests per 15 minutes

---

## WebSocket Events

### Client → Server

- `joinShipment` - Join a shipment room
- `updateLocation` - Update truck location
- `updateTripStatus` - Update trip status
- `updateShipment` - Update shipment status

### Server → Client

- `locationUpdated` - Location update notification
- `tripStatusUpdated` - Trip status update
- `shipmentUpdated` - Shipment status update
- `notification` - General notification
