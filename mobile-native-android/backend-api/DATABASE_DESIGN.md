# Edham Logistics Backend - Database Design Documentation

## 📋 Overview

This document provides comprehensive information about the database design for the Edham Logistics backend system. The database is built using PostgreSQL with a focus on scalability, performance, and data integrity.

## 🏗️ Architecture

### Database Technology
- **Primary Database**: PostgreSQL 14+
- **Caching Layer**: Redis
- **Migration Tool**: Flyway
- **Connection Pool**: HikariCP

### Design Principles
- **ACID Compliance**: Ensuring data integrity
- **Scalability**: Designed for horizontal scaling
- **Performance**: Optimized indexes and queries
- **Security**: Role-based access control
- **Audit Trail**: Complete activity logging

## 📊 Database Schema

### Core Tables

#### 1. Users Table
```sql
users
├── id (BIGSERIAL, PRIMARY KEY)
├── email (VARCHAR(255), UNIQUE, NOT NULL)
├── password (VARCHAR(255), NOT NULL)
├── first_name (VARCHAR(100), NOT NULL)
├── last_name (VARCHAR(100), NOT NULL)
├── phone (VARCHAR(20))
├── role (user_role, ENUM, NOT NULL)
├── organization_id (BIGINT, FK)
├── active (BOOLEAN, NOT NULL DEFAULT true)
├── email_verified (BOOLEAN, NOT NULL DEFAULT false)
├── phone_verified (BOOLEAN, NOT NULL DEFAULT false)
├── last_login_at (TIMESTAMP)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

**Roles Available:**
- `CUSTOMER` - Regular customers
- `DRIVER` - Delivery drivers
- `SUPERVISOR` - Operations supervisors
- `ACCOUNTANT` - Financial accountants
- `WORKSHOP` - Maintenance workshop staff
- `ADMIN` - System administrators
- `SUPER_ADMIN` - Super administrators

#### 2. Organizations Table
```sql
organizations
├── id (BIGSERIAL, PRIMARY KEY)
├── name (VARCHAR(255), NOT NULL)
├── description (TEXT)
├── tax_number (VARCHAR(50))
├── commercial_registration (VARCHAR(50))
├── phone (VARCHAR(20))
├── email (VARCHAR(255))
├── address (TEXT)
├── city (VARCHAR(100))
├── state (VARCHAR(100))
├── country (VARCHAR(100))
├── postal_code (VARCHAR(20))
├── active (BOOLEAN, NOT NULL DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 3. Vehicles Table
```sql
vehicles
├── id (BIGSERIAL, PRIMARY KEY)
├── license_plate (VARCHAR(20), UNIQUE, NOT NULL)
├── make (VARCHAR(50), NOT NULL)
├── model (VARCHAR(50), NOT NULL)
├── year (INTEGER, NOT NULL)
├── type (VARCHAR(50), NOT NULL)
├── capacity (DECIMAL(10,2))
├── fuel_type (VARCHAR(50))
├── registration_number (VARCHAR(50))
├── insurance_number (VARCHAR(50))
├── insurance_expiry (DATE)
├── maintenance_due (DATE)
├── status (VARCHAR(50), NOT NULL DEFAULT 'ACTIVE')
├── driver_id (BIGINT, FK → users.id)
├── organization_id (BIGINT, FK → organizations.id)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 4. Shipments Table
```sql
shipments
├── id (BIGSERIAL, PRIMARY KEY)
├── tracking_number (VARCHAR(50), UNIQUE, NOT NULL)
├── customer_id (BIGINT, FK → users.id, NOT NULL)
├── driver_id (BIGINT, FK → users.id)
├── vehicle_id (BIGINT, FK → vehicles.id)
├── origin_address (TEXT, NOT NULL)
├── origin_city (VARCHAR(100))
├── origin_state (VARCHAR(100))
├── origin_country (VARCHAR(100))
├── origin_postal_code (VARCHAR(20))
├── origin_latitude (DECIMAL(10,8))
├── origin_longitude (DECIMAL(11,8))
├── destination_address (TEXT, NOT NULL)
├── destination_city (VARCHAR(100))
├── destination_state (VARCHAR(100))
├── destination_country (VARCHAR(100))
├── destination_postal_code (VARCHAR(20))
├── destination_latitude (DECIMAL(10,8))
├── destination_longitude (DECIMAL(11,8))
├── current_location (TEXT)
├── current_latitude (DECIMAL(10,8))
├── current_longitude (DECIMAL(11,8))
├── status (shipment_status, ENUM, NOT NULL DEFAULT 'PENDING')
├── weight (DECIMAL(10,2))
├── dimensions (TEXT) -- JSON: {length, width, height}
├── special_instructions (TEXT)
├── estimated_delivery_date (TIMESTAMP)
├── actual_delivery_date (TIMESTAMP)
├── pickup_date (TIMESTAMP)
├── delivery_time (TIMESTAMP)
├── cost (DECIMAL(10,2))
├── currency (VARCHAR(3), DEFAULT 'USD')
├── payment_status (VARCHAR(50), DEFAULT 'PENDING')
├── notes (TEXT)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

**Shipment Status Flow:**
```
PENDING → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
    ↓         ↓          ↓           ↓          ↓
  CANCELLED  CANCELLED  CANCELLED   DELAYED   COMPLETED
```

#### 5. Tracking Events Table
```sql
tracking_events
├── id (BIGSERIAL, PRIMARY KEY)
├── shipment_id (BIGINT, FK → shipments.id, NOT NULL)
├── event_type (tracking_event_type, ENUM, NOT NULL)
├── description (TEXT)
├── location (TEXT)
├── latitude (DECIMAL(10,8))
├── longitude (DECIMAL(11,8))
├── timestamp (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── created_by (BIGINT, FK → users.id)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

**Tracking Event Types:**
- `CREATED` - Shipment created
- `ASSIGNED` - Driver assigned
- `PICKED_UP` - Package picked up
- `IN_TRANSIT` - Currently in transit
- `DELIVERED` - Successfully delivered
- `DELAYED` - Delivery delayed
- `CANCELLED` - Shipment cancelled
- `LOCATION_UPDATE` - Driver location update

#### 6. Invoices Table
```sql
invoices
├── id (BIGSERIAL, PRIMARY KEY)
├── invoice_number (VARCHAR(50), UNIQUE, NOT NULL)
├── customer_id (BIGINT, FK → users.id, NOT NULL)
├── shipment_ids (TEXT) -- JSON array of shipment IDs
├── amount (DECIMAL(10,2), NOT NULL)
├── tax (DECIMAL(10,2), DEFAULT 0)
├── total_amount (DECIMAL(10,2), NOT NULL)
├── currency (VARCHAR(3), DEFAULT 'USD')
├── due_date (DATE, NOT NULL)
├── description (TEXT)
├── status (invoice_status, ENUM, NOT NULL DEFAULT 'PENDING')
├── paid_at (TIMESTAMP)
├── payment_method (VARCHAR(50))
├── transaction_id (VARCHAR(100))
├── notes (TEXT)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 7. Payments Table
```sql
payments
├── id (BIGSERIAL, PRIMARY KEY)
├── invoice_id (BIGINT, FK → invoices.id, NOT NULL)
├── amount (DECIMAL(10,2), NOT NULL)
├── payment_method (VARCHAR(50), NOT NULL)
├── transaction_id (VARCHAR(100))
├── status (payment_status, ENUM, NOT NULL DEFAULT 'PENDING')
├── payment_date (TIMESTAMP)
├── gateway_response (TEXT)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

### Supporting Tables

#### 8. User Sessions Table
```sql
user_sessions
├── id (BIGSERIAL, PRIMARY KEY)
├── user_id (BIGINT, FK → users.id, NOT NULL)
├── session_token (VARCHAR(255), UNIQUE, NOT NULL)
├── refresh_token (VARCHAR(255), UNIQUE)
├── device_info (TEXT) -- JSON format
├── ip_address (INET)
├── user_agent (TEXT)
├── expires_at (TIMESTAMP, NOT NULL)
├── is_active (BOOLEAN, NOT NULL DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 9. Audit Logs Table
```sql
audit_logs
├── id (BIGSERIAL, PRIMARY KEY)
├── user_id (BIGINT, FK → users.id)
├── entity_type (VARCHAR(50), NOT NULL)
├── entity_id (BIGINT)
├── action (VARCHAR(50), NOT NULL)
├── old_values (TEXT) -- JSON format
├── new_values (TEXT) -- JSON format
├── ip_address (INET)
├── user_agent (TEXT)
├── timestamp (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 10. Notifications Table
```sql
notifications
├── id (BIGSERIAL, PRIMARY KEY)
├── user_id (BIGINT, FK → users.id, NOT NULL)
├── type (VARCHAR(50), NOT NULL)
├── title (VARCHAR(255), NOT NULL)
├── message (TEXT, NOT NULL)
├── data (TEXT) -- JSON format for additional data
├── read (BOOLEAN, NOT NULL DEFAULT false)
├── read_at (TIMESTAMP)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 11. User Preferences Table
```sql
user_preferences
├── id (BIGSERIAL, PRIMARY KEY)
├── user_id (BIGINT, FK → users.id, NOT NULL)
├── preference_key (VARCHAR(100), NOT NULL)
├── preference_value (TEXT, NOT NULL)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
├── updated_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

#### 12. Backup Logs Table
```sql
backup_logs
├── id (BIGSERIAL, PRIMARY KEY)
├── backup_type (VARCHAR(50), NOT NULL)
├── file_path (VARCHAR(500))
├── file_size (BIGINT)
├── status (VARCHAR(50), NOT NULL)
├── started_at (TIMESTAMP, NOT NULL)
├── completed_at (TIMESTAMP)
├── error_message (TEXT)
├── created_by (BIGINT, FK → users.id)
├── created_at (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
```

## 🚀 Performance Optimization

### Indexes

#### Primary Indexes
```sql
-- User-related indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_organization_id ON users(organization_id);

-- Shipment-related indexes
CREATE INDEX idx_shipments_customer_id ON shipments(customer_id);
CREATE INDEX idx_shipments_driver_id ON shipments(driver_id);
CREATE INDEX idx_shipments_vehicle_id ON shipments(vehicle_id);
CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_tracking_number ON shipments(tracking_number);
CREATE INDEX idx_shipments_created_at ON shipments(created_at);

-- Composite indexes for common queries
CREATE INDEX idx_shipments_customer_status ON shipments(customer_id, status);
CREATE INDEX idx_shipments_driver_status ON shipments(driver_id, status) WHERE driver_id IS NOT NULL;
CREATE INDEX idx_shipments_created_status ON shipments(created_at DESC, status);

-- Location-based indexes
CREATE INDEX idx_shipments_location_coords ON shipments USING GIST(ST_Point(current_longitude, current_latitude)) WHERE current_latitude IS NOT NULL AND current_longitude IS NOT NULL;
CREATE INDEX idx_tracking_events_location_coords ON tracking_events USING GIST(ST_Point(longitude, latitude)) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Invoice and payment indexes
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
```

#### Full-Text Search Indexes
```sql
-- Text search for shipments
CREATE INDEX idx_shipments_search ON shipments USING GIN(
    to_tsvector('english', tracking_number || ' ' || COALESCE(origin_address, '') || ' ' || COALESCE(destination_address, '') || ' ' || COALESCE(notes, ''))
);

-- Text search for users
CREATE INDEX idx_users_search ON users USING GIN(
    to_tsvector('english', first_name || ' ' || last_name || ' ' || COALESCE(email, '') || ' ' || COALESCE(phone, ''))
);

-- Text search for vehicles
CREATE INDEX idx_vehicles_search ON vehicles USING GIN(
    to_tsvector('english', license_plate || ' ' || make || ' ' || model || ' ' || COALESCE(type, ''))
);
```

### Materialized Views

#### Shipment Statistics
```sql
CREATE MATERIALIZED VIEW shipment_statistics AS
SELECT 
    DATE_TRUNC('day', created_at) as date,
    status,
    COUNT(*) as count,
    AVG(cost) as avg_cost,
    SUM(cost) as total_cost
FROM shipments
GROUP BY DATE_TRUNC('day', created_at), status
ORDER BY date DESC;
```

#### Revenue Statistics
```sql
CREATE MATERIALIZED VIEW revenue_statistics AS
SELECT 
    DATE_TRUNC('day', created_at) as date,
    status,
    COUNT(*) as invoice_count,
    SUM(total_amount) as total_revenue,
    AVG(total_amount) as avg_invoice_amount
FROM invoices
GROUP BY DATE_TRUNC('day', created_at), status
ORDER BY date DESC;
```

## 🔧 Stored Procedures

### 1. Intelligent Shipment Assignment
```sql
CALL assign_shipment_to_driver(
    p_shipment_id BIGINT,
    p_driver_id BIGINT,
    p_vehicle_id BIGINT,
    p_assigned_by BIGINT
);
```

### 2. Bulk Invoice Generation
```sql
CALL generate_monthly_invoices(
    p_month INTEGER,
    p_year INTEGER,
    p_generated_by BIGINT
);
```

### 3. Payment Reminder Automation
```sql
CALL send_payment_reminders(
    p_days_before_due INTEGER DEFAULT 3,
    p_test_mode BOOLEAN DEFAULT FALSE
);
```

## 📊 Functions

### 1. Driver Recommendation
```sql
SELECT * FROM recommend_driver_for_shipment(
    p_shipment_id BIGINT,
    p_latitude DECIMAL(10,8),
    p_longitude DECIMAL(11,8)
);
```

### 2. Delay Prediction
```sql
SELECT * FROM predict_shipment_delay(p_shipment_id BIGINT);
```

### 3. Revenue Analytics
```sql
SELECT * FROM get_revenue_analytics(
    p_start_date DATE DEFAULT CURRENT_DATE - INTERVAL '90 days',
    p_end_date DATE DEFAULT CURRENT_DATE,
    p_projection_days INTEGER DEFAULT 30
);
```

### 4. Dashboard Data
```sql
SELECT * FROM get_dashboard_data(
    p_user_id BIGINT,
    p_user_role TEXT,
    p_date_range INTEGER DEFAULT 30
);
```

## 🔒 Security Features

### Row-Level Security
```sql
-- Enable RLS on sensitive tables
ALTER TABLE shipments ENABLE ROW LEVEL SECURITY;
ALTER TABLE invoices ENABLE ROW LEVEL SECURITY;

-- Create policies for role-based access
CREATE POLICY customer_shipments ON shipments
    FOR ALL TO application_role
    USING (customer_id = current_setting('app.current_user_id')::BIGINT);

CREATE POLICY driver_shipments ON shipments
    FOR ALL TO application_role
    USING (driver_id = current_setting('app.current_user_id')::BIGINT);
```

### Data Encryption
```sql
-- Encrypt sensitive data
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Example of encrypted column
ALTER TABLE users ADD COLUMN encrypted_phone BYTEA;
UPDATE users SET encrypted_phone = pgp_sym_encrypt(phone, current_setting('app.encryption_key'));
```

## 📈 Monitoring & Maintenance

### Health Check Functions
```sql
SELECT * FROM database_health_check();
```

### Performance Monitoring
```sql
SELECT * FROM get_table_stats('shipments');
```

### Data Validation
```sql
SELECT * FROM validate_shipment_data();
```

### Automated Cleanup
```sql
SELECT cleanup_old_data(90); -- Clean data older than 90 days
```

## 🔄 Migration Strategy

### Version Control
- **Migration Tool**: Flyway
- **Naming Convention**: `V{version}__{description}.sql`
- **Rollback Support**: Each migration has corresponding rollback

### Migration Files
- `V1__Create_initial_tables.sql` - Base schema
- `V2__Add_indexes_and_constraints.sql` - Performance optimization
- `V3__Add_procedures_and_functions.sql` - Business logic

## 📊 Scaling Considerations

### Horizontal Scaling
- **Read Replicas**: For read-heavy operations
- **Partitioning**: For large tables (tracking_events, audit_logs)
- **Connection Pooling**: HikariCP configuration

### Vertical Scaling
- **Memory Allocation**: For large result sets
- **CPU Optimization**: For complex queries
- **Storage**: SSD for better I/O performance

### Caching Strategy
- **Redis Integration**: For frequently accessed data
- **Application-Level Caching**: For computed results
- **Query Result Caching**: For expensive operations

## 🔧 Configuration

### Connection Pool Settings
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### Performance Tuning
```sql
-- PostgreSQL configuration recommendations
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
```

## 📝 Best Practices

### 1. Data Integrity
- Use foreign key constraints
- Implement check constraints
- Use appropriate data types
- Normalize data where appropriate

### 2. Performance
- Create appropriate indexes
- Use EXPLAIN ANALYZE for query optimization
- Monitor slow queries
- Regular maintenance (VACUUM, ANALYZE)

### 3. Security
- Use parameterized queries
- Implement row-level security
- Encrypt sensitive data
- Regular security audits

### 4. Backup & Recovery
- Regular automated backups
- Point-in-time recovery capability
- Test restoration procedures
- Offsite backup storage

## 🚀 Future Enhancements

### 1. Advanced Analytics
- Time-series data storage
- Machine learning integration
- Predictive analytics
- Real-time dashboards

### 2. Multi-tenancy
- Tenant isolation
- Shared database approach
- Resource allocation
- Billing per tenant

### 3. Geographic Features
- PostGIS extension
- Advanced spatial queries
- Route optimization
- Geofencing capabilities

This database design provides a solid foundation for the Edham Logistics backend system, ensuring scalability, performance, and data integrity while supporting all current and future business requirements.
