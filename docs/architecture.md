# System Architecture

## Overview
- Hệ thống Quản lý Thuê phòng là một ứng dụng được thiết kế để quản lý nhận phòng và thanh toán hàng tháng cho dịch vụ cho thuê phòng.
- Các module nhận phòng và thanh toán hàng tháng cho phép nhân viên quản lý khách hàng nhận phòng đã đặt hoặc nhận phòng trực tiếp, đồng thời theo dõi các khoản thanh toán hàng tháng và quản lý hóa đơn.

## System Components
- **Booking Service**: Quản lý quá trình đặt phòng và nhận phòng. Cho phép tạo đặt phòng mới, xử lý nhận phòng từ đặt phòng có sẵn hoặc nhận phòng trực tiếp không qua đặt phòng. Duy trì thông tin về trạng thái phòng và khách hàng.
- **Payment Service**: Quản lý việc tạo hóa đơn và xử lý thanh toán. Có khả năng tạo hóa đơn tự động, theo dõi các hóa đơn chưa thanh toán/quá hạn, ghi nhận thanh toán.
- **Customer Service**: Hỗ trợ module nhận phòng và thanh toán bằng cách cung cấp thông tin chi tiết về khách hàng. Cho phép tìm kiếm khách hàng theo CMND/CCCD hoặc số điện thoại để xác thực thông tin khi nhận phòng và thanh toán.
- **Room Service**: Cung cấp thông tin về phòng và cập nhật trạng thái phòng khi có nhận phòng hoặc trả phòng. Theo dõi giá thuê phòng cho việc tạo hóa đơn.
- **API Gateway**: Làm nhiệm vụ định tuyến các yêu cầu từ frontend đến các service tương ứng. Xử lý vấn đề CORS và bảo mật.
- **Eureka Server**: Cho phép các service định danh và tìm kiếm lẫn nhau trong mạng nội bộ.
  
## Communication
- Các service giao tiếp với nhau thông qua RESTful API. Booking Service và Payment Service gọi đến Customer Service và Room Service để lấy thông tin khách hàng và phòng khi cần thiết.
- Việc gọi API giữa các service được thực hiện thông qua Feign Client, một thư viện khai báo REST client của Spring Cloud. Feign Client đơn giản hóa việc gọi API giữa các service với cú pháp khai báo dễ đọc.
- Trong môi trường Docker, các service có thể tham chiếu đến nhau bằng tên service được định nghĩa trong Docker Compose.

## Data Flow
- **Yêu cầu từ client**: Tất cả các yêu cầu từ bên ngoài đều đi qua API Gateway, từ đó định tuyến đến các service thích hợp.
- **Tương tác giữa các service**:
  +  Booking Service gọi Customer Service để xác minh thông tin khách hàng khi đặt phòng
  +  Booking Service gọi Room Service để kiểm tra tình trạng phòng trống và cập nhật trạng thái phòng
  +  Payment Service giao tiếp với Customer và Room Service để tạo hóa đơn
  +  Mỗi service duy trì cơ sở dữ liệu riêng cho dữ liệu domain của mình
- **Tương tác cơ sở dữ liệu**: Mỗi microservice có database riêng (customer_db, room_db, booking_db, payment_db), đảm bảo tính độc lập và tự chủ service.
- **Tích hợp Front-end** Hệ thống bao gồm một giao diện web giao tiếp với các service backend thông qua API Gateway, cung cấp các chức năng đặt phòng, quản lý thanh toán và báo cáo.

## Diagram
- Reference a high-level architecture diagram (place in `docs/asset/`).

## Scalability & Fault Tolerance
- Khả năng chịu lỗi được tăng cường thông qua một số cơ chế:
  + Các instance service đăng ký với Eureka, cho phép failover tự động đến các instance khỏe mạnh
  + API Gateway có thể phát hiện service không khả dụng và cung cấp phản hồi dự phòng
  + Mỗi service duy trì cơ sở dữ liệu riêng, ngăn chặn lỗi hệ thống hoàn toàn nếu một cơ sở dữ liệu trở nên không khả dụng
  + Mẫu Circuit Breaker của Spring Cloud có thể được triển khai để ngăn chặn lỗi dây chuyền
- Hệ thống có thể được mở rộng theo chiều ngang bằng cách triển khai nhiều instance của mỗi service, với việc cân bằng tải được xử lý thông qua Eureka và API Gateway. Ngoài ra, tính chất container hóa của các service cho phép triển khai và mở rộng dễ dàng trong các nền tảng điều phối container như Kubernetes.  
