-- Seed Real Operational Data for Edham Logistics
-- Role-specific users with password: password123

-- 1. Insert Real Customer
INSERT INTO users (email, password, first_name, last_name, phone, role, active, email_verified)
VALUES ('customer@edham.co', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'خالد', 'الفصيل', '+966500000001', 'CUSTOMER', true, true);

-- 2. Insert Real Driver
INSERT INTO users (email, password, first_name, last_name, phone, role, active, email_verified)
VALUES ('driver@edham.co', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'أحمد', 'منصور', '+966500000002', 'DRIVER', true, true);

-- 3. Insert Real Supervisor
INSERT INTO users (email, password, first_name, last_name, phone, role, active, email_verified)
VALUES ('supervisor@edham.co', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'سارة', 'أحمد', '+966500000003', 'SUPERVISOR', true, true);

-- 4. Insert Real Accountant
INSERT INTO users (email, password, first_name, last_name, phone, role, active, email_verified)
VALUES ('accountant@edham.co', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'عبدالرحمن', 'المالكي', '+966500000004', 'ACCOUNTANT', true, true);

-- 5. Insert Real Vehicle (Assigned to Driver Ahmed)
INSERT INTO vehicles (license_plate, make, model, year, type, capacity, status, driver_id, organization_id)
SELECT 'أ ب ج 1234', 'Mercedes', 'Actros', 2024, 'Refrigerated', 25.0, 'ACTIVE', id, 1
FROM users WHERE email = 'driver@edham.co';

-- 6. Insert an initial pending shipment for the customer
INSERT INTO shipments (tracking_number, customer_id, origin_address, destination_address, status, weight, cost, created_at)
SELECT 'EDH-INIT-001', id, 'الرياض، حي الملز', 'جدة، الميناء الإسلامي', 'PENDING', 5.5, 1250.0, CURRENT_TIMESTAMP
FROM users WHERE email = 'customer@edham.co';
