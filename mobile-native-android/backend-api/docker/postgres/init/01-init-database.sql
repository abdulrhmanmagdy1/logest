-- Edham Logistics Backend - PostgreSQL Initialization Script
-- This script runs when the PostgreSQL container starts for the first time

-- Create the main database if it doesn't exist
SELECT 'CREATE DATABASE edham_logistics'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'edham_logistics')\gexec

-- Create the application user
DO
$do$
BEGIN
   IF EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'edham_user') THEN

      RAISE NOTICE 'Role "edham_user" already exists. Skipping.';
   ELSE
      CREATE ROLE edham_user LOGIN PASSWORD 'edham_password_123';
      RAISE NOTICE 'Role "edham_user" created.';
   END IF;
END
$do$;

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON DATABASE edham_logistics TO edham_user;

-- Connect to the edham_logistics database
\c edham_logistics;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO edham_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO edham_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO edham_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO edham_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO edham_user;

-- Create extensions needed by the application
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Create custom types
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
      CREATE TYPE user_role AS ENUM ('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ACCOUNTANT', 'WORKSHOP', 'ADMIN', 'SUPER_ADMIN');
   END IF;
   
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'shipment_status') THEN
      CREATE TYPE shipment_status AS ENUM ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'DELAYED');
   END IF;
   
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'invoice_status') THEN
      CREATE TYPE invoice_status AS ENUM ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED');
   END IF;
   
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_status') THEN
      CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED');
   END IF;
   
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tracking_event_type') THEN
      CREATE TYPE tracking_event_type AS ENUM ('CREATED', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'DELAYED', 'CANCELLED', 'LOCATION_UPDATE');
   END IF;
END
$do$;

-- Create functions for JSON handling
CREATE OR REPLACE FUNCTION jsonb_array_to_text_array(jsonb_array JSONB)
RETURNS TEXT[] AS $$
BEGIN
    RETURN ARRAY(
        SELECT jsonb_array_elements(jsonb_array)::TEXT
    );
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Create function for generating tracking numbers
CREATE OR REPLACE FUNCTION generate_tracking_number()
RETURNS TEXT AS $$
DECLARE
    tracking_number TEXT;
    timestamp_part TEXT;
    random_part TEXT;
BEGIN
    timestamp_part := TO_CHAR(NOW(), 'YYYYMMDDHH24MISS');
    random_part := LPAD(FLOOR(RANDOM() * 10000)::TEXT, 4, '0');
    tracking_number := 'EDH' || timestamp_part || random_part;
    
    -- Ensure uniqueness
    WHILE EXISTS (SELECT 1 FROM shipments WHERE tracking_number = tracking_number) LOOP
        random_part := LPAD(FLOOR(RANDOM() * 10000)::TEXT, 4, '0');
        tracking_number := 'EDH' || timestamp_part || random_part;
    END LOOP;
    
    RETURN tracking_number;
END;
$$ LANGUAGE plpgsql;

-- Create function for generating invoice numbers
CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS TEXT AS $$
DECLARE
    invoice_number TEXT;
    year_part TEXT;
    month_part TEXT;
    sequence_part TEXT;
BEGIN
    year_part := TO_CHAR(NOW(), 'YYYY');
    month_part := TO_CHAR(NOW(), 'MM');
    
    -- Get next sequence for this month
    SELECT LPAD(COALESCE(MAX(CAST(SUBSTRING(invoice_number FROM '[0-9]+$') AS INTEGER), 0) + 1, 4, '0')
    INTO sequence_part
    FROM invoices 
    WHERE invoice_number LIKE 'INV-' || year_part || month_part || '%';
    
    invoice_number := 'INV-' || year_part || month_part || '-' || sequence_part;
    
    RETURN invoice_number;
END;
$$ LANGUAGE plpgsql;

-- Create function for updating updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create function for audit logging
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO audit_logs (
            user_id, entity_type, entity_id, action, 
            old_values, new_values, timestamp
        ) VALUES (
            COALESCE(current_setting('app.current_user_id', true)::BIGINT, NULL),
            TG_TABLE_NAME,
            OLD.id,
            TG_OP,
            row_to_json(OLD),
            NULL,
            CURRENT_TIMESTAMP
        );
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_logs (
            user_id, entity_type, entity_id, action,
            old_values, new_values, timestamp
        ) VALUES (
            COALESCE(current_setting('app.current_user_id', true)::BIGINT, NULL),
            TG_TABLE_NAME,
            NEW.id,
            TG_OP,
            row_to_json(OLD),
            row_to_json(NEW),
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO audit_logs (
            user_id, entity_type, entity_id, action,
            old_values, new_values, timestamp
        ) VALUES (
            COALESCE(current_setting('app.current_user_id', true)::BIGINT, NULL),
            TG_TABLE_NAME,
            NEW.id,
            TG_OP,
            NULL,
            row_to_json(NEW),
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create function for calculating distance between two points (Haversine formula)
CREATE OR REPLACE FUNCTION calculate_distance(
    lat1 DECIMAL, lon1 DECIMAL, 
    lat2 DECIMAL, lon2 DECIMAL
) RETURNS DECIMAL AS $$
DECLARE
    R DECIMAL := 6371; -- Earth's radius in kilometers
    dLat DECIMAL;
    dLon DECIMAL;
    a DECIMAL;
    c DECIMAL;
BEGIN
    dLat := RADIANS(lat2 - lat1);
    dLon := RADIANS(lon2 - lon1);
    
    a := SIN(dLat/2) * SIN(dLat/2) + 
         COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * 
         SIN(dLon/2) * SIN(dLon/2);
    
    c := 2 * ATAN2(SQRT(a), SQRT(1-a));
    
    RETURN R * c; -- Distance in kilometers
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Create function for getting shipment statistics
CREATE OR REPLACE FUNCTION get_shipment_statistics(
    start_date DATE DEFAULT CURRENT_DATE - INTERVAL '30 days',
    end_date DATE DEFAULT CURRENT_DATE
) RETURNS TABLE (
    status TEXT,
    count BIGINT,
    percentage DECIMAL(5,2),
    avg_cost DECIMAL(10,2),
    total_cost DECIMAL(12,2)
) AS $$
BEGIN
    RETURN QUERY
    WITH total_shipments AS (
        SELECT COUNT(*)::DECIMAL FROM shipments 
        WHERE DATE(created_at) BETWEEN start_date AND end_date
    )
    SELECT 
        s.status::TEXT,
        COUNT(*)::BIGINT,
        ROUND((COUNT(*) * 100.0 / NULLIF(ts.total_count, 0)), 2) as percentage,
        ROUND(AVG(s.cost), 2) as avg_cost,
        ROUND(SUM(s.cost), 2) as total_cost
    FROM shipments s, total_shipments ts
    WHERE DATE(s.created_at) BETWEEN start_date AND end_date
    GROUP BY s.status, ts.total_count
    ORDER BY count DESC;
END;
$$ LANGUAGE plpgsql;

-- Create function for getting revenue analytics
CREATE OR REPLACE FUNCTION get_revenue_analytics(
    start_date DATE DEFAULT CURRENT_DATE - INTERVAL '30 days',
    end_date DATE DEFAULT CURRENT_DATE
) RETURNS TABLE (
    period DATE,
    revenue DECIMAL(12,2),
    invoice_count BIGINT,
    avg_invoice_amount DECIMAL(10,2),
    growth_rate DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    WITH daily_revenue AS (
        SELECT 
            DATE(created_at) as period,
            SUM(total_amount) as revenue,
            COUNT(*) as invoice_count,
            AVG(total_amount) as avg_invoice_amount
        FROM invoices
        WHERE status = 'PAID'
        AND DATE(created_at) BETWEEN start_date AND end_date
        GROUP BY DATE(created_at)
    ),
    revenue_with_lag AS (
        SELECT 
            period,
            revenue,
            invoice_count,
            avg_invoice_amount,
            LAG(revenue) OVER (ORDER BY period) as prev_revenue
        FROM daily_revenue
    )
    SELECT 
        period,
        revenue,
        invoice_count,
        avg_invoice_amount,
        ROUND(
            CASE 
                WHEN prev_revenue IS NULL OR prev_revenue = 0 THEN 0
                ELSE ((revenue - prev_revenue) / prev_revenue) * 100
            END, 2
        ) as growth_rate
    FROM revenue_with_lag
    ORDER BY period;
END;
$$ LANGUAGE plpgsql;

-- Create function for driver performance metrics
CREATE OR REPLACE FUNCTION get_driver_performance(
    driver_id BIGINT,
    start_date DATE DEFAULT CURRENT_DATE - INTERVAL '30 days',
    end_date DATE DEFAULT CURRENT_DATE
) RETURNS TABLE (
    metric_name TEXT,
    metric_value DECIMAL,
    metric_unit TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Total deliveries
    SELECT 
        'Total Deliveries'::TEXT,
        COUNT(*)::DECIMAL,
        'count'::TEXT
    FROM shipments
    WHERE driver_id = driver_id
    AND status = 'DELIVERED'
    AND DATE(actual_delivery_date) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- On-time delivery rate
    SELECT 
        'On-Time Delivery Rate'::TEXT,
        ROUND(
            COUNT(CASE WHEN actual_delivery_date <= estimated_delivery_date THEN 1 END) * 100.0 / 
            NULLIF(COUNT(*), 0), 2
        ),
        'percentage'::TEXT
    FROM shipments
    WHERE driver_id = driver_id
    AND status = 'DELIVERED'
    AND DATE(actual_delivery_date) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Average delivery time
    SELECT 
        'Average Delivery Time'::TEXT,
        ROUND(AVG(
            EXTRACT(EPOCH FROM (actual_delivery_date - pickup_date)) / 3600
        ), 2),
        'hours'::TEXT
    FROM shipments
    WHERE driver_id = driver_id
    AND status = 'DELIVERED'
    AND pickup_date IS NOT NULL
    AND actual_delivery_date IS NOT NULL
    AND DATE(actual_delivery_date) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Total earnings
    SELECT 
        'Total Earnings'::TEXT,
        ROUND(SUM(cost), 2),
        'currency'::TEXT
    FROM shipments
    WHERE driver_id = driver_id
    AND status = 'DELIVERED'
    AND DATE(actual_delivery_date) BETWEEN start_date AND end_date;
END;
$$ LANGUAGE plpgsql;

-- Create function for customer analytics
CREATE OR REPLACE FUNCTION get_customer_analytics(
    customer_id BIGINT,
    start_date DATE DEFAULT CURRENT_DATE - INTERVAL '30 days',
    end_date DATE DEFAULT CURRENT_DATE
) RETURNS TABLE (
    metric_name TEXT,
    metric_value DECIMAL,
    metric_unit TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Total shipments
    SELECT 
        'Total Shipments'::TEXT,
        COUNT(*)::DECIMAL,
        'count'::TEXT
    FROM shipments
    WHERE customer_id = customer_id
    AND DATE(created_at) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Delivered shipments
    SELECT 
        'Delivered Shipments'::TEXT,
        COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END)::DECIMAL,
        'count'::TEXT
    FROM shipments
    WHERE customer_id = customer_id
    AND DATE(created_at) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Success rate
    SELECT 
        'Success Rate'::TEXT,
        ROUND(
            COUNT(CASE WHEN status = 'DELIVERED' THEN 1 END) * 100.0 / 
            NULLIF(COUNT(*), 0), 2
        ),
        'percentage'::TEXT
    FROM shipments
    WHERE customer_id = customer_id
    AND DATE(created_at) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Total spending
    SELECT 
        'Total Spending'::TEXT,
        ROUND(SUM(cost), 2),
        'currency'::TEXT
    FROM shipments
    WHERE customer_id = customer_id
    AND status = 'DELIVERED'
    AND DATE(actual_delivery_date) BETWEEN start_date AND end_date
    
    UNION ALL
    
    -- Average shipment cost
    SELECT 
        'Average Shipment Cost'::TEXT,
        ROUND(AVG(cost), 2),
        'currency'::TEXT
    FROM shipments
    WHERE customer_id = customer_id
    AND DATE(created_at) BETWEEN start_date AND end_date;
END;
$$ LANGUAGE plpgsql;

-- Output initialization message
RAISE NOTICE 'Edham Logistics database initialized successfully';
RAISE NOTICE 'Database: edham_logistics';
RAISE NOTICE 'User: edham_user';
RAISE NOTICE 'Extensions: uuid-ossp, postgis, pg_stat_statements, pg_trgm';
RAISE NOTICE 'Custom functions and triggers created';
