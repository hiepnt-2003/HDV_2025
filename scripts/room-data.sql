USE room_db;

-- Đảm bảo bảng đã được tạo
-- Bảng room_types
CREATE TABLE IF NOT EXISTS room_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng rooms
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(50) NOT NULL UNIQUE,
    room_type_id BIGINT NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

-- Xóa dữ liệu cũ
DELETE FROM rooms;
DELETE FROM room_types;

-- Đặt lại auto-increment
ALTER TABLE rooms AUTO_INCREMENT = 1;
ALTER TABLE room_types AUTO_INCREMENT = 1;

-- Thêm dữ liệu vào bảng room_types
INSERT INTO room_types (name, description) VALUES
('Standard', 'Phòng tiêu chuẩn, đầy đủ tiện nghi cơ bản'),
('Deluxe', 'Phòng cao cấp, rộng rãi với tiện nghi bổ sung'),
('VIP', 'Phòng VIP, đầy đủ tiện nghi cao cấp và dịch vụ đặc biệt');

-- Thêm dữ liệu vào bảng rooms
INSERT INTO rooms (room_number, room_type_id, monthly_price, status, description) VALUES
('A101', 1, 3000000.00, 'AVAILABLE', 'Phòng standard tầng 1, hướng đông, có ban công'),
('A102', 1, 3000000.00, 'AVAILABLE', 'Phòng standard tầng 1, hướng tây, có ban công'),
('A201', 1, 3200000.00, 'AVAILABLE', 'Phòng standard tầng 2, hướng đông, có ban công'),
('A202', 1, 3200000.00, 'AVAILABLE', 'Phòng standard tầng 2, hướng tây, có ban công'),
('B101', 2, 4500000.00, 'AVAILABLE', 'Phòng deluxe tầng 1, hướng nam, có ban công rộng'),
('B102', 2, 4500000.00, 'AVAILABLE', 'Phòng deluxe tầng 1, hướng bắc, có ban công rộng'),
('B201', 2, 4800000.00, 'AVAILABLE', 'Phòng deluxe tầng 2, hướng nam, có ban công rộng'),
('B202', 2, 4800000.00, 'AVAILABLE', 'Phòng deluxe tầng 2, hướng bắc, có ban công rộng'),
('C101', 3, 7000000.00, 'AVAILABLE', 'Phòng VIP tầng 1, hướng nam, có phòng khách riêng'),
('C201', 3, 7500000.00, 'AVAILABLE', 'Phòng VIP tầng 2, hướng nam, có phòng khách riêng và ban công lớn');