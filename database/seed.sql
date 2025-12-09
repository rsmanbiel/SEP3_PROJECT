-- SEP3 Warehouse Management System - Seed Data
-- PostgreSQL 15+

-- =====================================================
-- ROLES
-- =====================================================
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Full system access, user management, system configuration'),
('SUPERVISOR', 'Inventory oversight, reports generation, order approval'),
('WAREHOUSE_OPERATOR', 'Inventory CRUD operations, order processing'),
('CUSTOMER', 'View products, place orders, track shipments');

-- =====================================================
-- USERS (All passwords: 'password123' - hashed with BCrypt strength 10)
-- BCrypt hash verified for 'password123': $2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW
-- =====================================================
INSERT INTO users (username, email, password_hash, first_name, last_name, phone, address, city, postal_code, country, role_id) VALUES
-- Admin
('admin', 'admin@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'System', 'Administrator', '+45 12345678', 'Admin Street 1', 'Horsens', '8700', 'Denmark', 1),

-- Supervisors
('supervisor1', 'supervisor1@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Lars', 'Nielsen', '+45 23456789', 'Supervisor Vej 10', 'Horsens', '8700', 'Denmark', 2),
('supervisor2', 'supervisor2@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Maria', 'Hansen', '+45 34567890', 'Manager Alle 5', 'Aarhus', '8000', 'Denmark', 2),

-- Warehouse Operators
('operator1', 'operator1@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Peter', 'Jensen', '+45 45678901', 'Worker Gade 15', 'Horsens', '8700', 'Denmark', 3),
('operator2', 'operator2@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Anna', 'Pedersen', '+45 56789012', 'Lager Vej 20', 'Horsens', '8700', 'Denmark', 3),
('operator3', 'operator3@warehouse.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Erik', 'Madsen', '+45 67890123', 'Industri Alle 8', 'Horsens', '8700', 'Denmark', 3),

-- Customers
('customer1', 'john.doe@email.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'John', 'Doe', '+45 78901234', 'Customer Street 1', 'Copenhagen', '1000', 'Denmark', 4),
('customer2', 'jane.smith@email.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Jane', 'Smith', '+45 89012345', 'Buyer Road 42', 'Odense', '5000', 'Denmark', 4),
('customer3', 'bob.wilson@email.com', '$2a$10$VO32e6qZ5QIxJAsyfIOZQOSG83zC0guhi4i4mRhb5kZPA77PxhXQW', 'Bob', 'Wilson', '+45 90123456', 'Shopping Avenue 7', 'Aalborg', '9000', 'Denmark', 4);

-- =====================================================
-- CATEGORIES
-- =====================================================
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and components'),
('Clothing', 'Apparel and accessories'),
('Home & Garden', 'Home improvement and gardening supplies'),
('Sports & Outdoors', 'Sports equipment and outdoor gear'),
('Books & Media', 'Books, movies, and music'),
('Food & Beverages', 'Food items and drinks'),
('Health & Beauty', 'Personal care and beauty products'),
('Toys & Games', 'Toys, games, and hobbies'),
('Office Supplies', 'Office equipment and stationery'),
('Automotive', 'Car parts and accessories');

-- Sub-categories
INSERT INTO categories (name, description, parent_id) VALUES
('Smartphones', 'Mobile phones and accessories', 1),
('Laptops', 'Notebook computers', 1),
('Audio', 'Headphones, speakers, and audio equipment', 1),
('Men''s Clothing', 'Men''s apparel', 2),
('Women''s Clothing', 'Women''s apparel', 2),
('Furniture', 'Home furniture', 3),
('Garden Tools', 'Tools for gardening', 3);

-- =====================================================
-- PRODUCTS
-- =====================================================
INSERT INTO products (sku, name, description, category_id, price, cost_price, quantity_in_stock, minimum_stock_level, weight_kg, dimensions, location, barcode) VALUES
-- Electronics
('ELEC-001', 'Wireless Bluetooth Headphones', 'High-quality over-ear wireless headphones with noise cancellation', 13, 299.99, 150.00, 150, 20, 0.35, '20x18x8', 'A-01-01', '5901234123457'),
('ELEC-002', 'USB-C Charging Cable 2m', 'Fast charging USB-C cable, 2 meters length', 1, 79.99, 25.00, 500, 100, 0.05, '25x5x2', 'A-01-02', '5901234123458'),
('ELEC-003', 'Wireless Mouse', 'Ergonomic wireless mouse with adjustable DPI', 1, 149.99, 60.00, 200, 30, 0.12, '12x7x4', 'A-01-03', '5901234123459'),
('ELEC-004', 'Laptop Stand Aluminum', 'Adjustable aluminum laptop stand for better ergonomics', 12, 199.99, 80.00, 75, 15, 0.95, '30x25x15', 'A-02-01', '5901234123460'),
('ELEC-005', 'Portable Bluetooth Speaker', 'Waterproof portable speaker with 20h battery life', 13, 399.99, 180.00, 100, 20, 0.55, '18x8x8', 'A-02-02', '5901234123461'),

-- Clothing
('CLTH-001', 'Classic Cotton T-Shirt (M)', 'Premium cotton t-shirt, medium size, various colors', 14, 129.99, 40.00, 300, 50, 0.20, '30x25x3', 'B-01-01', '5901234223457'),
('CLTH-002', 'Classic Cotton T-Shirt (L)', 'Premium cotton t-shirt, large size, various colors', 14, 129.99, 40.00, 280, 50, 0.22, '32x27x3', 'B-01-02', '5901234223458'),
('CLTH-003', 'Denim Jeans Slim Fit', 'Classic slim fit denim jeans', 14, 399.99, 150.00, 150, 25, 0.65, '35x30x5', 'B-02-01', '5901234223459'),
('CLTH-004', 'Winter Jacket Waterproof', 'Warm waterproof jacket for winter', 14, 799.99, 350.00, 80, 15, 1.20, '60x50x10', 'B-03-01', '5901234223460'),
('CLTH-005', 'Running Sneakers', 'Lightweight running shoes with cushioning', 2, 599.99, 250.00, 120, 20, 0.70, '35x25x15', 'B-04-01', '5901234223461'),

-- Home & Garden
('HOME-001', 'LED Desk Lamp', 'Adjustable LED desk lamp with USB port', 3, 249.99, 100.00, 90, 15, 0.80, '45x15x15', 'C-01-01', '5901234323457'),
('HOME-002', 'Throw Pillow Set (2)', 'Decorative throw pillow set, 2 pieces', 16, 179.99, 70.00, 200, 30, 0.60, '45x45x15', 'C-01-02', '5901234323458'),
('HOME-003', 'Garden Hose 25m', 'Flexible garden hose with spray nozzle', 17, 299.99, 120.00, 60, 10, 3.50, '40x40x15', 'C-02-01', '5901234323459'),
('HOME-004', 'Tool Set 50 Pieces', 'Comprehensive home tool set', 3, 449.99, 200.00, 45, 10, 5.20, '50x35x15', 'C-02-02', '5901234323460'),
('HOME-005', 'Ceramic Plant Pot Large', 'Decorative ceramic pot for indoor plants', 3, 149.99, 50.00, 100, 20, 2.30, '30x30x35', 'C-03-01', '5901234323461'),

-- Sports & Outdoors
('SPRT-001', 'Yoga Mat Premium', 'Non-slip yoga mat, 6mm thickness', 4, 199.99, 70.00, 150, 25, 1.20, '180x60x0.6', 'D-01-01', '5901234423457'),
('SPRT-002', 'Dumbbell Set 20kg', 'Adjustable dumbbell set, 2-20kg', 4, 899.99, 450.00, 40, 8, 22.00, '45x25x25', 'D-02-01', '5901234423458'),
('SPRT-003', 'Hiking Backpack 40L', 'Waterproof hiking backpack with rain cover', 4, 549.99, 220.00, 65, 10, 1.50, '55x35x25', 'D-02-02', '5901234423459'),
('SPRT-004', 'Tennis Racket Pro', 'Professional tennis racket, lightweight', 4, 699.99, 300.00, 35, 8, 0.32, '70x28x3', 'D-03-01', '5901234423460'),
('SPRT-005', 'Camping Tent 4-Person', '4-person waterproof camping tent', 4, 1299.99, 600.00, 25, 5, 4.80, '60x25x25', 'D-03-02', '5901234423461'),

-- Office Supplies
('OFFC-001', 'Notebook A4 Hardcover', 'Premium hardcover notebook, 200 pages', 9, 79.99, 25.00, 400, 80, 0.45, '30x22x2', 'E-01-01', '5901234523457'),
('OFFC-002', 'Pen Set Blue/Black', 'Set of 10 ballpoint pens', 9, 49.99, 15.00, 600, 100, 0.15, '15x8x2', 'E-01-02', '5901234523458'),
('OFFC-003', 'Desk Organizer', 'Multi-compartment desk organizer', 9, 129.99, 45.00, 120, 20, 0.65, '25x18x12', 'E-01-03', '5901234523459'),
('OFFC-004', 'Printer Paper A4 500 Sheets', 'High-quality A4 printer paper', 9, 89.99, 35.00, 250, 50, 2.50, '30x22x6', 'E-02-01', '5901234523460'),
('OFFC-005', 'Whiteboard 90x60cm', 'Magnetic whiteboard with markers', 9, 349.99, 140.00, 30, 5, 3.20, '92x62x3', 'E-02-02', '5901234523461'),

-- More Electronics
('ELEC-006', 'Smartphone 128GB', 'Latest smartphone with 6.5" display, triple camera', 11, 3999.99, 2500.00, 50, 10, 0.18, '16x8x0.8', 'A-03-01', '5901234123462'),
('ELEC-007', 'Tablet 10" 64GB', '10-inch tablet with stylus support', 12, 2499.99, 1500.00, 75, 15, 0.48, '25x17x0.7', 'A-03-02', '5901234123463'),
('ELEC-008', 'Smart Watch', 'Fitness tracker with heart rate monitor', 1, 1299.99, 600.00, 120, 20, 0.05, '4x4x1', 'A-03-03', '5901234123464'),
('ELEC-009', 'Gaming Keyboard RGB', 'Mechanical gaming keyboard with RGB lighting', 1, 899.99, 400.00, 80, 15, 1.20, '45x15x3', 'A-04-01', '5901234123465'),
('ELEC-010', 'Webcam HD 1080p', 'High-definition webcam for video calls', 1, 499.99, 200.00, 150, 25, 0.15, '10x5x5', 'A-04-02', '5901234123466'),

-- More Clothing
('CLTH-006', 'Leather Jacket', 'Genuine leather jacket, classic style', 14, 1999.99, 800.00, 40, 8, 1.50, '55x50x8', 'B-05-01', '5901234223462'),
('CLTH-007', 'Wool Sweater', '100% wool sweater, warm and comfortable', 14, 599.99, 250.00, 100, 20, 0.45, '40x35x3', 'B-05-02', '5901234223463'),
('CLTH-008', 'Baseball Cap', 'Adjustable baseball cap, various colors', 2, 199.99, 60.00, 200, 30, 0.10, '25x25x10', 'B-06-01', '5901234223464'),
('CLTH-009', 'Winter Boots', 'Waterproof winter boots with insulation', 2, 899.99, 400.00, 60, 12, 1.80, '35x30x20', 'B-06-02', '5901234223465'),
('CLTH-010', 'Dress Shirt Formal', 'Formal dress shirt, white and blue colors', 14, 399.99, 150.00, 150, 25, 0.30, '35x30x2', 'B-07-01', '5901234223466'),

-- More Home & Garden
('HOME-006', 'Coffee Maker', 'Programmable coffee maker, 12 cups', 3, 599.99, 250.00, 45, 10, 3.50, '30x25x35', 'C-04-01', '5901234323462'),
('HOME-007', 'Vacuum Cleaner', 'Bagless vacuum cleaner with HEPA filter', 3, 1299.99, 600.00, 30, 8, 5.20, '40x30x30', 'C-04-02', '5901234323463'),
('HOME-008', 'Air Purifier', 'HEPA air purifier for rooms up to 50m²', 3, 899.99, 400.00, 25, 5, 4.50, '35x35x60', 'C-05-01', '5901234323464'),
('HOME-009', 'Kitchen Knife Set', 'Professional 8-piece knife set', 3, 699.99, 300.00, 50, 10, 2.10, '40x15x8', 'C-05-02', '5901234323465'),
('HOME-010', 'Bed Sheets Set', 'Premium cotton bed sheets, queen size', 16, 399.99, 150.00, 80, 15, 1.20, '80x60x10', 'C-06-01', '5901234323466'),

-- More Sports & Outdoors
('SPRT-006', 'Bicycle Helmet', 'Safety certified bicycle helmet, adjustable', 4, 299.99, 120.00, 90, 15, 0.35, '30x25x20', 'D-04-01', '5901234423462'),
('SPRT-007', 'Running Watch GPS', 'GPS running watch with heart rate monitor', 4, 1499.99, 700.00, 40, 8, 0.08, '5x4x1', 'D-04-02', '5901234423463'),
('SPRT-008', 'Swimming Goggles', 'Anti-fog swimming goggles, UV protection', 4, 149.99, 50.00, 150, 25, 0.05, '15x8x5', 'D-05-01', '5901234423464'),
('SPRT-009', 'Basketball', 'Official size basketball, indoor/outdoor', 4, 399.99, 150.00, 70, 12, 0.60, '25x25x25', 'D-05-02', '5901234423465'),
('SPRT-010', 'Fitness Resistance Bands', 'Set of 5 resistance bands, various strengths', 4, 199.99, 80.00, 100, 20, 0.30, '30x20x5', 'D-06-01', '5901234423466'),

-- More Office Supplies
('OFFC-006', 'Stapler Heavy Duty', 'Heavy-duty stapler, 210 sheet capacity', 9, 149.99, 60.00, 120, 20, 0.55, '20x10x8', 'E-03-01', '5901234523462'),
('OFFC-007', 'File Cabinet 2-Drawer', 'Metal file cabinet, letter size', 9, 1299.99, 600.00, 15, 3, 25.00, '50x40x70', 'E-03-02', '5901234523463'),
('OFFC-008', 'Monitor Stand', 'Adjustable monitor stand with USB hub', 9, 299.99, 120.00, 60, 10, 2.50, '60x25x15', 'E-04-01', '5901234523464'),
('OFFC-009', 'Document Shredder', 'Cross-cut shredder, 8 sheets capacity', 9, 799.99, 350.00, 20, 5, 5.50, '40x30x25', 'E-04-02', '5901234523465'),
('OFFC-010', 'Label Maker', 'Electronic label maker with keyboard', 9, 499.99, 200.00, 45, 8, 0.80, '20x15x8', 'E-05-01', '5901234523466');

-- =====================================================
-- SAMPLE ORDERS
-- =====================================================
INSERT INTO orders (order_number, customer_id, status, total_amount, shipping_address, shipping_city, shipping_postal_code, shipping_country, shipping_phone, notes, processed_by, created_at) VALUES
('ORD-20241201-000001', 8, 'DELIVERED', 529.97, 'Customer Street 1', 'Copenhagen', '1000', 'Denmark', '+45 78901234', 'Please leave at door', 5, '2024-12-01 10:30:00'),
('ORD-20241202-000002', 9, 'SHIPPED', 1099.98, 'Buyer Road 42', 'Odense', '5000', 'Denmark', '+45 89012345', NULL, 5, '2024-12-02 14:15:00'),
('ORD-20241203-000003', 10, 'PROCESSING', 449.99, 'Shopping Avenue 7', 'Aalborg', '9000', 'Denmark', '+45 90123456', 'Gift wrapping requested', 6, '2024-12-03 09:45:00'),
('ORD-20241204-000004', 8, 'CONFIRMED', 799.99, 'Customer Street 1', 'Copenhagen', '1000', 'Denmark', '+45 78901234', NULL, NULL, '2024-12-04 16:20:00'),
('ORD-20241205-000005', 9, 'PENDING', 1549.98, 'Buyer Road 42', 'Odense', '5000', 'Denmark', '+45 89012345', 'Urgent delivery needed', NULL, '2024-12-05 11:00:00');

-- =====================================================
-- ORDER ITEMS (disable trigger temporarily for seed data)
-- =====================================================
ALTER TABLE order_items DISABLE TRIGGER trigger_update_stock;
ALTER TABLE order_items DISABLE TRIGGER trigger_update_order_total;

INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
-- Order 1
(1, 1, 1, 299.99, 299.99),  -- Headphones
(1, 2, 2, 79.99, 159.98),   -- USB Cables
(1, 22, 1, 79.99, 79.99),   -- Notebook

-- Order 2
(2, 4, 1, 199.99, 199.99),  -- Laptop Stand
(2, 5, 1, 399.99, 399.99),  -- Bluetooth Speaker
(2, 17, 1, 199.99, 199.99), -- Yoga Mat
(2, 21, 1, 299.99, 299.99), -- Garden Hose

-- Order 3
(3, 14, 1, 449.99, 449.99), -- Tool Set

-- Order 4
(4, 9, 1, 799.99, 799.99),  -- Winter Jacket

-- Order 5
(5, 20, 1, 1299.99, 1299.99), -- Camping Tent
(5, 11, 1, 249.99, 249.99);   -- LED Lamp

ALTER TABLE order_items ENABLE TRIGGER trigger_update_stock;
ALTER TABLE order_items ENABLE TRIGGER trigger_update_order_total;

-- =====================================================
-- SAMPLE INVENTORY TRANSACTIONS
-- =====================================================
INSERT INTO inventory_transactions (product_id, transaction_type, quantity, notes, performed_by, created_at) VALUES
-- Initial stock purchases
(1, 'PURCHASE', 200, 'Initial stock', 5, '2024-11-01 08:00:00'),
(2, 'PURCHASE', 600, 'Initial stock', 5, '2024-11-01 08:00:00'),
(3, 'PURCHASE', 250, 'Initial stock', 5, '2024-11-01 08:00:00'),
(4, 'PURCHASE', 100, 'Initial stock', 5, '2024-11-01 08:00:00'),
(5, 'PURCHASE', 120, 'Initial stock', 5, '2024-11-01 08:00:00'),

-- Some adjustments
(1, 'DAMAGED', -5, 'Damaged in storage', 6, '2024-11-15 10:30:00'),
(3, 'ADJUSTMENT', -10, 'Inventory count correction', 6, '2024-11-20 14:00:00'),

-- Recent restocking
(6, 'PURCHASE', 100, 'Restocking t-shirts', 5, '2024-12-01 09:00:00'),
(17, 'PURCHASE', 50, 'Restocking yoga mats', 5, '2024-12-01 09:00:00');

-- =====================================================
-- VERIFY DATA
-- =====================================================
-- Run these to verify:
-- SELECT * FROM roles;
-- SELECT u.username, u.email, r.name as role FROM users u JOIN roles r ON u.role_id = r.id;
-- SELECT * FROM categories WHERE parent_id IS NULL;
-- SELECT COUNT(*) as total_products FROM products;
-- SELECT * FROM v_order_summary;
-- SELECT * FROM v_low_stock_products;
