-- Edham Logistics Backend - Initial Database Schema
-- Version 1.0.0

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create custom types
CREATE TYPE user_role AS ENUM ('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ACCOUNTANT', 'WORKSHOP', 'ADMIN', 'SUPER_ADMIN');
CREATE TYPE shipment_status AS ENUM ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'DELAYED');
CREATE TYPE invoice_status AS ENUM ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED');
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED');
CREATE TYPE tracking_event_type AS ENUM ('CREATED', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'DELAYED', 'CANCELLED', 'LOCATION_UPDATE');

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role user_role NOT NULL DEFAULT 'CUSTOMER',
    organization_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    phone_verified BOOLEAN NOT NULL DEFAULT false,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Organizations table
CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tax_number VARCHAR(50),
    commercial_registration VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    capacity DECIMAL(10,2),
    fuel_type VARCHAR(50),
    registration_number VARCHAR(50),
    insurance_number VARCHAR(50),
    insurance_expiry DATE,
    maintenance_due DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    driver_id BIGINT,
    organization_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (driver_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

-- Shipments table
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    driver_id BIGINT,
    vehicle_id BIGINT,
    origin_address TEXT NOT NULL,
    origin_city VARCHAR(100),
    origin_state VARCHAR(100),
    origin_country VARCHAR(100),
    origin_postal_code VARCHAR(20),
    origin_latitude DECIMAL(10,8),
    origin_longitude DECIMAL(11,8),
    destination_address TEXT NOT NULL,
    destination_city VARCHAR(100),
    destination_state VARCHAR(100),
    destination_country VARCHAR(100),
    destination_postal_code VARCHAR(20),
    destination_latitude DECIMAL(10,8),
    destination_longitude DECIMAL(11,8),
    current_location TEXT,
    current_latitude DECIMAL(10,8),
    current_longitude DECIMAL(11,8),
    status shipment_status NOT NULL DEFAULT 'PENDING',
    weight DECIMAL(10,2),
    dimensions TEXT, -- JSON format: {length, width, height}
    special_instructions TEXT,
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,
    pickup_date TIMESTAMP,
    delivery_time TIMESTAMP,
    cost DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'USD',
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (driver_id) REFERENCES users(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Tracking Events table
CREATE TABLE tracking_events (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    event_type tracking_event_type NOT NULL,
    description TEXT,
    location TEXT,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Invoices table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    shipment_ids TEXT, -- JSON array of shipment IDs
    amount DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    due_date DATE NOT NULL,
    description TEXT,
    status invoice_status NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100),
    status payment_status NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP,
    gateway_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- User Sessions table
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL,
    refresh_token VARCHAR(255) UNIQUE,
    device_info TEXT, -- JSON format
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Audit Log table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(50) NOT NULL,
    old_values TEXT, -- JSON format
    new_values TEXT, -- JSON format
    ip_address INET,
    user_agent TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data TEXT, -- JSON format for additional data
    read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User Preferences table
CREATE TABLE user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preference_key VARCHAR(100) NOT NULL,
    preference_value TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(user_id, preference_key)
);

-- Backup Logs table
CREATE TABLE backup_logs (
    id BIGSERIAL PRIMARY KEY,
    backup_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    error_message TEXT,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create indexes for performance optimization
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_organization_id ON users(organization_id);

CREATE INDEX idx_shipments_customer_id ON shipments(customer_id);
CREATE INDEX idx_shipments_driver_id ON shipments(driver_id);
CREATE INDEX idx_shipments_vehicle_id ON shipments(vehicle_id);
CREATE INDEX idx_shipments_status ON shipments(status);
CREATE INDEX idx_shipments_tracking_number ON shipments(tracking_number);
CREATE INDEX idx_shipments_created_at ON shipments(created_at);

CREATE INDEX idx_tracking_events_shipment_id ON tracking_events(shipment_id);
CREATE INDEX idx_tracking_events_timestamp ON tracking_events(timestamp);
CREATE INDEX idx_tracking_events_event_type ON tracking_events(event_type);

CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);
CREATE INDEX idx_invoices_created_at ON invoices(created_at);

CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);

CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_session_token ON user_sessions(session_token);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

CREATE INDEX idx_user_preferences_user_id ON user_preferences(user_id);

CREATE INDEX idx_backup_logs_backup_type ON backup_logs(backup_type);
CREATE INDEX idx_backup_logs_started_at ON backup_logs(started_at);

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_organizations_updated_at BEFORE UPDATE ON organizations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vehicles_updated_at BEFORE UPDATE ON vehicles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_shipments_updated_at BEFORE UPDATE ON shipments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_sessions_updated_at BEFORE UPDATE ON user_sessions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_preferences_updated_at BEFORE UPDATE ON user_preferences
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, role, active, email_verified) 
VALUES ('admin@edham-logistics.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'System', 'Administrator', 'SUPER_ADMIN', true, true);

-- Insert default organization
INSERT INTO organizations (name, description, email, phone, address, city, country) 
VALUES ('Edham Logistics', 'Main logistics organization', 'info@edham-logistics.com', '+966501234567', 'Riyadh, Saudi Arabia', 'Riyadh', 'Saudi Arabia');

-- Create views for common queries
CREATE VIEW shipment_details AS
SELECT 
    s.id,
    s.tracking_number,
    s.status,
    s.origin_address,
    s.destination_address,
    s.current_location,
    s.estimated_delivery_date,
    s.actual_delivery_date,
    s.cost,
    s.created_at,
    c.first_name || ' ' || c.last_name as customer_name,
    c.email as customer_email,
    d.first_name || ' ' || d.last_name as driver_name,
    d.email as driver_email,
    v.license_plate,
    v.make || ' ' || v.model as vehicle_info
FROM shipments s
LEFT JOIN users c ON s.customer_id = c.id
LEFT JOIN users d ON s.driver_id = d.id
LEFT JOIN vehicles v ON s.vehicle_id = v.id;

CREATE VIEW invoice_summary AS
SELECT 
    i.id,
    i.invoice_number,
    i.total_amount,
    i.currency,
    i.due_date,
    i.status,
    i.created_at,
    c.first_name || ' ' || c.last_name as customer_name,
    c.email as customer_email,
    COUNT(p.id) as payment_count,
    COALESCE(SUM(p.amount), 0) as paid_amount
FROM invoices i
LEFT JOIN users c ON i.customer_id = c.id
LEFT JOIN payments p ON i.id = p.invoice_id AND p.status = 'COMPLETED'
GROUP BY i.id, c.first_name, c.last_name, c.email;

-- Create materialized views for reporting (refresh periodically)
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

-- Create functions for common operations
CREATE OR REPLACE FUNCTION get_shipment_tracking(p_shipment_id BIGINT)
RETURNS TABLE (
    event_type VARCHAR(50),
    description TEXT,
    location TEXT,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    timestamp TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        te.event_type::VARCHAR(50),
        te.description,
        te.location,
        te.latitude,
        te.longitude,
        te.timestamp
    FROM tracking_events te
    WHERE te.shipment_id = p_shipment_id
    ORDER BY te.timestamp DESC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_user_shipments(p_user_id BIGINT, p_status VARCHAR(50) DEFAULT NULL)
RETURNS TABLE (
    id BIGINT,
    tracking_number VARCHAR(50),
    status VARCHAR(50),
    origin_address TEXT,
    destination_address TEXT,
    current_location TEXT,
    cost DECIMAL(10,2),
    created_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.id,
        s.tracking_number,
        s.status::VARCHAR(50),
        s.origin_address,
        s.destination_address,
        s.current_location,
        s.cost,
        s.created_at
    FROM shipments s
    WHERE s.customer_id = p_user_id
    AND (p_status IS NULL OR s.status = p_status::shipment_status)
    ORDER BY s.created_at DESC;
END;
$$ LANGUAGE plpgsql;

-- Create stored procedures for data cleanup
CREATE OR REPLACE FUNCTION cleanup_expired_sessions()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM user_sessions 
    WHERE expires_at < CURRENT_TIMESTAMP OR is_active = false;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION cleanup_old_notifications(p_days_old INTEGER DEFAULT 30)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM notifications 
    WHERE read = true 
    AND read_at < CURRENT_TIMESTAMP - INTERVAL '1 day' * p_days_old;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions (adjust based on your security requirements)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO edham_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO edham_app;
