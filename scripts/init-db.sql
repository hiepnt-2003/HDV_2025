-- Database Initialization Script

-- Customer DB
CREATE DATABASE IF NOT EXISTS customer_db;
USE customer_db;

CREATE TABLE IF NOT EXISTS customers (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         full_name VARCHAR(255) NOT NULL,
    identification_number VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Room DB
CREATE DATABASE IF NOT EXISTS room_db;
USE room_db;

CREATE TABLE IF NOT EXISTS room_types (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

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

-- Insert initial room types
INSERT INTO room_types (name, description) VALUES
                                               ('Standard', 'Phòng tiêu chuẩn, đầy đủ tiện nghi cơ bản'),
                                               ('Deluxe', 'Phòng cao cấp, rộng rãi với tiện nghi bổ sung'),
                                               ('VIP', 'Phòng VIP, đầy đủ tiện nghi cao cấp và dịch vụ đặc biệt');

-- Booking DB
CREATE DATABASE IF NOT EXISTS booking_db;
USE booking_db;

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        customer_id BIGINT NOT NULL,
                                        room_id BIGINT NOT NULL,
                                        booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        check_in_date DATE NOT NULL,
                                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS check_ins (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         booking_id BIGINT,
                                         customer_id BIGINT NOT NULL,
                                         room_id BIGINT NOT NULL,
                                         check_in_date DATE NOT NULL,
                                         expected_check_out_date DATE,
                                         status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );