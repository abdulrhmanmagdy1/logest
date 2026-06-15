-- Edham Logistics Backend - Advanced Procedures and Functions
-- Version 1.2.0

-- Create advanced stored procedures for business operations

-- Procedure for intelligent shipment assignment
CREATE OR REPLACE PROCEDURE assign_shipment_to_driver(
    p_shipment_id BIGINT,
    p_driver_id BIGINT,
    p_vehicle_id BIGINT,
    p_assigned_by BIGINT,
    OUT p_success BOOLEAN,
    OUT p_message TEXT
) AS $$
DECLARE
    v_shipment_status shipment_status;
    v_driver_active BOOLEAN;
    v_vehicle_available BOOLEAN;
    v_existing_assignment BIGINT;
BEGIN
    -- Check if shipment exists and is in correct status
    SELECT status INTO v_shipment_status
    FROM shipments 
    WHERE id = p_shipment_id;
    
    IF v_shipment_status IS NULL THEN
        p_success := FALSE;
        p_message := 'Shipment not found';
        RETURN;
    END IF;
    
    IF v_shipment_status != 'PENDING' THEN
        p_success := FALSE;
        p_message := 'Shipment is not in PENDING status';
        RETURN;
    END IF;
    
    -- Check if driver is active and available
    SELECT active INTO v_driver_active
    FROM users 
    WHERE id = p_driver_id AND role = 'DRIVER';
    
    IF NOT v_driver_active THEN
        p_success := FALSE;
        p_message := 'Driver not found or inactive';
        RETURN;
    END IF;
    
    -- Check if driver is already assigned to active shipment
    SELECT COUNT(*) INTO v_existing_assignment
    FROM shipments 
    WHERE driver_id = p_driver_id 
    AND status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT');
    
    IF v_existing_assignment > 0 THEN
        p_success := FALSE;
        p_message := 'Driver is already assigned to an active shipment';
        RETURN;
    END IF;
    
    -- Check if vehicle is available
    SELECT status INTO v_vehicle_available
    FROM vehicles 
    WHERE id = p_vehicle_id AND driver_id = p_driver_id;
    
    IF v_vehicle_available != 'ACTIVE' THEN
        p_success := FALSE;
        p_message := 'Vehicle not available or not assigned to driver';
        RETURN;
    END IF;
    
    -- Assign shipment
    UPDATE shipments 
    SET 
        driver_id = p_driver_id,
        vehicle_id = p_vehicle_id,
        status = 'ASSIGNED',
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_shipment_id;
    
    -- Create tracking event
    INSERT INTO tracking_events (
        shipment_id, event_type, description, 
        created_by, timestamp
    ) VALUES (
        p_shipment_id, 'ASSIGNED', 
        'Shipment assigned to driver', 
        p_assigned_by, CURRENT_TIMESTAMP
    );
    
    -- Log assignment
    INSERT INTO audit_logs (
        user_id, entity_type, entity_id, action,
        new_values, timestamp
    ) VALUES (
        p_assigned_by, 'SHIPMENT', p_shipment_id, 'ASSIGN_DRIVER',
        JSON_BUILD_OBJECT(
            'driver_id', p_driver_id,
            'vehicle_id', p_vehicle_id,
            'status', 'ASSIGNED'
        ),
        CURRENT_TIMESTAMP
    );
    
    p_success := TRUE;
    p_message := 'Shipment assigned successfully';
    
EXCEPTION
    WHEN OTHERS THEN
        p_success := FALSE;
        p_message := 'Error assigning shipment: ' || SQLERRM;
END;
$$ LANGUAGE plpgsql;

-- Procedure for bulk invoice generation
CREATE OR REPLACE PROCEDURE generate_monthly_invoices(
    p_month INTEGER,
    p_year INTEGER,
    p_generated_by BIGINT,
    OUT p_success BOOLEAN,
    OUT p_message TEXT,
    OUT p_invoice_count INTEGER
) AS $$
DECLARE
    v_start_date DATE;
    v_end_date DATE;
    v_customer_record RECORD;
    v_invoice_number TEXT;
    v_shipment_total DECIMAL(10,2);
    v_tax_amount DECIMAL(10,2);
    v_total_amount DECIMAL(10,2);
BEGIN
    -- Calculate date range
    v_start_date := DATE_MAKE_DATE(p_year, p_month, 1);
    v_end_date := (v_start_date + INTERVAL '1 month') - INTERVAL '1 day';
    
    p_invoice_count := 0;
    
    -- Process each customer
    FOR v_customer_record IN 
        SELECT DISTINCT customer_id, COUNT(*) as shipment_count, SUM(cost) as total_cost
        FROM shipments 
        WHERE DATE(created_at) BETWEEN v_start_date AND v_end_date
        AND status = 'DELIVERED'
        AND cost IS NOT NULL
        GROUP BY customer_id
    LOOP
        -- Calculate amounts
        v_shipment_total := v_customer_record.total_cost;
        v_tax_amount := v_shipment_total * 0.15; -- 15% tax
        v_total_amount := v_shipment_total + v_tax_amount;
        
        -- Generate invoice number
        v_invoice_number := 'INV-' || TO_CHAR(v_start_date, 'YYYYMM') || '-' || 
                          LPAD((SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number FROM '[0-9]+$') AS INTEGER)), 0) + 1)::TEXT, 4, '0');
        
        -- Create invoice
        INSERT INTO invoices (
            invoice_number, customer_id, amount, tax, total_amount,
            due_date, description, status, created_at
        ) VALUES (
            v_invoice_number, v_customer_record.customer_id,
            v_shipment_total, v_tax_amount, v_total_amount,
            v_end_date + INTERVAL '30 days',
            'Monthly invoice for ' || TO_CHAR(v_start_date, 'Month YYYY'),
            'PENDING', CURRENT_TIMESTAMP
        );
        
        -- Link shipments to invoice (using JSON array)
        UPDATE invoices 
        SET shipment_ids = (
            SELECT JSON_AGG(id)
            FROM shipments
            WHERE customer_id = v_customer_record.customer_id
            AND DATE(created_at) BETWEEN v_start_date AND v_end_date
            AND status = 'DELIVERED'
        )
        WHERE invoice_number = v_invoice_number;
        
        p_invoice_count := p_invoice_count + 1;
        
        -- Log invoice creation
        INSERT INTO audit_logs (
            user_id, entity_type, entity_id, action,
            new_values, timestamp
        ) VALUES (
            p_generated_by, 'INVOICE', 
            (SELECT id FROM invoices WHERE invoice_number = v_invoice_number),
            'GENERATE_MONTHLY',
            JSON_BUILD_OBJECT(
                'invoice_number', v_invoice_number,
                'customer_id', v_customer_record.customer_id,
                'total_amount', v_total_amount,
                'shipment_count', v_customer_record.shipment_count
            ),
            CURRENT_TIMESTAMP
        );
    END LOOP;
    
    p_success := TRUE;
    p_message := 'Generated ' || p_invoice_count || ' monthly invoices successfully';
    
EXCEPTION
    WHEN OTHERS THEN
        p_success := FALSE;
        p_message := 'Error generating invoices: ' || SQLERRM;
        p_invoice_count := 0;
END;
$$ LANGUAGE plpgsql;

-- Function for intelligent driver recommendation
CREATE OR REPLACE FUNCTION recommend_driver_for_shipment(
    p_shipment_id BIGINT,
    p_latitude DECIMAL(10,8),
    p_longitude DECIMAL(11,8)
) RETURNS TABLE (
    driver_id BIGINT,
    driver_name TEXT,
    vehicle_info TEXT,
    distance_km DECIMAL(8,2),
    active_shipments INTEGER,
    performance_score DECIMAL(3,2),
    recommendation_score DECIMAL(3,2)
) AS $$
BEGIN
    RETURN QUERY
    WITH driver_metrics AS (
        SELECT 
            d.id as driver_id,
            d.first_name || ' ' || d.last_name as driver_name,
            v.make || ' ' || v.model || ' (' || v.license_plate || ')' as vehicle_info,
            -- Calculate distance from driver's last known location
            CASE 
                WHEN te.latitude IS NOT NULL AND te.longitude IS NOT NULL THEN
                    6371 * ACOS(
                        COS(RADIANS(p_latitude)) * COS(RADIANS(te.latitude)) * 
                        COS(RADIANS(te.longitude) - RADIANS(p_longitude)) + 
                        SIN(RADIANS(p_latitude)) * SIN(RADIANS(te.latitude))
                    )
                ELSE NULL
            END as distance_km,
            -- Count active shipments
            (SELECT COUNT(*) FROM shipments s 
             WHERE s.driver_id = d.id 
             AND s.status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')) as active_shipments,
            -- Performance metrics
            COALESCE(
                (SELECT AVG(
                    CASE 
                        WHEN s.actual_delivery_date <= s.estimated_delivery_date THEN 1.0
                        ELSE 0.5
                    END
                ) FROM shipments s 
                 WHERE s.driver_id = d.id 
                 AND s.status = 'DELIVERED'
                 AND s.created_at > CURRENT_DATE - INTERVAL '90 days'
                ), 0.75
            ) as performance_score
        FROM users d
        JOIN vehicles v ON v.driver_id = d.id
        LEFT JOIN LATERAL (
            SELECT latitude, longitude
            FROM tracking_events te
            WHERE te.shipment_id IN (
                SELECT id FROM shipments WHERE driver_id = d.id
            )
            ORDER BY timestamp DESC
            LIMIT 1
        ) te ON true
        WHERE d.role = 'DRIVER' 
        AND d.active = true 
        AND v.status = 'ACTIVE'
    )
    SELECT 
        dm.driver_id,
        dm.driver_name,
        dm.vehicle_info,
        COALESCE(dm.distance_km, 999) as distance_km,
        dm.active_shipments,
        dm.performance_score,
        -- Calculate recommendation score (0-100)
        CASE 
            WHEN dm.active_shipments = 0 THEN
                (dm.performance_score * 50) + 
                (CASE WHEN dm.distance_km <= 10 THEN 50 
                      WHEN dm.distance_km <= 25 THEN 40 
                      WHEN dm.distance_km <= 50 THEN 30 
                      ELSE 20 END)
            WHEN dm.active_shipments = 1 THEN
                (dm.performance_score * 40) + 
                (CASE WHEN dm.distance_km <= 5 THEN 40 
                      WHEN dm.distance_km <= 15 THEN 30 
                      WHEN dm.distance_km <= 30 THEN 20 
                      ELSE 10 END)
            ELSE
                (dm.performance_score * 30) + 
                (CASE WHEN dm.distance_km <= 2 THEN 30 
                      WHEN dm.distance_km <= 10 THEN 20 
                      ELSE 10 END)
        END as recommendation_score
    FROM driver_metrics dm
    WHERE dm.active_shipments < 3 -- Maximum 3 active shipments
    ORDER BY recommendation_score DESC, distance_km ASC
    LIMIT 10;
END;
$$ LANGUAGE plpgsql;

-- Function for shipment delay prediction
CREATE OR REPLACE FUNCTION predict_shipment_delay(
    p_shipment_id BIGINT
) RETURNS TABLE (
    delay_probability DECIMAL(3,2),
    estimated_delay_hours INTEGER,
    risk_factors TEXT[]
) AS $$
BEGIN
    RETURN QUERY
    WITH shipment_analysis AS (
        SELECT 
            s.id,
            s.created_at,
            s.estimated_delivery_date,
            s.origin_city,
            s.destination_city,
            -- Historical performance on this route
            COALESCE(
                (SELECT AVG(
                    EXTRACT(EPOCH FROM (actual_delivery_date - estimated_delivery_date)) / 3600
                ) FROM shipments h
                 WHERE h.origin_city = s.origin_city 
                 AND h.destination_city = s.destination_city
                 AND h.status = 'DELIVERED'
                 AND h.created_at > CURRENT_DATE - INTERVAL '180 days'
                ), 0
            ) as avg_route_delay_hours,
            -- Current driver performance
            COALESCE(
                (SELECT AVG(
                    CASE 
                        WHEN actual_delivery_date > estimated_delivery_date THEN 1.0
                        ELSE 0.0
                    END
                ) FROM shipments d
                 WHERE d.driver_id = s.driver_id
                 AND d.status = 'DELIVERED'
                 AND d.created_at > CURRENT_DATE - INTERVAL '90 days'
                ), 0.2
            ) as driver_delay_rate,
            -- Weather conditions (placeholder - would integrate with weather API)
            CASE 
                WHEN EXTRACT(MONTH FROM CURRENT_DATE) IN (12, 1, 2) THEN 0.3 -- Winter
                WHEN EXTRACT(MONTH FROM CURRENT_DATE) IN (6, 7, 8) THEN 0.1 -- Summer
                ELSE 0.2 -- Spring/Fall
            END as weather_risk_factor
        FROM shipments s
        WHERE s.id = p_shipment_id
    )
    SELECT 
        -- Calculate delay probability (0-100%)
        LEAST(
            GREATEST(
                (sa.avg_route_delay_hours * 5) + 
                (sa.driver_delay_rate * 40) + 
                (sa.weather_risk_factor * 30) +
                (CASE 
                    WHEN CURRENT_DATE > sa.estimated_delivery_date - INTERVAL '2 days' THEN 20
                    WHEN CURRENT_DATE > sa.estimated_delivery_date - INTERVAL '5 days' THEN 10
                    ELSE 0
                END),
                0
            ),
            100
        ) / 100 as delay_probability,
        -- Estimated delay in hours
        GREATEST(
            ROUND(sa.avg_route_delay_hours * sa.driver_delay_rate * 2),
            0
        ) as estimated_delay_hours,
        -- Risk factors
        ARRAY_REMOVE(ARRAY[
            CASE WHEN sa.avg_route_delay_hours > 12 THEN 'High traffic route' ELSE NULL END,
            CASE WHEN sa.driver_delay_rate > 0.3 THEN 'Driver performance issues' ELSE NULL END,
            CASE WHEN sa.weather_risk_factor > 0.25 THEN 'Weather conditions' ELSE NULL END,
            CASE WHEN CURRENT_DATE > sa.estimated_delivery_date - INTERVAL '2 days' THEN 'Tight deadline' ELSE NULL END
        ], NULL) as risk_factors
    FROM shipment_analysis sa;
END;
$$ LANGUAGE plpgsql;

-- Function for revenue analytics with projections
CREATE OR REPLACE FUNCTION get_revenue_analytics(
    p_start_date DATE DEFAULT CURRENT_DATE - INTERVAL '90 days',
    p_end_date DATE DEFAULT CURRENT_DATE,
    p_projection_days INTEGER DEFAULT 30
) RETURNS TABLE (
    period_date DATE,
    actual_revenue DECIMAL(12,2),
    projected_revenue DECIMAL(12,2),
    shipment_count INTEGER,
    average_shipment_value DECIMAL(10,2),
    growth_rate DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    WITH daily_revenue AS (
        SELECT 
            DATE(created_at) as revenue_date,
            SUM(total_amount) as daily_revenue,
            COUNT(*) as shipment_count,
            AVG(total_amount) as avg_shipment_value
        FROM invoices
        WHERE DATE(created_at) BETWEEN p_start_date AND p_end_date
        AND status = 'PAID'
        GROUP BY DATE(created_at)
    ),
    trend_analysis AS (
        SELECT 
            dr.revenue_date,
            dr.daily_revenue,
            dr.shipment_count,
            dr.avg_shipment_value,
            -- Calculate 7-day moving average
            AVG(dr.daily_revenue) OVER (
                ORDER BY dr.revenue_date 
                ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
            ) as moving_avg_7d,
            -- Calculate growth rate
            (dr.daily_revenue - LAG(dr.daily_revenue, 1) OVER (ORDER BY dr.revenue_date)) / 
            NULLIF(LAG(dr.daily_revenue, 1) OVER (ORDER BY dr.revenue_date), 0) * 100 as growth_rate
        FROM daily_revenue dr
    )
    SELECT 
        ta.revenue_date as period_date,
        ta.daily_revenue as actual_revenue,
        -- Project future revenue based on trend
        CASE 
            WHEN ta.revenue_date > p_end_date THEN
                ta.moving_avg_7d * (1 + (COALESCE(ta.growth_rate, 0) / 100))
            ELSE NULL
        END as projected_revenue,
        ta.shipment_count,
        ta.avg_shipment_value,
        ta.growth_rate
    FROM trend_analysis ta
    WHERE ta.revenue_date BETWEEN p_start_date AND (p_end_date + INTERVAL '1 day' * p_projection_days)
    ORDER BY ta.revenue_date;
END;
$$ LANGUAGE plpgsql;

-- Procedure for automated payment reminders
CREATE OR REPLACE PROCEDURE send_payment_reminders(
    p_days_before_due INTEGER DEFAULT 3,
    p_test_mode BOOLEAN DEFAULT FALSE
) AS $$
DECLARE
    v_overdue_invoice RECORD;
    v_reminder_count INTEGER := 0;
    v_message_template TEXT;
BEGIN
    -- Message template
    v_message_template := 'Dear Customer, this is a reminder that invoice %s is due on %s. ' ||
                        'Total amount due: %s %s. Please make payment to avoid service interruption.';
    
    -- Process overdue and upcoming due invoices
    FOR v_overdue_invoice IN 
        SELECT 
            i.id,
            i.invoice_number,
            i.customer_id,
            i.total_amount,
            i.currency,
            i.due_date,
            u.email,
            u.first_name,
            u.last_name
        FROM invoices i
        JOIN users u ON i.customer_id = u.id
        WHERE i.status = 'PENDING'
        AND (
            i.due_date <= CURRENT_DATE + INTERVAL '1 day' * p_days_before_due
            OR i.due_date < CURRENT_DATE
        )
        AND NOT EXISTS (
            SELECT 1 FROM notifications n 
            WHERE n.user_id = i.customer_id 
            AND n.type = 'PAYMENT_REMINDER'
            AND n.data::json->>'invoice_id' = i.id::TEXT
            AND n.created_at > CURRENT_DATE - INTERVAL '7 days'
        )
    LOOP
        -- Create notification
        INSERT INTO notifications (
            user_id, type, title, message, data, created_at
        ) VALUES (
            v_overdue_invoice.customer_id,
            'PAYMENT_REMINDER',
            'Payment Reminder',
            FORMAT(v_message_template, 
                   v_overdue_invoice.invoice_number,
                   v_overdue_invoice.due_date,
                   v_overdue_invoice.total_amount,
                   v_overdue_invoice.currency
            ),
            JSON_BUILD_OBJECT(
                'invoice_id', v_overdue_invoice.id,
                'invoice_number', v_overdue_invoice.invoice_number,
                'amount', v_overdue_invoice.total_amount,
                'due_date', v_overdue_invoice.due_date,
                'currency', v_overdue_invoice.currency
            ),
            CURRENT_TIMESTAMP
        );
        
        -- Log reminder
        INSERT INTO audit_logs (
            entity_type, entity_id, action, new_values, timestamp
        ) VALUES (
            'INVOICE', v_overdue_invoice.id, 'PAYMENT_REMINDER',
            JSON_BUILD_OBJECT(
                'customer_id', v_overdue_invoice.customer_id,
                'invoice_number', v_overdue_invoice.invoice_number,
                'due_date', v_overdue_invoice.due_date
            ),
            CURRENT_TIMESTAMP
        );
        
        v_reminder_count := v_reminder_count + 1;
        
        -- In test mode, only process first 5 invoices
        IF p_test_mode AND v_reminder_count >= 5 THEN
            EXIT;
        END IF;
    END LOOP;
    
    -- Log the procedure execution
    INSERT INTO audit_logs (
        entity_type, entity_id, action, new_values, timestamp
    ) VALUES (
        'SYSTEM', NULL, 'SEND_PAYMENT_REMINDERS',
        JSON_BUILD_OBJECT(
            'reminders_sent', v_reminder_count,
            'days_before_due', p_days_before_due,
            'test_mode', p_test_mode
        ),
        CURRENT_TIMESTAMP
    );
    
EXCEPTION
    WHEN OTHERS THEN
        -- Log error
        INSERT INTO audit_logs (
            entity_type, entity_id, action, new_values, timestamp
        ) VALUES (
            'SYSTEM', NULL, 'SEND_PAYMENT_REMINDERS_ERROR',
            JSON_BUILD_OBJECT(
                'error_message', SQLERRM,
                'error_detail', SQLSTATE
            ),
            CURRENT_TIMESTAMP
        );
        RAISE;
END;
$$ LANGUAGE plpgsql;

-- Create comprehensive dashboard data function
CREATE OR REPLACE FUNCTION get_dashboard_data(
    p_user_id BIGINT,
    p_user_role TEXT,
    p_date_range INTEGER DEFAULT 30
) RETURNS TABLE (
    metric_name TEXT,
    metric_value NUMERIC,
    metric_trend DECIMAL(5,2),
    last_updated TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    -- Role-specific dashboard metrics
    CASE p_user_role
        WHEN 'CUSTOMER' THEN
            SELECT 
                'Active Shipments'::TEXT, COUNT(*)::NUMERIC, 0, CURRENT_TIMESTAMP
            FROM shipments 
            WHERE customer_id = p_user_id 
            AND status IN ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')
            
            UNION ALL
            
            SELECT 
                'Total Spent'::TEXT, 
                COALESCE(SUM(total_amount), 0)::NUMERIC, 
                0, CURRENT_TIMESTAMP
            FROM invoices 
            WHERE customer_id = p_user_id 
            AND status = 'PAID'
            AND created_at > CURRENT_DATE - INTERVAL '1 day' * p_date_range
            
            UNION ALL
            
            SELECT 
                'Pending Payments'::TEXT, 
                COALESCE(SUM(total_amount), 0)::NUMERIC, 
                0, CURRENT_TIMESTAMP
            FROM invoices 
            WHERE customer_id = p_user_id 
            AND status = 'PENDING'
            
        WHEN 'DRIVER' THEN
            SELECT 
                'Active Shipments'::TEXT, COUNT(*)::NUMERIC, 0, CURRENT_TIMESTAMP
            FROM shipments 
            WHERE driver_id = p_user_id 
            AND status IN ('ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')
            
            UNION ALL
            
            SELECT 
                'Completed Today'::TEXT, COUNT(*)::NUMERIC, 0, CURRENT_TIMESTAMP
            FROM shipments 
            WHERE driver_id = p_user_id 
            AND status = 'DELIVERED'
            AND DATE(actual_delivery_date) = CURRENT_DATE
            
            UNION ALL
            
            SELECT 
                'Total Earnings'::TEXT, 
                COALESCE(SUM(cost), 0)::NUMERIC, 
                0, CURRENT_TIMESTAMP
            FROM shipments 
            WHERE driver_id = p_user_id 
            AND status = 'DELIVERED'
            AND DATE(actual_delivery_date) > CURRENT_DATE - INTERVAL '1 day' * p_date_range
            
        WHEN 'ADMIN' THEN
            SELECT 
                'Total Users'::TEXT, COUNT(*)::NUMERIC, 0, CURRENT_TIMESTAMP
            FROM users WHERE active = true
            
            UNION ALL
            
            SELECT 
                'Active Shipments'::TEXT, COUNT(*)::NUMERIC, 0, CURRENT_TIMESTAMP
            FROM shipments 
            WHERE status IN ('PENDING', 'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT')
            
            UNION ALL
            
            SELECT 
                'Total Revenue'::TEXT, 
                COALESCE(SUM(total_amount), 0)::NUMERIC, 
                0, CURRENT_TIMESTAMP
            FROM invoices 
            WHERE status = 'PAID'
            AND created_at > CURRENT_DATE - INTERVAL '1 day' * p_date_range
            
        ELSE
            SELECT 
                'Dashboard Metrics'::TEXT, 0::NUMERIC, 0, CURRENT_TIMESTAMP
            WHERE false
    END;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions for new procedures and functions
-- GRANT EXECUTE ON PROCEDURE assign_shipment_to_driver(BIGINT, BIGINT, BIGINT, BIGINT, BOOLEAN, TEXT) TO edham_app;
-- GRANT EXECUTE ON PROCEDURE generate_monthly_invoices(INTEGER, INTEGER, BIGINT, BOOLEAN, TEXT, INTEGER) TO edham_app;
-- GRANT EXECUTE ON FUNCTION recommend_driver_for_shipment(BIGINT, DECIMAL, DECIMAL) TO edham_app;
-- GRANT EXECUTE ON FUNCTION predict_shipment_delay(BIGINT) TO edham_app;
-- GRANT EXECUTE ON FUNCTION get_revenue_analytics(DATE, DATE, INTEGER) TO edham_app;
-- GRANT EXECUTE ON PROCEDURE send_payment_reminders(INTEGER, BOOLEAN) TO edham_app;
-- GRANT EXECUTE ON FUNCTION get_dashboard_data(BIGINT, TEXT, INTEGER) TO edham_app;
