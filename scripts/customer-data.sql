USE customer_db;

-- Đảm bảo bảng đã được tạo (nếu không tồn tại trong init-db.sql)
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

-- Xóa dữ liệu cũ nếu cần
DELETE FROM customers;
ALTER TABLE customers AUTO_INCREMENT = 1;

-- Thêm dữ liệu vào bảng customers
INSERT INTO customers (full_name, identification_number, phone_number, email, address) VALUES
('Nguyễn Văn An', '001234567890', '0901234567', 'nguyenvan.an@example.com', 'Quận 1, TP. Hồ Chí Minh'),
('Trần Thị Bình', '001234567891', '0912345678', 'tranthi.binh@example.com', 'Quận Hai Bà Trưng, Hà Nội'),
('Lê Văn Cường', '001234567892', '0923456789', 'levan.cuong@example.com', 'Quận Hải Châu, Đà Nẵng'),
('Phạm Thị Dung', '001234567893', '0934567890', 'phamthi.dung@example.com', 'Quận Ninh Kiều, Cần Thơ'),
('Hoàng Văn Em', '001234567894', '0945678901', 'hoangvan.em@example.com', 'Quận Ngô Quyền, Hải Phòng'),
('Đỗ Thị Phương', '001234567895', '0956789012', 'dothi.phuong@example.com', 'Thành phố Huế, Thừa Thiên Huế'),
('Vũ Văn Giang', '001234567896', '0967890123', 'vuvan.giang@example.com', 'Thành phố Nha Trang, Khánh Hòa'),
('Đặng Thị Hương', '001234567897', '0978901234', 'dangthi.huong@example.com', 'Thành phố Đà Lạt, Lâm Đồng'),
('Bùi Văn Hùng', '001234567898', '0989012345', 'buivan.hung@example.com', 'Quận 7, TP. Hồ Chí Minh'),
('Mai Thị Kiều', '001234567899', '0990123456', 'maithi.kieu@example.com', 'Quận Cầu Giấy, Hà Nội');