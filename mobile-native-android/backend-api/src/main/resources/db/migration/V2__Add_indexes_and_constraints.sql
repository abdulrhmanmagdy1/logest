-- Edham Logistics Backend - Additional Indexes and Constraints
-- Version 1.1.0

-- Add composite indexes for better query performance
CREATE INDEX CONCURRENTLY idx_shipments_customer_status ON shipments(customer_id, status);
CREATE INDEX CONCURRENTLY idx_shipments_driver_status ON shipments(driver_id, status) WHERE driver_id IS NOT NULL;
CREATE INDEX CONCURRENTLY idx_shipments_created_status ON shipments(created_at DESC, status);
CREATE INDEX CONCURRENTLY idx_shipments_location_coords ON shipments USING GIST(ST_Point(current_longitude, current_latitude)) WHERE current_latitude IS NOT NULL AND current_longitude IS NOT NULL;

CREATE INDEX CONCURRENTLY idx_tracking_events_shipment_timestamp ON tracking_events(shipment_id, timestamp DESC);
CREATE INDEX CONCURRENTLY idx_tracking_events_location_coords ON tracking_events USING GIST(ST_Point(longitude, latitude)) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

CREATE INDEX CONCURRENTLY idx_invoices_customer_status ON invoices(customer_id, status);
CREATE INDEX CONCURRENTLY idx_invoices_due_status ON invoices(due_date, status);
CREATE INDEX CONCURRENTLY idx_invoices_created_status ON invoices(created_at DESC, status);

CREATE INDEX CONCURRENTLY idx_payments_invoice_status ON payments(invoice_id, status);
CREATE INDEX CONCURRENTLY idx_payments_date_status ON payments(payment_date DESC, status) WHERE payment_date IS NOT NULL;

CREATE INDEX CONCURRENTLY idx_user_sessions_user_active ON user_sessions(user_id, is_active) WHERE is_active = true;
CREATE INDEX CONCURRENTLY idx_user_sessions_expires_active ON user_sessions(expires_at, is_active) WHERE is_active = true;

CREATE INDEX CONCURRENTLY idx_audit_logs_user_timestamp ON audit_logs(user_id, timestamp DESC) WHERE user_id IS NOT NULL;
CREATE INDEX CONCURRENTLY idx_audit_logs_entity_timestamp ON audit_logs(entity_type, entity_id, timestamp DESC);

CREATE INDEX CONCURRENTLY idx_notifications_user_read ON notifications(user_id, read) WHERE read = false;
CREATE INDEX CONCURRENTLY idx_notifications_user_created ON notifications(user_id, created_at DESC);

-- Add unique constraints for data integrity
ALTER TABLE shipments ADD CONSTRAINT uk_shipments_tracking_number UNIQUE (tracking_number);
ALTER TABLE invoices ADD CONSTRAINT uk_invoices_invoice_number UNIQUE (invoice_number);
ALTER TABLE vehicles ADD CONSTRAINT uk_vehicles_license_plate UNIQUE (license_plate);

-- Add check constraints
ALTER TABLE shipments ADD CONSTRAINT chk_shipments_cost CHECK (cost >= 0);
ALTER TABLE shipments ADD CONSTRAINT chk_shipments_weight CHECK (weight >= 0);
ALTER TABLE invoices ADD CONSTRAINT chk_invoices_amount CHECK (amount >= 0);
ALTER TABLE invoices ADD CONSTRAINT chk_invoices_tax CHECK (tax >= 0);
ALTER TABLE invoices ADD CONSTRAINT chk_invoices_total_amount CHECK (total_amount >= 0);
ALTER TABLE payments ADD CONSTRAINT chk_payments_amount CHECK (amount >= 0);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_year CHECK (year >= 1900 AND year <= EXTRACT(YEAR FROM CURRENT_DATE) + 1);
ALTER TABLE vehicles ADD CONSTRAINT chk_vehicles_capacity CHECK (capacity >= 0);

-- Add foreign key constraints with proper actions
ALTER TABLE shipments ADD CONSTRAINT fk_shipments_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE shipments ADD CONSTRAINT fk_shipments_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE shipments ADD CONSTRAINT fk_shipments_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE SET NULL;

ALTER TABLE tracking_events ADD CONSTRAINT fk_tracking_events_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE;
ALTER TABLE tracking_events ADD CONSTRAINT fk_tracking_events_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE invoices ADD CONSTRAINT fk_invoices_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE payments ADD CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE;

ALTER TABLE vehicles ADD CONSTRAINT fk_vehicles_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE vehicles ADD CONSTRAINT fk_vehicles_organization FOREIGN KEY (organization_id) REFERENCES organizations(id) ON DELETE SET NULL;

ALTER TABLE user_sessions ADD CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notifications ADD CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_preferences ADD CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE backup_logs ADD CONSTRAINT fk_backup_logs_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL;

-- Add partial indexes for common filtered queries
CREATE INDEX CONCURRENTLY idx_shipments_pending ON shipments(created_at DESC) WHERE status = 'PENDING';
CREATE INDEX CONCURRENTLY idx_shipments_in_transit ON shipments(created_at DESC) WHERE status = 'IN_TRANSIT';
CREATE INDEX CONCURRENTLY idx_shipments_delivered ON shipments(created_at DESC) WHERE status = 'DELIVERED';

CREATE INDEX CONCURRENTLY idx_invoices_pending ON invoices(created_at DESC) WHERE status = 'PENDING';
CREATE INDEX CONCURRENTLY idx_invoices_overdue ON invoices(due_date) WHERE status = 'PENDING' AND due_date < CURRENT_DATE;

CREATE INDEX CONCURRENTLY idx_notifications_unread ON notifications(created_at DESC) WHERE read = false;

-- Add text search indexes for full-text search
CREATE INDEX CONCURRENTLY idx_shipments_search ON shipments USING GIN(
    to_tsvector('english', tracking_number || ' ' || COALESCE(origin_address, '') || ' ' || COALESCE(destination_address, '') || ' ' || COALESCE(notes, ''))
);

CREATE INDEX CONCURRENTLY idx_users_search ON users USING GIN(
    to_tsvector('english', first_name || ' ' || last_name || ' ' || COALESCE(email, '') || ' ' || COALESCE(phone, ''))
);

CREATE INDEX CONCURRENTLY idx_vehicles_search ON vehicles USING GIN(
    to_tsvector('english', license_plate || ' ' || make || ' ' || model || ' ' || COALESCE(type, ''))
);

CREATE INDEX CONCURRENTLY idx_invoices_search ON invoices USING GIN(
    to_tsvector('english', invoice_number || ' ' || COALESCE(description, '') || ' ' || COALESCE(notes, ''))
);

-- Create partitioned table for large datasets (optional, for future scaling)
-- This is commented out as it requires PostgreSQL 10+ and careful planning
/*
-- Create partitioned table for tracking_events (by month)
CREATE TABLE tracking_events_partitioned (
    LIKE tracking_events INCLUDING ALL
) PARTITION BY RANGE (timestamp);

-- Create monthly partitions for current year
CREATE TABLE tracking_events_2024_01 PARTITION OF tracking_events_partitioned
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE tracking_events_2024_02 PARTITION OF tracking_events_partitioned
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

-- Add more partitions as needed
*/

-- Create materialized view refresh function
CREATE OR REPLACE FUNCTION refresh_materialized_views()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY shipment_statistics;
    REFRESH MATERIALIZED VIEW CONCURRENTLY revenue_statistics;
END;
$$ LANGUAGE plpgsql;

-- Create trigger function for automatic materialized view refresh
CREATE OR REPLACE FUNCTION trigger_refresh_materialized_views()
RETURNS trigger AS $$
BEGIN
    -- Refresh materialized views every hour
    -- This would typically be called by a scheduled job
    PERFORM refresh_materialized_views();
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Add performance monitoring functions
CREATE OR REPLACE FUNCTION get_table_stats(p_table_name TEXT)
RETURNS TABLE (
    table_name TEXT,
    row_count BIGINT,
    total_size TEXT,
    index_size TEXT,
    last_analyze TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        schemaname || '.' || tablename as table_name,
        n_tup_ins - n_tup_del as row_count,
        pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
        pg_size_pretty(pg_indexes_size(schemaname||'.'||tablename)) as index_size,
        last_analyze
    FROM pg_stat_user_tables 
    WHERE tablename = p_table_name;
END;
$$ LANGUAGE plpgsql;

-- Add data validation functions
CREATE OR REPLACE FUNCTION validate_shipment_data()
RETURNS TABLE (
    shipment_id BIGINT,
    validation_errors TEXT[]
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        s.id,
        ARRAY_AGG(
            CASE 
                WHEN s.tracking_number IS NULL OR s.tracking_number = '' THEN 'Missing tracking number'
                WHEN s.customer_id IS NULL THEN 'Missing customer'
                WHEN s.origin_address IS NULL OR s.origin_address = '' THEN 'Missing origin address'
                WHEN s.destination_address IS NULL OR s.destination_address = '' THEN 'Missing destination address'
                WHEN s.cost < 0 THEN 'Invalid cost'
                WHEN s.weight < 0 THEN 'Invalid weight'
                ELSE NULL
            END
        ) FILTER (WHERE CASE 
            WHEN s.tracking_number IS NULL OR s.tracking_number = '' THEN 'Missing tracking number'
            WHEN s.customer_id IS NULL THEN 'Missing customer'
            WHEN s.origin_address IS NULL OR s.origin_address = '' THEN 'Missing origin address'
            WHEN s.destination_address IS NULL OR s.destination_address = '' THEN 'Missing destination address'
            WHEN s.cost < 0 THEN 'Invalid cost'
            WHEN s.weight < 0 THEN 'Invalid weight'
            ELSE NULL
        END IS NOT NULL)
    FROM shipments s
    WHERE s.created_at > CURRENT_DATE - INTERVAL '7 days'
    GROUP BY s.id;
END;
$$ LANGUAGE plpgsql;

-- Add cleanup functions for maintenance
CREATE OR REPLACE FUNCTION cleanup_old_data(p_days_old INTEGER DEFAULT 90)
RETURNS INTEGER AS $$
DECLARE
    deleted_sessions INTEGER;
    deleted_notifications INTEGER;
    deleted_audit_logs INTEGER;
BEGIN
    -- Clean up old sessions
    DELETE FROM user_sessions 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 day' * p_days_old;
    GET DIAGNOSTICS deleted_sessions = ROW_COUNT;
    
    -- Clean up old read notifications
    DELETE FROM notifications 
    WHERE read = true 
    AND read_at < CURRENT_TIMESTAMP - INTERVAL '1 day' * p_days_old;
    GET DIAGNOSTICS deleted_notifications = ROW_COUNT;
    
    -- Clean up old audit logs (keep only critical ones)
    DELETE FROM audit_logs 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 day' * p_days_old
    AND action NOT IN ('LOGIN', 'LOGOUT', 'CREATE', 'DELETE');
    GET DIAGNOSTICS deleted_audit_logs = ROW_COUNT;
    
    RETURN deleted_sessions + deleted_notifications + deleted_audit_logs;
END;
$$ LANGUAGE plpgsql;

-- Create function for database health check
CREATE OR REPLACE FUNCTION database_health_check()
RETURNS TABLE (
    check_name TEXT,
    status TEXT,
    details TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 'Connection Count'::TEXT, 
           CASE WHEN COUNT(*) < 100 THEN 'OK' ELSE 'WARNING' END,
           'Active connections: ' || COUNT(*)::TEXT
    FROM pg_stat_activity WHERE state = 'active'
    
    UNION ALL
    
    SELECT 'Table Sizes'::TEXT,
           CASE 
               WHEN pg_total_relation_size('shipments') < 1000000000 THEN 'OK' 
               ELSE 'WARNING' 
           END,
           'Shipments table size: ' || pg_size_pretty(pg_total_relation_size('shipments'))
    
    UNION ALL
    
    SELECT 'Index Usage'::TEXT,
           CASE 
               WHEN AVG(idx_scan) > 10 THEN 'OK' 
               ELSE 'WARNING' 
           END,
           'Average index scans: ' || ROUND(AVG(idx_scan), 2)::TEXT
    FROM pg_stat_user_indexes WHERE schemaname = 'public';
END;
$$ LANGUAGE plpgsql;

-- Add function to generate daily statistics
CREATE OR REPLACE FUNCTION generate_daily_statistics(p_date DATE DEFAULT CURRENT_DATE)
RETURNS void AS $$
BEGIN
    -- Insert daily shipment statistics
    INSERT INTO shipment_statistics (date, status, count, avg_cost, total_cost)
    SELECT 
        p_date,
        status,
        COUNT(*),
        AVG(cost),
        SUM(cost)
    FROM shipments 
    WHERE DATE(created_at) = p_date
    GROUP BY status
    ON CONFLICT (date, status) 
    DO UPDATE SET 
        count = EXCLUDED.count,
        avg_cost = EXCLUDED.avg_cost,
        total_cost = EXCLUDED.total_cost;
    
    -- Insert daily revenue statistics
    INSERT INTO revenue_statistics (date, status, invoice_count, total_revenue, avg_invoice_amount)
    SELECT 
        p_date,
        status,
        COUNT(*),
        SUM(total_amount),
        AVG(total_amount)
    FROM invoices 
    WHERE DATE(created_at) = p_date
    GROUP BY status
    ON CONFLICT (date, status) 
    DO UPDATE SET 
        invoice_count = EXCLUDED.invoice_count,
        total_revenue = EXCLUDED.total_revenue,
        avg_invoice_amount = EXCLUDED.avg_invoice_amount;
END;
$$ LANGUAGE plpgsql;

-- Grant necessary permissions for maintenance functions
-- GRANT EXECUTE ON FUNCTION refresh_materialized_views() TO edham_app;
-- GRANT EXECUTE ON FUNCTION get_table_stats(TEXT) TO edham_app;
-- GRANT EXECUTE ON FUNCTION validate_shipment_data() TO edham_app;
-- GRANT EXECUTE ON FUNCTION cleanup_old_data(INTEGER) TO edham_app;
-- GRANT EXECUTE ON FUNCTION database_health_check() TO edham_app;
-- GRANT EXECUTE ON FUNCTION generate_daily_statistics(DATE) TO edham_app;

-- Create scheduled job for daily statistics (requires pg_cron extension)
-- SELECT cron.schedule('daily-statistics', '0 1 * * *', 'SELECT generate_daily_statistics();');

-- Create scheduled job for materialized view refresh
-- SELECT cron.schedule('refresh-views', '0 */6 * * *', 'SELECT refresh_materialized_views();');

-- Create scheduled job for data cleanup
-- SELECT cron.schedule('cleanup-old-data', '0 2 * * 0', 'SELECT cleanup_old_data(90);');
