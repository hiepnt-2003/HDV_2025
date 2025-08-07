-- Tạo database cho payment service
CREATE DATABASE IF NOT EXISTS payment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE payment_db;

-- Bảng lưu thông tin hóa đơn
CREATE TABLE invoices (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          invoice_number VARCHAR(20) NOT NULL UNIQUE,
                          customer_id BIGINT NOT NULL,
                          room_id BIGINT NOT NULL,
                          check_in_id BIGINT,

    -- Thông tin thời gian
                          billing_period_month INT NOT NULL,
                          billing_period_year INT NOT NULL,
                          issue_date DATE NOT NULL,
                          due_date DATE NOT NULL,

    -- Thông tin phòng
                          room_number VARCHAR(10) NOT NULL,
                          monthly_rent DECIMAL(15,2) NOT NULL,

    -- Thông tin điện nước
                          electricity_previous_reading DECIMAL(10,2) DEFAULT 0,
                          electricity_current_reading DECIMAL(10,2) DEFAULT 0,
                          electricity_usage DECIMAL(10,2) GENERATED ALWAYS AS (electricity_current_reading - electricity_previous_reading) STORED,
                          electricity_unit_price DECIMAL(10,2) DEFAULT 3500,
                          electricity_amount DECIMAL(15,2) GENERATED ALWAYS AS (electricity_usage * electricity_unit_price) STORED,

                          water_previous_reading DECIMAL(10,2) DEFAULT 0,
                          water_current_reading DECIMAL(10,2) DEFAULT 0,
                          water_usage DECIMAL(10,2) GENERATED ALWAYS AS (water_current_reading - water_previous_reading) STORED,
                          water_unit_price DECIMAL(10,2) DEFAULT 25000,
                          water_amount DECIMAL(15,2) GENERATED ALWAYS AS (water_usage * water_unit_price) STORED,

    -- Các khoản phí khác
                          service_fee DECIMAL(15,2) DEFAULT 0,
                          internet_fee DECIMAL(15,2) DEFAULT 150000,
                          parking_fee DECIMAL(15,2) DEFAULT 0,
                          other_fees DECIMAL(15,2) DEFAULT 0,
                          other_fees_description TEXT,

    -- Tổng tiền
                          subtotal DECIMAL(15,2) GENERATED ALWAYS AS (
                              monthly_rent + electricity_amount + water_amount + service_fee + internet_fee + parking_fee + other_fees
                              ) STORED,
                          discount_amount DECIMAL(15,2) DEFAULT 0,
                          total_amount DECIMAL(15,2) GENERATED ALWAYS AS (subtotal - discount_amount) STORED,

    -- Trạng thái thanh toán
                          status ENUM('PENDING', 'PAID', 'OVERDUE', 'CANCELLED') DEFAULT 'PENDING',
                          payment_date DATETIME NULL,
                          payment_method VARCHAR(50) NULL,

    -- Ghi chú
                          notes TEXT,

    -- Timestamps
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes
                          INDEX idx_customer_id (customer_id),
                          INDEX idx_room_id (room_id),
                          INDEX idx_check_in_id (check_in_id),
                          INDEX idx_billing_period (billing_period_year, billing_period_month),
                          INDEX idx_status (status),
                          INDEX idx_issue_date (issue_date),
                          INDEX idx_due_date (due_date)
);

-- Bảng lịch sử thanh toán
CREATE TABLE payment_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 invoice_id BIGINT NOT NULL,
                                 amount_paid DECIMAL(15,2) NOT NULL,
                                 payment_date DATETIME NOT NULL,
                                 payment_method VARCHAR(50) NOT NULL,
                                 reference_number VARCHAR(100),
                                 notes TEXT,
                                 created_by VARCHAR(100),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                 FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
                                 INDEX idx_invoice_id (invoice_id),
                                 INDEX idx_payment_date (payment_date)
);

-- Bảng cấu hình giá dịch vụ
CREATE TABLE service_rates (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               service_type ENUM('ELECTRICITY', 'WATER', 'INTERNET', 'SERVICE', 'PARKING') NOT NULL,
                               rate DECIMAL(10,2) NOT NULL,
                               unit VARCHAR(20) NOT NULL,
                               description VARCHAR(255),
                               effective_date DATE NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               INDEX idx_service_type (service_type),
                               INDEX idx_effective_date (effective_date),
                               INDEX idx_is_active (is_active)
);

-- Bảng lưu số đọc điện nước hàng tháng
CREATE TABLE utility_readings (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  room_id BIGINT NOT NULL,
                                  reading_month INT NOT NULL,
                                  reading_year INT NOT NULL,
                                  electricity_reading DECIMAL(10,2) DEFAULT 0,
                                  water_reading DECIMAL(10,2) DEFAULT 0,
                                  reading_date DATE NOT NULL,
                                  recorded_by VARCHAR(100),
                                  notes TEXT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                  UNIQUE KEY uk_room_period (room_id, reading_year, reading_month),
                                  INDEX idx_room_id (room_id),
                                  INDEX idx_reading_period (reading_year, reading_month),
                                  INDEX idx_reading_date (reading_date)
);

-- Thêm dữ liệu mẫu cho service rates
INSERT INTO service_rates (service_type, rate, unit, description, effective_date) VALUES
                                                                                      ('ELECTRICITY', 3500, 'VND/kWh', 'Giá điện sinh hoạt', '2024-01-01'),
                                                                                      ('WATER', 25000, 'VND/m³', 'Giá nước sinh hoạt', '2024-01-01'),
                                                                                      ('INTERNET', 150000, 'VND/tháng', 'Phí internet cố định', '2024-01-01'),
                                                                                      ('SERVICE', 100000, 'VND/tháng', 'Phí dịch vụ chung', '2024-01-01'),
                                                                                      ('PARKING', 200000, 'VND/tháng', 'Phí gửi xe', '2024-01-01');

-- Trigger để tự động tạo invoice number
DELIMITER //
CREATE TRIGGER generate_invoice_number
    BEFORE INSERT ON invoices
    FOR EACH ROW
BEGIN
    DECLARE next_number INT;
    DECLARE invoice_prefix VARCHAR(10);

    SET invoice_prefix = CONCAT('INV', DATE_FORMAT(NEW.issue_date, '%Y%m'));

    SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number, 10) AS UNSIGNED)), 0) + 1
    INTO next_number
    FROM invoices
    WHERE invoice_number LIKE CONCAT(invoice_prefix, '%');

    SET NEW.invoice_number = CONCAT(invoice_prefix, LPAD(next_number, 4, '0'));
END//
DELIMITER ;



-- Dữ liệu mẫu cho Payment Service phù hợp với các service khác
USE payment_db;

-- Xóa dữ liệu cũ
DELETE FROM payment_history;
DELETE FROM invoices;
DELETE FROM utility_readings;

-- Đặt lại auto-increment
ALTER TABLE invoices AUTO_INCREMENT = 1;
ALTER TABLE payment_history AUTO_INCREMENT = 1;
ALTER TABLE utility_readings AUTO_INCREMENT = 1;

-- Thêm số đọc điện nước cho các phòng đã nhận (tháng 4/2025)
INSERT INTO utility_readings (room_id, reading_month, reading_year, electricity_reading, water_reading, reading_date, recorded_by, notes) VALUES
-- Phòng A201 (Room ID 4) - Khách hàng Binh Tran đã check-in
(4, 4, 2025, 120.5, 15.2, '2025-04-30', 'admin', 'Số đọc cuối tháng 4'),
-- Phòng C101 (Room ID 10) - Khách hàng Em Hoang đã check-in
(10, 4, 2025, 135.8, 18.7, '2025-04-30', 'admin', 'Số đọc cuối tháng 4');

-- Thêm số đọc điện nước tháng 5/2025 (tháng hiện tại)
INSERT INTO utility_readings (room_id, reading_month, reading_year, electricity_reading, water_reading, reading_date, recorded_by, notes) VALUES
-- Phòng A201 (Room ID 4) - Khách hàng Binh Tran
(4, 5, 2025, 145.8, 17.5, '2025-05-31', 'admin', 'Số đọc cuối tháng 5'),
-- Phòng C101 (Room ID 10) - Khách hàng Em Hoang
(10, 5, 2025, 165.2, 22.3, '2025-05-31', 'admin', 'Số đọc cuối tháng 5');

-- Thêm hóa đơn tháng 5/2025 cho các phòng đã nhận
INSERT INTO invoices (
    customer_id, room_id, check_in_id,
    billing_period_month, billing_period_year,
    issue_date, due_date,
    room_number, monthly_rent,
    electricity_previous_reading, electricity_current_reading, electricity_unit_price,
    water_previous_reading, water_current_reading, water_unit_price,
    service_fee, internet_fee, parking_fee, other_fees, other_fees_description,
    discount_amount, status, notes
) VALUES
-- Hóa đơn cho khách hàng Binh Tran (Customer ID 2, Room A201)
(2, 4, 1, 5, 2025, '2025-05-01', '2025-05-15',
 'A201', 3200000.00,
 120.5, 145.8, 3500.00,  -- Điện: 25.3 kWh
 15.2, 17.5, 25000.00,   -- Nước: 2.3 m³
 100000.00, 150000.00, 200000.00, 50000.00, 'Phí vệ sinh thêm',
 0.00, 'PAID', 'Đã thanh toán đầy đủ'),

-- Hóa đơn cho khách hàng Em Hoang (Customer ID 5, Room C101)
(5, 10, 2, 5, 2025, '2025-05-01', '2025-05-15',
 'C101', 7000000.00,
 135.8, 165.2, 3500.00,  -- Điện: 29.4 kWh
 18.7, 22.3, 25000.00,   -- Nước: 3.6 m³
 100000.00, 150000.00, 200000.00, 0.00, NULL,
 100000.00, 'PENDING', 'Chờ thanh toán');

-- Thêm hóa đơn tháng 6/2025 (sắp tới) cho các phòng đã nhận
-- Cập nhật số đọc mới cho tháng 6
INSERT INTO utility_readings (room_id, reading_month, reading_year, electricity_reading, water_reading, reading_date, recorded_by, notes) VALUES
-- Phòng A201 (Room ID 4)
(4, 6, 2025, 168.5, 19.8, '2025-06-15', 'admin', 'Số đọc giữa tháng 6'),
-- Phòng C101 (Room ID 10)
(10, 6, 2025, 188.7, 25.1, '2025-06-15', 'admin', 'Số đọc giữa tháng 6');

-- Hóa đơn tháng 6/2025
INSERT INTO invoices (
    customer_id, room_id, check_in_id,
    billing_period_month, billing_period_year,
    issue_date, due_date,
    room_number, monthly_rent,
    electricity_previous_reading, electricity_current_reading, electricity_unit_price,
    water_previous_reading, water_current_reading, water_unit_price,
    service_fee, internet_fee, parking_fee, other_fees, other_fees_description,
    discount_amount, status, notes
) VALUES
-- Hóa đơn tháng 6 cho Binh Tran
(2, 4, 1, 6, 2025, '2025-06-01', '2025-06-15',
 'A201', 3200000.00,
 145.8, 168.5, 3500.00,  -- Điện: 22.7 kWh
 17.5, 19.8, 25000.00,   -- Nước: 2.3 m³
 100000.00, 150000.00, 200000.00, 0.00, NULL,
 0.00, 'PENDING', 'Hóa đơn tháng 6'),

-- Hóa đơn tháng 6 cho Em Hoang
(5, 10, 2, 6, 2025, '2025-06-01', '2025-06-15',
 'C101', 7000000.00,
 165.2, 188.7, 3500.00,  -- Điện: 23.5 kWh
 22.3, 25.1, 25000.00,   -- Nước: 2.8 m³
 100000.00, 150000.00, 200000.00, 0.00, NULL,
 0.00, 'PENDING', 'Hóa đơn tháng 6');

-- Thêm lịch sử thanh toán cho hóa đơn đã thanh toán
INSERT INTO payment_history (invoice_id, amount_paid, payment_date, payment_method, reference_number, notes, created_by) VALUES
-- Thanh toán cho hóa đơn tháng 5 của Binh Tran (Invoice ID 1)
(1, 3788550.0, '2025-05-10 14:30:00', 'BANK_TRANSFER', 'TF202505100001', 'Chuyển khoản ngân hàng ACB', 'system'),

-- Thanh toán một phần cho hóa đơn tháng 5 của Em Hoang (Invoice ID 2)
(2, 5000000.0, '2025-05-12 09:15:00', 'CASH', 'CASH202505120001', 'Thanh toán tiền mặt tại quầy', 'admin');

-- Thêm dữ liệu cho các phòng sắp nhận (tháng 7/2025) - tạo hóa đơn trước
-- Hóa đơn cho các booking PENDING sắp nhận phòng
INSERT INTO invoices (
    customer_id, room_id, check_in_id,
    billing_period_month, billing_period_year,
    issue_date, due_date,
    room_number, monthly_rent,
    electricity_previous_reading, electricity_current_reading, electricity_unit_price,
    water_previous_reading, water_current_reading, water_unit_price,
    service_fee, internet_fee, parking_fee, other_fees, other_fees_description,
    discount_amount, status, notes
) VALUES
-- Hóa đơn tháng 7 cho An Nguyen (Customer ID 1, Room A101) - Booking sắp nhận 16/5
(1, 1, NULL, 7, 2025, '2025-06-25', '2025-07-10',
 'A101', 3000000.00,
 0.00, 0.00, 3500.00,    -- Chưa có số đọc
 0.00, 0.00, 25000.00,
 100000.00, 150000.00, 0.00, 0.00, NULL,
 0.00, 'PENDING', 'Hóa đơn tháng đầu, chưa có số đọc điện nước'),

-- Hóa đơn tháng 7 cho Cuong Le (Customer ID 3, Room B101) - Booking sắp nhận 1/6
(3, 6, NULL, 7, 2025, '2025-06-25', '2025-07-10',
 'B101', 4500000.00,
 0.00, 0.00, 3500.00,
 0.00, 0.00, 25000.00,
 100000.00, 150000.00, 200000.00, 0.00, NULL,
 0.00, 'PENDING', 'Hóa đơn tháng đầu, chưa có số đọc điện nước');

-- Thêm một số hóa đơn quá hạn để test
INSERT INTO invoices (
    customer_id, room_id, check_in_id,
    billing_period_month, billing_period_year,
    issue_date, due_date,
    room_number, monthly_rent,
    electricity_previous_reading, electricity_current_reading, electricity_unit_price,
    water_previous_reading, water_current_reading, water_unit_price,
    service_fee, internet_fee, parking_fee, other_fees, other_fees_description,
    discount_amount, status, notes
) VALUES
-- Hóa đơn quá hạn tháng 4 cho Em Hoang
(5, 10, 2, 4, 2025, '2025-04-01', '2025-04-15',
 'C101', 7000000.00,
 100.0, 135.8, 3500.00,  -- Điện: 35.8 kWh
 15.0, 18.7, 25000.00,   -- Nước: 3.7 m³
 100000.00, 150000.00, 200000.00, 0.00, NULL,
 0.00, 'OVERDUE', 'Hóa đơn quá hạn tháng 4');

-- Cập nhật trạng thái hóa đơn quá hạn
UPDATE invoices SET status = 'OVERDUE' WHERE due_date < CURDATE() AND status = 'PENDING';

-- Kiểm tra dữ liệu
SELECT 'INVOICES' as table_name, COUNT(*) as count FROM invoices
UNION ALL
SELECT 'PAYMENT_HISTORY' as table_name, COUNT(*) as count FROM payment_history
UNION ALL
SELECT 'UTILITY_READINGS' as table_name, COUNT(*) as count FROM utility_readings;

-- Hiển thị tóm tắt hóa đơn
SELECT
    i.id,
    i.room_number,
    i.billing_period_month,
    i.billing_period_year,
    i.monthly_rent,
    (i.electricity_current_reading - i.electricity_previous_reading) * i.electricity_unit_price as electricity_amount,
    (i.water_current_reading - i.water_previous_reading) * i.water_unit_price as water_amount,
    i.service_fee + i.internet_fee + i.parking_fee + i.other_fees as other_fees_total,
    i.monthly_rent +
    (i.electricity_current_reading - i.electricity_previous_reading) * i.electricity_unit_price +
    (i.water_current_reading - i.water_previous_reading) * i.water_unit_price +
    i.service_fee + i.internet_fee + i.parking_fee + i.other_fees - i.discount_amount as total_amount,
    i.status,
    i.due_date
FROM invoices i
ORDER BY i.id;