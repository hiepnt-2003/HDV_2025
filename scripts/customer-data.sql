-- Dữ liệu cho customer_db
USE customer_db;

-- Đảm bảo bảng đã được tạo
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

-- Xóa dữ liệu cũ
DELETE FROM customers;
ALTER TABLE customers AUTO_INCREMENT = 1;

-- Thêm dữ liệu vào bảng customers
INSERT INTO customers (full_name, identification_number, phone_number, email, address) VALUES
('An Nguyen', '001234567890', '0901234567', 'an.nguyen@example.com', 'District 1, Ho Chi Minh City'),
('Binh Tran', '001234567891', '0912345678', 'binh.tran@example.com', 'Hai Ba Trung District, Hanoi'),
('Cuong Le', '001234567892', '0923456789', 'cuong.le@example.com', 'Hai Chau District, Da Nang'),
('Dung Pham', '001234567893', '0934567890', 'dung.pham@example.com', 'Ninh Kieu District, Can Tho'),
('Em Hoang', '001234567894', '0945678901', 'em.hoang@example.com', 'Ngo Quyen District, Hai Phong'),
('Phuong Do', '001234567895', '0956789012', 'phuong.do@example.com', 'Hue City, Thua Thien Hue'),
('Giang Vu', '001234567896', '0967890123', 'giang.vu@example.com', 'Nha Trang City, Khanh Hoa'),
('Huong Dang', '001234567897', '0978901234', 'huong.dang@example.com', 'Da Lat City, Lam Dong'),
('Hung Bui', '001234567898', '0989012345', 'hung.bui@example.com', 'District 7, Ho Chi Minh City'),
('Kieu Mai', '001234567899', '0990123456', 'kieu.mai@example.com', 'Cau Giay District, Hanoi');
