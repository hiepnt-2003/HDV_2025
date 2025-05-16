// js/api.js
// Cấu hình API base URL - thay đổi IP và port phù hợp với môi trường của bạn
const API_BASE_URL = 'http://localhost:8080';  // URL của API Gateway

// Các hàm gọi API
const API = {
    // === Customer Service ===
    
    // Lấy tất cả khách hàng
    getAllCustomers: async () => {
        const response = await fetch(`${API_BASE_URL}/api/customers`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách khách hàng');
        return await response.json();
    },
    
    // Lấy thông tin khách hàng theo ID
    getCustomerById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/customers/${id}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin khách hàng');
        return await response.json();
    },
    
    // Tìm kiếm khách hàng theo CMND/CCCD hoặc SĐT
    searchCustomers: async (term) => {
        const response = await fetch(`${API_BASE_URL}/api/customers/search?term=${term}`);
        if (!response.ok) throw new Error('Lỗi khi tìm kiếm khách hàng');
        return await response.json();
    },
    
    // Tạo khách hàng mới
    createCustomer: async (customerData) => {
        const response = await fetch(`${API_BASE_URL}/api/customers`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(customerData)
        });
        if (!response.ok) throw new Error('Lỗi khi tạo khách hàng mới');
        return await response.json();
    },
    
    // Cập nhật thông tin khách hàng
    updateCustomer: async (id, customerData) => {
        const response = await fetch(`${API_BASE_URL}/api/customers/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(customerData)
        });
        if (!response.ok) throw new Error('Lỗi khi cập nhật thông tin khách hàng');
        return await response.json();
    },
    
    // === Room Service ===
    
    // Lấy tất cả phòng
    getAllRooms: async () => {
        const response = await fetch(`${API_BASE_URL}/api/rooms`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách phòng');
        return await response.json();
    },
    
    // Lấy thông tin phòng theo ID
    getRoomById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/rooms/${id}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin phòng');
        return await response.json();
    },
    
    // Lấy thông tin phòng theo số phòng
    getRoomByNumber: async (roomNumber) => {
        const response = await fetch(`${API_BASE_URL}/api/rooms/number/${roomNumber}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin phòng');
        return await response.json();
    },
    
    // Lấy danh sách phòng trống
    getAvailableRooms: async () => {
        const response = await fetch(`${API_BASE_URL}/api/rooms/available`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách phòng trống');
        return await response.json();
    },
    
    // Cập nhật trạng thái phòng trong api.js
    updateRoomStatus: async (id, status) => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/rooms/${id}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(status)
            });
            // Xử lý phản hồi...
        } catch (error) {
            // Xử lý lỗi...
        }
    },
    
    // Lấy tất cả loại phòng
    getAllRoomTypes: async () => {
        const response = await fetch(`${API_BASE_URL}/api/room-types`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách loại phòng');
        return await response.json();
    },
    
    // === Booking Service ===
    
    // Lấy tất cả đặt phòng
    getAllBookings: async () => {
        const response = await fetch(`${API_BASE_URL}/api/bookings`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách đặt phòng');
        return await response.json();
    },
    
    // Lấy thông tin đặt phòng theo ID
    getBookingById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/bookings/${id}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin đặt phòng');
        return await response.json();
    },
    
    // Lấy danh sách đặt phòng của khách hàng
    getBookingsByCustomerId: async (customerId) => {
        const response = await fetch(`${API_BASE_URL}/api/bookings/customer/${customerId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách đặt phòng của khách hàng');
        return await response.json();
    },
    
    // Tìm kiếm đặt phòng
    searchBookings: async (term) => {
        const isNumber = !isNaN(term);
        const url = isNumber ? 
            `${API_BASE_URL}/api/bookings/search?bookingId=${term}` : 
            `${API_BASE_URL}/api/bookings/search?customerId=${term}`;
        
        const response = await fetch(url);
        if (!response.ok) throw new Error('Lỗi khi tìm kiếm đặt phòng');
        return await response.json();
    },
    
    // Lấy danh sách đặt phòng theo trạng thái
    getBookingsByStatus: async (status) => {
        const response = await fetch(`${API_BASE_URL}/api/bookings/status/${status}`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách đặt phòng theo trạng thái');
        return await response.json();
    },
    
    // Tạo đặt phòng mới
    createBooking: async (bookingData) => {
        const response = await fetch(`${API_BASE_URL}/api/bookings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookingData)
        });
        if (!response.ok) throw new Error('Lỗi khi tạo đặt phòng mới');
        return await response.json();
    },
    
    // Cập nhật trạng thái đặt phòng
    updateBookingStatus: async (id, status) => {
        if (!id || isNaN(id)) {
            throw new Error('ID đặt phòng không hợp lệ');
        }
        
        // Kiểm tra ID booking hợp lệ
        console.log(`Cập nhật trạng thái booking #${id} thành ${status}`);
        
        try {
            // Gửi request giống hệt như cách Postman gửi
            const response = await fetch(`${API_BASE_URL}/api/bookings/${id}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                // Đảm bảo status được gửi chính xác như trong Postman
                body: JSON.stringify(status)
            });
            
            console.log('Server response status:', response.status);
            
            if (!response.ok) {
                // Lấy thông tin lỗi chi tiết
                let errorData;
                try {
                    errorData = await response.json();
                    console.error('Server error details:', errorData);
                } catch (e) {
                    const errorText = await response.text();
                    console.error('Error text:', errorText);
                }
                throw new Error(`Lỗi khi cập nhật: ${errorData?.message || 'Không thể cập nhật trạng thái đặt phòng'}`);
            }
            
            // Xử lý phản hồi
            return await response.json();
        } catch (error) {
            console.error('Error in updateBookingStatus:', error);
            throw error;
        }
    },
    
    // Lấy tất cả nhận phòng
    getAllCheckIns: async () => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách nhận phòng');
        return await response.json();
    },
    
    // Lấy danh sách nhận phòng của khách hàng
    getCheckInsByCustomerId: async (customerId) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/customer/${customerId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách nhận phòng của khách hàng');
        return await response.json();
    },
    
    // Lấy danh sách nhận phòng theo phòng
    getCheckInsByRoomId: async (roomId) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/room/${roomId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách nhận phòng theo phòng');
        return await response.json();
    },
    
    // Lấy danh sách nhận phòng theo trạng thái
    getCheckInsByStatus: async (status) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/status/${status}`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách nhận phòng theo trạng thái');
        return await response.json();
    },
    
    // Lấy danh sách check-in hoạt động của khách hàng
    getActiveCheckInsByCustomer: async (customerId) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/customer/${customerId}?status=ACTIVE`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách check-in hoạt động');
        return await response.json();
    },
    
    // Tạo nhận phòng mới (cập nhật trong api.js)
    createCheckIn: async (checkInData) => {
        try {
            console.log('API - Creating check-in with data:', JSON.stringify(checkInData));
            
            const response = await fetch(`${API_BASE_URL}/api/check-ins`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(checkInData)
            });
            
            console.log('API - Check-in response status:', response.status);
            
            if (!response.ok) {
                let errorMessage = 'Lỗi khi tạo nhận phòng mới';
                try {
                    const errorData = await response.json();
                    console.error('API Error data:', errorData);
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    const errorText = await response.text();
                    console.error('API Error text:', errorText);
                }
                throw new Error(errorMessage);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API - Error creating check-in:', error);
            throw error; // Ném lỗi để xử lý ở UI
        }
    },
    
    // Tạo nhận phòng từ đặt phòng
    createCheckInFromBooking: async (bookingId) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/from-booking/${bookingId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) throw new Error('Lỗi khi nhận phòng từ đặt phòng');
        return await response.json();
    },
    
};

// Export API object
window.API = API;