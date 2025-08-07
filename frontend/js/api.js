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
            if (!response.ok) throw new Error('Lỗi khi cập nhật trạng thái phòng');
            return await response.json();
        } catch (error) {
            console.error('Error updating room status:', error);
            throw error;
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

        console.log(`Cập nhật trạng thái booking #${id} thành ${status}`);

        try {
            const response = await fetch(`${API_BASE_URL}/api/bookings/${id}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(status)
            });

            console.log('Server response status:', response.status);

            if (!response.ok) {
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

    // Lấy check-in theo booking ID
    getCheckInByBookingId: async (bookingId) => {
        const response = await fetch(`${API_BASE_URL}/api/check-ins/booking/${bookingId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin check-in theo booking');
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

    // Tạo nhận phòng mới
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
            throw error;
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

    // Cập nhật trạng thái check-in
    updateCheckInStatus: async (id, status) => {
        if (!id || isNaN(id)) {
            throw new Error('ID check-in không hợp lệ');
        }

        try {
            const response = await fetch(`${API_BASE_URL}/api/check-ins/${id}/status`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(status)
            });

            console.log('Server response status:', response.status);

            if (!response.ok) {
                let errorData;
                try {
                    errorData = await response.json();
                    console.error('Server error details:', errorData);
                } catch (e) {
                    const errorText = await response.text();
                    console.error('Error text:', errorText);
                }
                throw new Error(`Lỗi khi cập nhật: ${errorData?.message || 'Không thể cập nhật trạng thái check-in'}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error in updateCheckInStatus:', error);
            throw error;
        }
    },

    // === Payment Service ===

    // Lấy tất cả hóa đơn
    getAllInvoices: async () => {
        const response = await fetch(`${API_BASE_URL}/api/invoices`);
        if (!response.ok) throw new Error('Lỗi khi lấy danh sách hóa đơn');
        return await response.json();
    },

    // Lấy hóa đơn theo ID
    getInvoiceById: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/${id}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thông tin hóa đơn');
        return await response.json();
    },

    // Lấy hóa đơn theo khách hàng
    getInvoicesByCustomerId: async (customerId) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/customer/${customerId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn của khách hàng');
        return await response.json();
    },

    // Lấy hóa đơn theo phòng
    getInvoicesByRoomId: async (roomId) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/room/${roomId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn của phòng');
        return await response.json();
    },

    // Lấy hóa đơn theo trạng thái
    getInvoicesByStatus: async (status) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/status/${status}`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn theo trạng thái');
        return await response.json();
    },

    // Lấy hóa đơn theo kỳ
    getInvoicesByPeriod: async (year, month) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/period?year=${year}&month=${month}`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn theo kỳ');
        return await response.json();
    },

    // Tạo hóa đơn mới
    createInvoice: async (invoiceData) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(invoiceData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi tạo hóa đơn mới');
        }
        return await response.json();
    },

    // Cập nhật trạng thái hóa đơn
    updateInvoiceStatus: async (id, status) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/${id}/status`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(status)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi cập nhật trạng thái hóa đơn');
        }
        return await response.json();
    },

    // Xóa hóa đơn
    deleteInvoice: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Lỗi khi xóa hóa đơn');
    },

    // Lấy thống kê dashboard
    getDashboardStats: async () => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/dashboard-stats`);
        if (!response.ok) throw new Error('Lỗi khi lấy thống kê dashboard');
        return await response.json();
    },

    // Lấy doanh thu theo tháng
    getRevenueByMonth: async (year, month) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/revenue/month?year=${year}&month=${month}`);
        if (!response.ok) throw new Error('Lỗi khi lấy doanh thu theo tháng');
        return await response.json();
    },

    // Lấy doanh thu theo năm
    getRevenueByYear: async (year) => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/revenue/year?year=${year}`);
        if (!response.ok) throw new Error('Lỗi khi lấy doanh thu theo năm');
        return await response.json();
    },

    // Lấy hóa đơn quá hạn
    getOverdueInvoices: async () => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/overdue`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn quá hạn');
        return await response.json();
    },

    // Lấy hóa đơn sắp đến hạn
    getInvoicesDueSoon: async () => {
        const response = await fetch(`${API_BASE_URL}/api/invoices/due-soon`);
        if (!response.ok) throw new Error('Lỗi khi lấy hóa đơn sắp đến hạn');
        return await response.json();
    },

    // === Payment History ===

    // Lấy tất cả lịch sử thanh toán
    getAllPayments: async () => {
        const response = await fetch(`${API_BASE_URL}/api/payments`);
        if (!response.ok) throw new Error('Lỗi khi lấy lịch sử thanh toán');
        return await response.json();
    },

    // Lấy lịch sử thanh toán theo hóa đơn
    getPaymentsByInvoiceId: async (invoiceId) => {
        const response = await fetch(`${API_BASE_URL}/api/payments/invoice/${invoiceId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy lịch sử thanh toán của hóa đơn');
        return await response.json();
    },

    // Lấy lịch sử thanh toán theo khoảng thời gian
    getPaymentsByDateRange: async (startDate, endDate) => {
        const response = await fetch(`${API_BASE_URL}/api/payments/date-range?startDate=${startDate}&endDate=${endDate}`);
        if (!response.ok) throw new Error('Lỗi khi lấy lịch sử thanh toán theo thời gian');
        return await response.json();
    },

    // Xử lý thanh toán
    processPayment: async (paymentData) => {
        const response = await fetch(`${API_BASE_URL}/api/payments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(paymentData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi xử lý thanh toán');
        }
        return await response.json();
    },

    // Lấy tổng tiền đã thanh toán
    getTotalPaidByInvoiceId: async (invoiceId) => {
        const response = await fetch(`${API_BASE_URL}/api/payments/total-paid/${invoiceId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy tổng tiền đã thanh toán');
        return await response.json();
    },

    // Lấy thống kê thanh toán theo phương thức
    getPaymentStatsByMethod: async (startDate, endDate) => {
        const response = await fetch(`${API_BASE_URL}/api/payments/stats/by-method?startDate=${startDate}&endDate=${endDate}`);
        if (!response.ok) throw new Error('Lỗi khi lấy thống kê thanh toán');
        return await response.json();
    },

    // === Utility Readings ===

    // Lấy tất cả số đọc điện nước
    getAllUtilityReadings: async () => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings`);
        if (!response.ok) throw new Error('Lỗi khi lấy số đọc điện nước');
        return await response.json();
    },

    // Lấy số đọc theo phòng
    getUtilityReadingsByRoomId: async (roomId) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings/room/${roomId}`);
        if (!response.ok) throw new Error('Lỗi khi lấy số đọc của phòng');
        return await response.json();
    },

    // Lấy số đọc theo phòng và kỳ
    getUtilityReadingByRoomAndPeriod: async (roomId, year, month) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings/room/${roomId}/period?year=${year}&month=${month}`);
        if (!response.ok) throw new Error('Lỗi khi lấy số đọc theo kỳ');
        return await response.json();
    },

    // Lấy số đọc mới nhất của phòng
    getLatestUtilityReading: async (roomId) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings/room/${roomId}/latest`);
        if (!response.ok) throw new Error('Lỗi khi lấy số đọc mới nhất');
        return await response.json();
    },

    // Lưu số đọc mới
    saveUtilityReading: async (readingData) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(readingData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi lưu số đọc điện nước');
        }
        return await response.json();
    },

    // Cập nhật số đọc
    updateUtilityReading: async (id, readingData) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(readingData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi cập nhật số đọc điện nước');
        }
        return await response.json();
    },

    // Xóa số đọc
    deleteUtilityReading: async (id) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/readings/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Lỗi khi xóa số đọc điện nước');
    },

    // === Service Rates ===

    // Lấy tất cả giá dịch vụ
    getAllServiceRates: async () => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/rates`);
        if (!response.ok) throw new Error('Lỗi khi lấy giá dịch vụ');
        return await response.json();
    },

    // Lấy giá dịch vụ hiện tại
    getCurrentServiceRates: async () => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/rates/current`);
        if (!response.ok) throw new Error('Lỗi khi lấy giá dịch vụ hiện tại');
        return await response.json();
    },

    // Lấy giá dịch vụ hiện tại theo loại
    getCurrentRateByServiceType: async (serviceType) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/rates/current/${serviceType}`);
        if (!response.ok) throw new Error('Lỗi khi lấy giá dịch vụ theo loại');
        return await response.json();
    },

    // Lưu giá dịch vụ mới
    saveServiceRate: async (rateData) => {
        const response = await fetch(`${API_BASE_URL}/api/utilities/rates`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(rateData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Lỗi khi lưu giá dịch vụ');
        }
        return await response.json();
    }
};

// Export API object
window.API = API;