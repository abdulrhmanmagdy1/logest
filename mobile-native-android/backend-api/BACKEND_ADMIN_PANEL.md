# Edham Logistics Backend - Admin Panel Documentation

## 📋 Overview

The Edham Logistics Backend Admin Panel provides comprehensive administrative capabilities for managing the entire logistics system. This documentation covers all admin features, configuration options, and operational procedures.

## 🔐 Admin Access & Security

### Admin Roles & Permissions

#### Super Admin
- Full system access
- User management (all roles)
- System configuration
- Security settings
- Backup and restore
- Audit log access

#### System Admin
- User management (excluding Super Admins)
- System monitoring
- Configuration management
- Report generation
- Limited security settings

#### Operations Admin
- Shipment management
- Driver and vehicle management
- Route optimization
- Customer support
- Performance monitoring

### Authentication
```javascript
// Admin Login
POST /api/v1/admin/auth/login
{
  "email": "admin@edham-logistics.com",
  "password": "admin_password",
  "twoFactorCode": "123456" // If 2FA enabled
}

// Response
{
  "success": true,
  "data": {
    "token": "jwt_token_here",
    "user": {
      "id": 1,
      "email": "admin@edham-logistics.com",
      "role": "SUPER_ADMIN",
      "permissions": ["SYSTEM_ADMIN", "USER_MANAGEMENT", ...]
    },
    "sessionTimeout": 3600000
  }
}
```

## 📊 Dashboard Overview

### System Health Dashboard
```javascript
GET /api/v1/admin/dashboard/health
Authorization: Bearer <admin_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "systemStatus": "HEALTHY",
    "uptime": "45 days, 12:34:56",
    "activeUsers": {
      "total": 1250,
      "customers": 980,
      "drivers": 220,
      "admins": 50
    },
    "performance": {
      "apiResponseTime": 145,
      "databaseResponseTime": 23,
      "cacheHitRate": 94.5,
      "errorRate": 0.02
    },
    "resources": {
      "cpuUsage": 45.2,
      "memoryUsage": 67.8,
      "diskUsage": 34.1,
      "networkBandwidth": 125.6
    },
    "activeShipments": {
      "pending": 45,
      "inTransit": 123,
      "delivered": 2340
    }
  }
}
```

### Business Metrics Dashboard
```javascript
GET /api/v1/admin/dashboard/metrics?period=30d
Authorization: Bearer <admin_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "revenue": {
      "total": 456789.50,
      "growth": 12.5,
      "byPeriod": [
        { "date": "2023-12-01", "amount": 12345.67 },
        { "date": "2023-12-02", "amount": 15678.90 }
      ]
    },
    "shipments": {
      "total": 2340,
      "growth": 8.3,
      "byStatus": {
        "delivered": 2100,
        "inTransit": 180,
        "pending": 45,
        "cancelled": 15
      }
    },
    "customers": {
      "total": 980,
      "newThisPeriod": 45,
      "retentionRate": 92.5
    },
    "drivers": {
      "total": 220,
      "active": 198,
      "averageRating": 4.6
    }
  }
}
```

## 👥 User Management

### User Directory
```javascript
// Get all users with filtering
GET /api/v1/admin/users?page=0&size=20&role=CUSTOMER&status=ACTIVE&search=john
Authorization: Bearer <admin_token>

// Create new user
POST /api/v1/admin/users
Authorization: Bearer <admin_token>
{
  "email": "newuser@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+966501234567",
  "role": "CUSTOMER",
  "organizationId": 1,
  "active": true,
  "sendWelcomeEmail": true
}

// Update user
PUT /api/v1/admin/users/{id}
Authorization: Bearer <admin_token>
{
  "firstName": "Updated",
  "lastName": "Name",
  "phone": "+966501234568",
  "active": true
}

// Deactivate user
PUT /api/v1/admin/users/{id}/deactivate
Authorization: Bearer <admin_token>

// Reset user password
POST /api/v1/admin/users/{id}/reset-password
Authorization: Bearer <admin_token>
{
  "sendEmail": true,
  "temporaryPassword": "TempPass123!"
}
```

### Role Management
```javascript
// Get all roles
GET /api/v1/admin/roles
Authorization: Bearer <admin_token>

// Create custom role
POST /api/v1/admin/roles
Authorization: Bearer <admin_token>
{
  "name": "OPERATIONS_MANAGER",
  "displayName": "Operations Manager",
  "permissions": [
    "SHIPMENT_READ",
    "SHIPMENT_UPDATE",
    "DRIVER_READ",
    "DRIVER_UPDATE",
    "VEHICLE_READ"
  ],
  "description": "Can manage shipments and drivers"
}

// Update role permissions
PUT /api/v1/admin/roles/{id}
Authorization: Bearer <admin_token>
{
  "permissions": [
    "SHIPMENT_READ",
    "SHIPMENT_CREATE",
    "SHIPMENT_UPDATE",
    "DRIVER_READ",
    "DRIVER_UPDATE"
  ]
}
```

## 📦 Shipment Management

### Shipment Overview
```javascript
// Get all shipments with advanced filtering
GET /api/v1/admin/shipments?page=0&size=50&status=PENDING&customerId=123&driverId=456&dateFrom=2023-12-01&dateTo=2023-12-31
Authorization: Bearer <admin_token>

// Get shipment details
GET /api/v1/admin/shipments/{id}
Authorization: Bearer <admin_token>

// Create shipment (admin override)
POST /api/v1/admin/shipments
Authorization: Bearer <admin_token>
{
  "customerId": 123,
  "originAddress": "123 Main St, Riyadh",
  "destinationAddress": "456 Oak Ave, Jeddah",
  "weight": 15.5,
  "dimensions": { "length": 12, "width": 6, "height": 4 },
  "priority": "HIGH",
  "specialInstructions": "Handle with care",
  "estimatedDeliveryDate": "2023-12-05T10:00:00Z",
  "assignedDriverId": 456,
  "assignedVehicleId": 789
}

// Update shipment
PUT /api/v1/admin/shipments/{id}
Authorization: Bearer <admin_token>
{
  "status": "ASSIGNED",
  "driverId": 456,
  "vehicleId": 789,
  "priority": "HIGH",
  "estimatedDeliveryDate": "2023-12-05T10:00:00Z",
  "adminNotes": "Priority shipment - expedite delivery"
}

// Cancel shipment
PUT /api/v1/admin/shipments/{id}/cancel
Authorization: Bearer <admin_token>
{
  "reason": "Customer requested cancellation",
  "refundAmount": 50.00,
  "notifyCustomer": true
}
```

### Bulk Operations
```javascript
// Bulk assign drivers
POST /api/v1/admin/shipments/bulk-assign
Authorization: Bearer <admin_token>
{
  "shipmentIds": [1, 2, 3, 4, 5],
  "driverId": 456,
  "vehicleId": 789
}

// Bulk update status
POST /api/v1/admin/shipments/bulk-update
Authorization: Bearer <admin_token>
{
  "shipmentIds": [1, 2, 3, 4, 5],
  "status": "CANCELLED",
  "reason": "System maintenance"
}

// Bulk export
POST /api/v1/admin/shipments/export
Authorization: Bearer <admin_token>
{
  "format": "EXCEL",
  "filters": {
    "dateFrom": "2023-12-01",
    "dateTo": "2023-12-31",
    "status": ["DELIVERED", "CANCELLED"]
  },
  "fields": ["trackingNumber", "customer", "driver", "status", "cost", "deliveryDate"]
}
```

## 🚛 Fleet Management

### Vehicle Management
```javascript
// Get all vehicles
GET /api/v1/admin/vehicles?page=0&size=20&status=ACTIVE&type=TRUCK
Authorization: Bearer <admin_token>

// Create vehicle
POST /api/v1/admin/vehicles
Authorization: Bearer <admin_token>
{
  "licensePlate": "ABC-1234",
  "make": "Toyota",
  "model": "Hilux",
  "year": 2023,
  "type": "TRUCK",
  "capacity": {
    "weight": 5000,
    "volume": 25
  },
  "driverId": 456,
  "status": "ACTIVE",
  "insuranceExpiry": "2024-12-31",
  "registrationExpiry": "2024-06-30"
}

// Update vehicle maintenance
PUT /api/v1/admin/vehicles/{id}/maintenance
Authorization: Bearer <admin_token>
{
  "type": "SCHEDULED",
  "description": "Oil change and tire rotation",
  "scheduledDate": "2023-12-15",
  "estimatedCost": 500.00,
  "notifyDriver": true
}
```

### Driver Management
```javascript
// Get all drivers
GET /api/v1/admin/drivers?page=0&size=20&status=ACTIVE&rating=4.5
Authorization: Bearer <admin_token>

// Create driver
POST /api/v1/admin/drivers
Authorization: Bearer <admin_token>
{
  "userId": 789,
  "licenseNumber": "SA-123456",
  "licenseExpiry": "2025-12-31",
  "vehicleId": 123,
  "status": "ACTIVE",
  "maxWeight": 1000,
  "specializations": ["FRAGILE", "OVERSIZED"],
  "emergencyContact": {
    "name": "Jane Doe",
    "phone": "+966501234568",
    "relationship": "Spouse"
  }
}

// Update driver performance
PUT /api/v1/admin/drivers/{id}/performance
Authorization: Bearer <admin_token>
{
  "rating": 4.8,
  "totalDeliveries": 1250,
  "onTimeDeliveryRate": 95.5,
  "customerSatisfactionScore": 4.7,
  "efficiencyScore": 4.6,
  "notes": "Excellent performance this quarter"
}
```

## 💰 Financial Management

### Revenue Analytics
```javascript
// Get revenue analytics
GET /api/v1/admin/analytics/revenue?period=30d&groupBy=day
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "totalRevenue": 456789.50,
    "growth": 12.5,
    "breakdown": {
      "byService": [
        { "service": "STANDARD", "revenue": 234567.89 },
        { "service": "EXPRESS", "revenue": 123456.78 },
        { "service": "INTERNATIONAL", "revenue": 98764.83 }
      ],
      "byRegion": [
        { "region": "Riyadh", "revenue": 234567.89 },
        { "region": "Jeddah", "revenue": 123456.78 },
        { "region": "Dammam", "revenue": 98764.83 }
      ]
    },
    "trends": [
      { "date": "2023-12-01", "revenue": 12345.67 },
      { "date": "2023-12-02", "revenue": 15678.90 }
    ]
  }
}
```

### Invoice Management
```javascript
// Get all invoices
GET /api/v1/admin/invoices?page=0&size=20&status=PENDING&customerId=123
Authorization: Bearer <admin_token>

// Create manual invoice
POST /api/v1/admin/invoices
Authorization: Bearer <admin_token>
{
  "customerId": 123,
  "shipmentIds": [1, 2, 3],
  "amount": 450.00,
  "tax": 67.50,
  "totalAmount": 517.50,
  "currency": "SAR",
  "dueDate": "2023-12-15",
  "description": "Monthly invoice for December 2023",
  "paymentTerms": "NET_30",
  "notes": "Payment due within 30 days"
}

// Process payment
POST /api/v1/admin/invoices/{id}/payment
Authorization: Bearer <admin_token>
{
  "amount": 517.50,
  "paymentMethod": "BANK_TRANSFER",
  "transactionId": "TXN123456789",
  "paymentDate": "2023-12-10",
  "notes": "Payment received via bank transfer"
}
```

### Financial Reports
```javascript
// Generate financial report
POST /api/v1/admin/reports/financial
Authorization: Bearer <admin_token>
{
  "period": {
    "from": "2023-12-01",
    "to": "2023-12-31"
  },
  "type": "PROFIT_LOSS",
  "format": "PDF",
  "includeCharts": true,
  "emailTo": ["finance@edham-logistics.com"]
}
```

## 🔧 System Configuration

### Application Settings
```javascript
// Get system settings
GET /api/v1/admin/settings
Authorization: Bearer <admin_token>

// Update system settings
PUT /api/v1/admin/settings
Authorization: Bearer <admin_token>
{
  "general": {
    "companyName": "Edham Logistics",
    "supportEmail": "support@edham-logistics.com",
    "supportPhone": "+966-50-XXX-XXXX",
    "timezone": "Asia/Riyadh",
    "dateFormat": "YYYY-MM-DD",
    "currency": "SAR"
  },
  "shipment": {
    "autoAssignDriver": true,
    "defaultPriority": "STANDARD",
    "maxDeliveryDays": 7,
    "trackingNumberPrefix": "EDH"
  },
  "notifications": {
    "emailNotifications": true,
    "smsNotifications": true,
    "pushNotifications": true,
    "notificationRetention": 90
  },
  "security": {
    "passwordMinLength": 8,
    "sessionTimeout": 3600,
    "maxLoginAttempts": 5,
    "lockoutDuration": 300
  }
}
```

### Feature Flags
```javascript
// Get feature flags
GET /api/v1/admin/features
Authorization: Bearer <admin_token>

// Update feature flags
PUT /api/v1/admin/features
Authorization: Bearer <admin_token>
{
  "realTimeTracking": {
    "enabled": true,
    "description": "Enable real-time shipment tracking",
    "rolloutPercentage": 100
  },
  "advancedAnalytics": {
    "enabled": true,
    "description": "Enable advanced analytics dashboard",
    "rolloutPercentage": 50
  },
  "mobileAppV2": {
    "enabled": false,
    "description": "Enable new mobile app version",
    "rolloutPercentage": 10
  }
}
```

## 📊 Monitoring & Analytics

### System Monitoring
```javascript
// Get system metrics
GET /api/v1/admin/monitoring/metrics
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "performance": {
      "apiResponseTime": 145,
      "databaseResponseTime": 23,
      "cacheHitRate": 94.5,
      "errorRate": 0.02
    },
    "resources": {
      "cpuUsage": 45.2,
      "memoryUsage": 67.8,
      "diskUsage": 34.1,
      "networkBandwidth": 125.6
    },
    "database": {
      "connections": 45,
      "queryTime": 12.5,
      "slowQueries": 2,
      "deadlocks": 0
    },
    "cache": {
      "hitRate": 94.5,
      "memoryUsage": 256,
      "evictions": 1234
    }
  }
}
```

### User Analytics
```javascript
// Get user behavior analytics
GET /api/v1/admin/analytics/users?period=30d
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "activeUsers": {
      "total": 1250,
      "daily": 450,
      "weekly": 890,
      "monthly": 1250
    },
    "userRetention": {
      "day1": 85.5,
      "day7": 72.3,
      "day30": 58.9
    },
    "featureUsage": [
      { "feature": "SHIPMENT_CREATE", "usage": 1234 },
      { "feature": "TRACKING", "usage": 5678 },
      { "feature": "PAYMENT", "usage": 890 }
    ],
    "userSegments": [
      { "segment": "NEW_USERS", "count": 45, "percentage": 3.6 },
      { "segment": "ACTIVE_USERS", "count": 890, "percentage": 71.2 },
      { "segment": "INACTIVE_USERS", "count": 315, "percentage": 25.2 }
    ]
  }
}
```

## 🔍 Audit & Security

### Audit Logs
```javascript
// Get audit logs
GET /api/v1/admin/audit?page=0&size=50&action=LOGIN&userId=123&dateFrom=2023-12-01&dateTo=2023-12-31
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 123,
        "email": "user@example.com",
        "action": "LOGIN",
        "entityType": "USER",
        "entityId": 123,
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0...",
        "timestamp": "2023-12-01T10:30:00Z",
        "details": {
          "success": true,
          "location": "Riyadh, Saudi Arabia"
        }
      }
    ],
    "totalElements": 1234,
    "totalPages": 25
  }
}
```

### Security Events
```javascript
// Get security events
GET /api/v1/admin/security/events?severity=HIGH&dateFrom=2023-12-01
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "events": [
      {
        "id": 1,
        "type": "MULTIPLE_FAILED_LOGINS",
        "severity": "HIGH",
        "userId": 123,
        "ipAddress": "192.168.1.100",
        "timestamp": "2023-12-01T10:30:00Z",
        "details": {
          "attemptCount": 6,
          "timeWindow": "5 minutes",
          "blocked": true
        }
      }
    ]
  }
}
```

## 🔄 Backup & Recovery

### Backup Management
```javascript
// Get backup history
GET /api/v1/admin/backup/history
Authorization: Bearer <admin_token>

// Create manual backup
POST /api/v1/admin/backup/create
Authorization: Bearer <admin_token>
{
  "type": "FULL",
  "includeFiles": true,
  "compression": true,
  "encryption": true,
  "description": "Manual backup before system update"
}

// Restore from backup
POST /api/v1/admin/backup/restore
Authorization: Bearer <admin_token>
{
  "backupId": "backup_20231201_120000",
  "confirm": true,
  "notifyUsers": true
}
```

### Data Export
```javascript
// Export system data
POST /api/v1/admin/export
Authorization: Bearer <admin_token>
{
  "entities": ["USERS", "SHIPMENTS", "INVOICES"],
  "format": "CSV",
  "filters": {
    "dateFrom": "2023-12-01",
    "dateTo": "2023-12-31"
  },
  "compression": true,
  "emailTo": "admin@edham-logistics.com"
}
```

## 📧 Communication Center

### System Announcements
```javascript
// Create announcement
POST /api/v1/admin/announcements
Authorization: Bearer <admin_token>
{
  "title": "Scheduled Maintenance",
  "message": "System will be under maintenance on Dec 15, 2023 from 2:00 AM to 4:00 AM",
  "type": "MAINTENANCE",
  "priority": "HIGH",
  "targetAudience": "ALL_USERS",
  "scheduledFor": "2023-12-14T20:00:00Z",
  "expiresAt": "2023-12-15T04:00:00Z",
  "channels": ["EMAIL", "PUSH", "IN_APP"]
}

// Send bulk notifications
POST /api/v1/admin/notifications/bulk
Authorization: Bearer <admin_token>
{
  "userIds": [1, 2, 3, 4, 5],
  "title": "Important Update",
  "message": "New features have been added to the system",
  "type": "SYSTEM_UPDATE",
  "channels": ["EMAIL", "PUSH"]
}
```

## 🛠️ Maintenance Tools

### System Health Check
```javascript
// Run health check
POST /api/v1/admin/health/check
Authorization: Bearer <admin_token>

// Response
{
  "success": true,
  "data": {
    "overall": "HEALTHY",
    "checks": [
      {
        "component": "DATABASE",
        "status": "HEALTHY",
        "responseTime": 23,
        "details": "All connections working properly"
      },
      {
        "component": "CACHE",
        "status": "HEALTHY",
        "responseTime": 5,
        "details": "Cache hit rate: 94.5%"
      },
      {
        "component": "EXTERNAL_APIS",
        "status": "WARNING",
        "responseTime": 1250,
        "details": "Payment gateway response time is high"
      }
    ]
  }
}
```

### Cache Management
```javascript
// Clear cache
POST /api/v1/admin/cache/clear
Authorization: Bearer <admin_token>
{
  "type": "ALL", // ALL, USER_SESSIONS, SHIPMENT_CACHE, etc.
  "pattern": "user:*", // Optional pattern for selective clearing
  "notifyUsers": false
}

// Get cache statistics
GET /api/v1/admin/cache/stats
Authorization: Bearer <admin_token>
```

## 📈 Reporting Center

### Custom Reports
```javascript
// Create custom report
POST /api/v1/admin/reports/custom
Authorization: Bearer <admin_token>
{
  "name": "Monthly Performance Report",
  "description": "Detailed monthly performance metrics",
  "query": {
    "entities": ["SHIPMENTS", "INVOICES"],
    "filters": {
      "dateFrom": "2023-12-01",
      "dateTo": "2023-12-31"
    },
    "aggregations": [
      {
        "field": "status",
        "operation": "COUNT"
      },
      {
        "field": "cost",
        "operation": "SUM"
      }
    ]
  },
  "schedule": {
    "frequency": "MONTHLY",
    "dayOfMonth": 1,
    "time": "09:00",
    "recipients": ["admin@edham-logistics.com"]
  },
  "format": "PDF"
}

// Get report templates
GET /api/v1/admin/reports/templates
Authorization: Bearer <admin_token>
```

### Scheduled Reports
```javascript
// Get scheduled reports
GET /api/v1/admin/reports/scheduled
Authorization: Bearer <admin_token>

// Update scheduled report
PUT /api/v1/admin/reports/scheduled/{id}
Authorization: Bearer <admin_token>
{
  "schedule": {
    "frequency": "WEEKLY",
    "dayOfWeek": 1, // Monday
    "time": "09:00",
    "recipients": ["manager@edham-logistics.com"]
  }
}
```

## 🔌 API Rate Limiting

### Rate Limit Configuration
```javascript
// Get rate limit settings
GET /api/v1/admin/rate-limits
Authorization: Bearer <admin_token>

// Update rate limits
PUT /api/v1/admin/rate-limits
Authorization: Bearer <admin_token>
{
  "endpoints": [
    {
      "path": "/api/v1/auth/login",
      "method": "POST",
      "maxRequests": 5,
      "windowSeconds": 60,
      "blockDuration": 300
    },
    {
      "path": "/api/v1/shipments",
      "method": "GET",
      "maxRequests": 100,
      "windowSeconds": 60,
      "blockDuration": 60
    }
  ]
}
```

## 🚨 Alert Management

### Alert Configuration
```javascript
// Get alert rules
GET /api/v1/admin/alerts/rules
Authorization: Bearer <admin_token>

// Create alert rule
POST /api/v1/admin/alerts/rules
Authorization: Bearer <admin_token>
{
  "name": "High Error Rate",
  "description": "Alert when error rate exceeds 5%",
  "condition": {
    "metric": "error_rate",
    "operator": "GREATER_THAN",
    "threshold": 5.0,
    "timeWindow": "5m"
  },
  "actions": [
    {
      "type": "EMAIL",
      "recipients": ["admin@edham-logistics.com"]
    },
    {
      "type": "SLACK",
      "webhook": "https://hooks.slack.com/..."
    }
  ],
  "enabled": true
}
```

## 📱 Mobile App Management

### App Version Management
```javascript
// Get app versions
GET /api/v1/admin/mobile/versions
Authorization: Bearer <admin_token>

// Update app version
POST /api/v1/admin/mobile/versions
Authorization: Bearer <admin_token>
{
  "platform": "ANDROID",
  "version": "2.1.0",
  "buildNumber": 45,
  "releaseNotes": "Bug fixes and performance improvements",
  "downloadUrl": "https://play.google.com/store/apps/...",
  "mandatory": false,
  "rolloutPercentage": 50
}
```

### Push Notification Management
```javascript
// Send push notification
POST /api/v1/admin/mobile/push
Authorization: Bearer <admin_token>
{
  "title": "System Update",
  "message": "A new version of the app is available",
  "targetAudience": "ALL_USERS",
  "platforms": ["ANDROID", "IOS"],
  "data": {
    "type": "APP_UPDATE",
    "version": "2.1.0",
    "downloadUrl": "https://play.google.com/store/apps/..."
  },
  "scheduledFor": "2023-12-01T10:00:00Z"
}
```

## 🔧 Third-Party Integrations

### Integration Management
```javascript
// Get integrations
GET /api/v1/admin/integrations
Authorization: Bearer <admin_token>

// Configure integration
POST /api/v1/admin/integrations
Authorization: Bearer <admin_token>
{
  "provider": "STRIPE",
  "type": "PAYMENT_GATEWAY",
  "config": {
    "apiKey": "sk_test_...",
    "webhookSecret": "whsec_...",
    "enabled": true
  }
}
```

## 📊 Data Visualization

### Dashboard Widgets
```javascript
// Get dashboard configuration
GET /api/v1/admin/dashboard/widgets
Authorization: Bearer <admin_token>

// Update dashboard layout
PUT /api/v1/admin/dashboard/layout
Authorization: Bearer <admin_token>
{
  "widgets": [
    {
      "id": "revenue-chart",
      "type": "LINE_CHART",
      "position": { "x": 0, "y": 0, "width": 6, "height": 4 },
      "config": {
        "title": "Revenue Trend",
        "metric": "revenue",
        "period": "30d"
      }
    },
    {
      "id": "active-users",
      "type": "STAT_CARD",
      "position": { "x": 6, "y": 0, "width": 3, "height": 2 },
      "config": {
        "title": "Active Users",
        "metric": "active_users",
        "period": "24h"
      }
    }
  ]
}
```

## 🎯 Admin Panel UI Components

### Common Components

#### Data Table Component
```javascript
// Usage example
<AdminDataTable
  data={shipments}
  columns={columns}
  pagination={pagination}
  filters={filters}
  actions={actions}
  selection="multiple"
  onSelectionChange={handleSelectionChange}
  onRowClick={handleRowClick}
/>
```

#### Filter Panel Component
```javascript
// Usage example
<AdminFilterPanel
  filters={filterConfig}
  values={filterValues}
  onChange={handleFilterChange}
  onReset={handleFilterReset}
/>
```

#### Action Menu Component
```javascript
// Usage example
<AdminActionMenu
  actions={[
    { label: 'Edit', icon: 'edit', onClick: handleEdit },
    { label: 'Delete', icon: 'delete', onClick: handleDelete, danger: true }
  ]}
  trigger="click"
/>
```

## 🚀 Performance Optimization

### Admin Panel Optimization
```javascript
// Lazy loading for large datasets
const useLazyLoading = (fetchFunction) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  
  const loadMore = useCallback(async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    const newData = await fetchFunction();
    setData(prev => [...prev, ...newData]);
    setHasMore(newData.length > 0);
    setLoading(false);
  }, [fetchFunction, loading, hasMore]);
  
  return { data, loading, hasMore, loadMore };
};
```

### Caching Strategy
```javascript
// React Query for data caching
const useAdminData = (endpoint, params) => {
  return useQuery({
    queryKey: [endpoint, params],
    queryFn: () => api.get(endpoint, { params }),
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
    refetchOnWindowFocus: false
  });
};
```

## 📞 Support & Troubleshooting

### Common Issues
1. **Slow Dashboard Loading**
   - Check database indexes
   - Optimize queries
   - Implement caching

2. **Memory Issues**
   - Monitor memory usage
   - Implement pagination
   - Use lazy loading

3. **Permission Errors**
   - Verify user roles
   - Check permission mappings
   - Clear user cache

### Debug Mode
```javascript
// Enable debug mode
localStorage.setItem('admin_debug', 'true');

// Debug information will be available in console
console.log('Admin Debug Info:', {
  user: getCurrentUser(),
  permissions: getUserPermissions(),
  features: getFeatureFlags()
});
```

## 📋 Admin Panel Checklist

### Daily Tasks
- [ ] Review system health metrics
- [ ] Check critical security events
- [ ] Monitor active shipments
- [ ] Review user feedback

### Weekly Tasks
- [ ] Generate performance reports
- [ ] Review user analytics
- [ ] Check backup status
- [ ] Update system documentation

### Monthly Tasks
- [ ] Security audit review
- [ ] Performance optimization
- [ ] User access review
- [ ] System capacity planning

### Quarterly Tasks
- [ ] Disaster recovery testing
- [ ] Security penetration testing
- [ ] Performance benchmarking
- [ ] Feature usage analysis

---

This admin panel documentation provides comprehensive coverage of all administrative functions available in the Edham Logistics backend system. For specific implementation details or additional support, please refer to the API documentation or contact the development team.
