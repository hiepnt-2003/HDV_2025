-- Dữ liệu cho booking_db
USE booking_db;

-- Đảm bảo bảng đã được tạo
-- Bảng bookings
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

-- Bảng check_ins
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

-- Xóa dữ liệu cũ
DELETE FROM check_ins;
DELETE FROM bookings;

-- Đặt lại auto-increment
ALTER TABLE check_ins AUTO_INCREMENT = 1;
ALTER TABLE bookings AUTO_INCREMENT = 1;

-- Thêm dữ liệu vào bảng bookings
-- Các giá trị status hợp lệ: 'CANCELLED', 'CHECKED_IN', 'PENDING'
INSERT INTO bookings (customer_id, room_id, booking_date, check_in_date, status, notes) VALUES
(1, 1, '2025-02-01 09:00:00', '2025-05-16', 'PENDING', 'Khách hàng sẽ nhận phòng vào buổi chiều'),
(2, 3, '2025-02-05 10:30:00', '2025-06-10', 'CHECKED_IN', 'Khách hàng yêu cầu thêm ga trải giường'),
(3, 5, '2025-02-10 14:00:00', '2025-06-01', 'PENDING', 'Khách hàng thanh toán trước 50%'),
(4, 7, '2025-02-15 11:00:00', '2025-06-10', 'PENDING', 'Đang chờ xác nhận thanh toán'),
(5, 9, '2025-03-10 09:30:00', '2025-07-066', 'CHECKED_IN', 'Khách hàng VIP, chuẩn bị phòng đặc biệt'),
(6, 2, '2025-03-25 15:45:00', '2025-07-05', 'PENDING', 'Khách hàng đặt phòng dài hạn'),
(7, 4, '2025-03-30 13:20:00', '2025-08-10', 'CANCELLED', 'Đợi xác nhận thông tin từ khách hàng'),
(8, 6, '2025-04-05 10:15:00', '2025-07-15', 'PENDING', 'Khách hàng cần đưa đón từ sân bay'),
(9, 8, '2025-04-10 09:30:00', '2025-06-20', 'CANCELLED', 'Khách hàng cần xác nhận lại thời gian'),
(10, 10, '2025-04-15 14:00:00', '2025-05-25', 'PENDING', 'Khách hàng yêu cầu phòng yên tĩnh');

-- Thêm dữ liệu vào bảng check_ins
-- Các giá trị status hợp lệ: 'ACTIVE', 'CHECKED_OUT'
INSERT INTO check_ins (booking_id, customer_id, room_id, check_in_date, expected_check_out_date, status) VALUES
(2, 2, 3, '2025-05-16', '2025-05-16', 'ACTIVE'),
(5, 5, 9, '2025-05-15', '2025-05-15', 'ACTIVE');