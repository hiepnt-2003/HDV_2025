// js/index.js - Homepage JavaScript

// Global variables
let allRooms = [];
let roomTypes = [];
let isAdminLoggedIn = false;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadRooms();
    loadRoomTypes();
    checkAdminLogin();
    setupEventListeners();
});

// Load rooms data
async function loadRooms() {
    try {
        allRooms = await API.getAllRooms();
        displayRooms(allRooms);
        updateStats();
    } catch (error) {
        console.error('Error loading rooms:', error);
        document.getElementById('roomsList').innerHTML = '<div class="col-12 text-center"><div class="alert alert-danger"><i class="bi bi-exclamation-triangle me-2"></i>Không thể tải danh sách phòng. Vui lòng thử lại sau.</div></div>';
    }
}

// Load room types
async function loadRoomTypes() {
    try {
        roomTypes = await API.getAllRoomTypes();
        updateRoomTypeFilter();
    } catch (error) {
        console.error('Error loading room types:', error);
    }
}

// Update room type filter
function updateRoomTypeFilter() {
    const select = document.getElementById('roomTypeFilter');
    select.innerHTML = '<option value="">Tất cả loại phòng</option>';
    roomTypes.forEach(function(type) {
        select.innerHTML += '<option value="' + type.id + '">' + type.name + '</option>';
    });
}

// Display rooms
function displayRooms(rooms) {
    const container = document.getElementById('roomsList');

    if (rooms.length === 0) {
        container.innerHTML = '<div class="col-12 text-center"><div class="alert alert-info"><i class="bi bi-info-circle me-2"></i>Không tìm thấy phòng nào phù hợp với tiêu chí tìm kiếm.</div></div>';
        return;
    }

    let html = '';
    rooms.forEach(function(room) {
        const roomTypeName = room.roomType ? room.roomType.name : 'N/A';
        const description = room.description || 'Phòng hiện đại, đầy đủ tiện nghi';

        html += '<div class="col-lg-4 col-md-6" data-room-id="' + room.id + '">';
        html += '  <div class="room-card" onclick="showRoomDetail(' + room.id + ')">';
        html += '    <div class="room-image">';
        html += '      <div class="room-status status-' + room.status.toLowerCase() + '">';
        html += '        ' + getStatusText(room.status);
        html += '      </div>';
        html += '      <div class="position-absolute bottom-0 start-0 p-3">';
        html += '        <div class="text-white">';
        html += '          <h4 class="fw-bold mb-0">' + room.roomNumber + '</h4>';
        html += '          <small>' + roomTypeName + '</small>';
        html += '        </div>';
        html += '      </div>';
        html += '    </div>';
        html += '    <div class="card-body p-4">';
        html += '      <div class="d-flex justify-content-between align-items-start mb-3">';
        html += '        <div>';
        html += '          <h5 class="card-title mb-1">Phòng ' + room.roomNumber + '</h5>';
        html += '          <p class="text-muted small mb-0">' + roomTypeName + '</p>';
        html += '        </div>';
        html += '        <div class="price-tag">';
        html += '          ' + formatCurrency(room.monthlyPrice) + '/tháng';
        html += '        </div>';
        html += '      </div>';
        html += '      <p class="card-text text-muted">' + description + '</p>';
        html += '      <div class="d-flex justify-content-between align-items-center">';
        html += '        <span class="status-badge status-' + room.status.toLowerCase() + '">';
        html += '          <i class="bi ' + getStatusIcon(room.status) + ' me-1"></i>';
        html += '          ' + getStatusText(room.status);
        html += '        </span>';
        html += '        <button class="btn btn-outline-primary btn-sm" onclick="event.stopPropagation(); showRoomDetail(' + room.id + ')">';
        html += '          <i class="bi bi-eye me-1"></i>Chi tiết';
        html += '        </button>';
        html += '      </div>';
        html += '    </div>';
        html += '  </div>';
        html += '</div>';
    });

    container.innerHTML = html;
}

// Update statistics
function updateStats() {
    const total = allRooms.length;
    const available = allRooms.filter(function(room) { return room.status === 'AVAILABLE'; }).length;
    const occupied = allRooms.filter(function(room) { return room.status === 'OCCUPIED'; }).length;
    const booked = allRooms.filter(function(room) { return room.status === 'BOOKED'; }).length;

    document.getElementById('totalRooms').textContent = total;
    document.getElementById('availableRooms').textContent = available;
    document.getElementById('occupiedRooms').textContent = occupied;
    document.getElementById('bookedRooms').textContent = booked;
}

// Filter functions
function applyFilters() {
    const roomType = document.getElementById('roomTypeFilter').value;
    const status = document.getElementById('statusFilter').value;
    const price = document.getElementById('priceFilter').value;
    const search = document.getElementById('searchInput').value.toLowerCase();

    let filtered = allRooms.filter(function(room) {
        const matchType = !roomType || (room.roomType && room.roomType.id.toString() === roomType);
        const matchStatus = !status || room.status === status;
        const matchSearch = !search || room.roomNumber.toLowerCase().includes(search);

        let matchPrice = true;
        if (price) {
            const priceRange = price.split('-');
            const min = parseInt(priceRange[0]);
            const max = parseInt(priceRange[1]);
            const roomPrice = parseFloat(room.monthlyPrice);
            matchPrice = roomPrice >= min && roomPrice <= max;
        }

        return matchType && matchStatus && matchSearch && matchPrice;
    });

    displayRooms(filtered);
    updateFilterButtons();
}

function filterByStatus(status) {
    // Update filter buttons
    document.querySelectorAll('.filter-btn').forEach(function(btn) {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    if (status === 'all') {
        displayRooms(allRooms);
        document.getElementById('statusFilter').value = '';
    } else {
        const filtered = allRooms.filter(function(room) { return room.status === status; });
        displayRooms(filtered);
        document.getElementById('statusFilter').value = status;
    }
}

function clearFilters() {
    document.getElementById('roomTypeFilter').value = '';
    document.getElementById('statusFilter').value = '';
    document.getElementById('priceFilter').value = '';
    document.getElementById('searchInput').value = '';

    // Reset filter buttons
    document.querySelectorAll('.filter-btn').forEach(function(btn) {
        btn.classList.remove('active');
    });
    document.querySelector('.filter-btn').classList.add('active');

    displayRooms(allRooms);
}

function updateFilterButtons() {
    const status = document.getElementById('statusFilter').value;
    document.querySelectorAll('.filter-btn').forEach(function(btn) {
        btn.classList.remove('active');
    });

    if (!status) {
        document.querySelector('.filter-btn').classList.add('active');
    } else {
        const targetBtn = document.querySelector('[onclick="filterByStatus(\'' + status + '\')"]');
        if (targetBtn) {
            targetBtn.classList.add('active');
        }
    }
}

// Room detail modal
function showRoomDetail(roomId) {
    const room = allRooms.find(function(r) { return r.id === roomId; });
    if (!room) return;

    const roomTypeName = room.roomType ? room.roomType.name : 'N/A';
    const description = room.description || 'Phòng hiện đại, đầy đủ tiện nghi, vị trí thuận lợi';

    const content = '<div class="row">' +
        '<div class="col-md-6">' +
        '  <div class="room-image mb-3" style="height: 250px; border-radius: 10px;">' +
        '    <div class="room-status status-' + room.status.toLowerCase() + '">' + getStatusText(room.status) + '</div>' +
        '  </div>' +
        '</div>' +
        '<div class="col-md-6">' +
        '  <h3 class="mb-3">Phòng ' + room.roomNumber + '</h3>' +
        '  <div class="mb-3">' +
        '    <strong>Loại phòng:</strong> ' + roomTypeName + '<br>' +
        '    <strong>Giá thuê:</strong> <span class="text-success fw-bold">' + formatCurrency(room.monthlyPrice) + '/tháng</span><br>' +
        '    <strong>Trạng thái:</strong> ' +
        '    <span class="badge status-' + room.status.toLowerCase() + '">' +
        '      <i class="bi ' + getStatusIcon(room.status) + ' me-1"></i>' + getStatusText(room.status) +
        '    </span>' +
        '  </div>' +
        '  <div class="mb-3">' +
        '    <strong>Mô tả:</strong><br>' +
        '    <p class="text-muted">' + description + '</p>' +
        '  </div>' +
        '  <div class="mb-3">' +
        '    <strong>Tiện ích:</strong>' +
        '    <ul class="list-unstyled">' +
        '      <li><i class="bi bi-wifi text-primary me-2"></i>Wifi miễn phí</li>' +
        '      <li><i class="bi bi-snow2 text-primary me-2"></i>Điều hòa</li>' +
        '      <li><i class="bi bi-droplet text-primary me-2"></i>Nước nóng</li>' +
        '      <li><i class="bi bi-shield-check text-primary me-2"></i>An ninh 24/7</li>' +
        '    </ul>' +
        '  </div>' +
        '</div>' +
        '</div>' +
        '<hr>' +
        '<div class="row">' +
        '  <div class="col-md-4">' +
        '    <div class="text-center p-3 bg-light rounded">' +
        '      <h5 class="text-primary">' + formatCurrency(room.monthlyPrice) + '</h5>' +
        '      <small class="text-muted">Giá thuê/tháng</small>' +
        '    </div>' +
        '  </div>' +
        '  <div class="col-md-4">' +
        '    <div class="text-center p-3 bg-light rounded">' +
        '      <h5 class="text-success">150,000đ</h5>' +
        '      <small class="text-muted">Phí dịch vụ/tháng</small>' +
        '    </div>' +
        '  </div>' +
        '  <div class="col-md-4">' +
        '    <div class="text-center p-3 bg-light rounded">' +
        '      <h5 class="text-info">' + formatCurrency(room.monthlyPrice) + '</h5>' +
        '      <small class="text-muted">Tiền cọc (= 1 tháng thuê)</small>' +
        '    </div>' +
        '  </div>' +
        '</div>';

    document.getElementById('roomDetailContent').innerHTML = content;

    // Show/hide book button based on room status
    const bookBtn = document.getElementById('bookRoomBtn');
    if (room.status === 'AVAILABLE') {
        bookBtn.style.display = 'inline-block';
        bookBtn.onclick = function() { showBookingModal(roomId); };
    } else {
        bookBtn.style.display = 'none';
    }

    // Show modal
    const roomDetailModal = document.getElementById('roomDetailModal');
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        const modal = new bootstrap.Modal(roomDetailModal);
        modal.show();
    } else {
        roomDetailModal.style.display = 'block';
        roomDetailModal.classList.add('show');
    }
}

// Show booking modal
function showBookingModal(roomId) {
    const room = allRooms.find(function(r) { return r.id === roomId; });
    if (!room) return;

    // Hide room detail modal first
    const roomDetailModal = document.getElementById('roomDetailModal');
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        const detailModal = bootstrap.Modal.getInstance(roomDetailModal);
        if (detailModal) {
            detailModal.hide();
        }
    } else {
        roomDetailModal.style.display = 'none';
        roomDetailModal.classList.remove('show');
    }

    // Populate room info
    const roomTypeName = room.roomType ? room.roomType.name : 'N/A';
    document.getElementById('selectedRoomInfo').innerHTML =
        '<div class="room-info-card">' +
        '  <div class="d-flex justify-content-between align-items-center mb-2">' +
        '    <div class="room-number">Phòng ' + room.roomNumber + '</div>' +
        '    <span class="badge bg-success">' + getStatusText(room.status) + '</span>' +
        '  </div>' +
        '  <div class="mb-2">' +
        '    <strong>Loại:</strong> ' + roomTypeName +
        '  </div>' +
        '  <div class="room-price">' +
        '    ' + formatCurrency(room.monthlyPrice) + '/tháng' +
        '  </div>' +
        '</div>';

    // Update summary
    updateBookingSummary(room.monthlyPrice);

    // Set default check-in date (tomorrow)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById('bookingCheckInDate').value = tomorrow.toISOString().split('T')[0];

    // Reset form
    resetBookingForm();

    // Store room ID for later use
    window.selectedRoomId = roomId;

    // Show booking modal
    const bookingModal = document.getElementById('bookingModal');
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        const modal = new bootstrap.Modal(bookingModal);
        modal.show();
    } else {
        bookingModal.style.display = 'block';
        bookingModal.classList.add('show');
    }
}

// Update booking summary
function updateBookingSummary(monthlyPrice) {
    const rent = parseFloat(monthlyPrice);
    const deposit = rent; // Tiền cọc = tiền thuê
    const serviceFee = 150000;
    const total = rent + deposit + serviceFee;

    document.getElementById('bookingSummaryRent').textContent = formatCurrency(rent);
    document.getElementById('bookingSummaryDeposit').textContent = formatCurrency(deposit);
    document.getElementById('bookingSummaryTotal').textContent = formatCurrency(total);
}

// Reset booking form
function resetBookingForm() {
    document.getElementById('bookingCustomerSearch').value = '';
    document.getElementById('bookingNewCustomerCheck').checked = false;
    document.getElementById('bookingFullName').value = '';
    document.getElementById('bookingIdentificationNumber').value = '';
    document.getElementById('bookingPhoneNumber').value = '';
    document.getElementById('bookingEmail').value = '';
    document.getElementById('bookingNotes').value = '';

    // Disable customer fields initially
    toggleBookingCustomerFields(false);
}

// Toggle customer fields enabled/disabled
function toggleBookingCustomerFields(enabled) {
    const fields = ['bookingFullName', 'bookingIdentificationNumber', 'bookingPhoneNumber', 'bookingEmail'];
    fields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        if (enabled) {
            field.removeAttribute('disabled');
            field.classList.remove('customer-form-disabled');
            field.classList.add('customer-form-enabled');
        } else {
            field.setAttribute('disabled', 'disabled');
            field.classList.add('customer-form-disabled');
            field.classList.remove('customer-form-enabled');
        }
    });
}

// Search customer for booking
async function searchBookingCustomer() {
    const searchTerm = document.getElementById('bookingCustomerSearch').value.trim();
    if (!searchTerm) {
        showAlert('Vui lòng nhập CMND/CCCD hoặc số điện thoại để tìm kiếm', 'warning');
        return;
    }

    try {
        const customers = await API.searchCustomers(searchTerm);

        if (customers && customers.length > 0) {
            const customer = customers[0];

            // Fill customer info
            document.getElementById('bookingFullName').value = customer.fullName;
            document.getElementById('bookingIdentificationNumber').value = customer.identificationNumber;
            document.getElementById('bookingPhoneNumber').value = customer.phoneNumber;
            document.getElementById('bookingEmail').value = customer.email || '';

            // Disable fields and uncheck new customer
            document.getElementById('bookingNewCustomerCheck').checked = false;
            toggleBookingCustomerFields(false);

            // Show success
            const searchField = document.getElementById('bookingCustomerSearch');
            searchField.classList.add('customer-found');
            setTimeout(function() {
                searchField.classList.remove('customer-found');
            }, 1000);

            showAlert('Đã tìm thấy thông tin khách hàng!', 'success');
        } else {
            // Not found - enable new customer mode
            document.getElementById('bookingNewCustomerCheck').checked = true;
            toggleBookingCustomerFields(true);
            showAlert('Không tìm thấy khách hàng. Vui lòng nhập thông tin mới.', 'info');
        }
    } catch (error) {
        console.error('Error searching customer:', error);
        showAlert('Lỗi khi tìm kiếm khách hàng. Vui lòng thử lại.', 'danger');
    }
}

// Confirm booking
async function confirmBooking() {
    if (!validateBookingForm()) {
        return;
    }

    const bookingData = {
        customerId: null, // Will be set after creating/finding customer
        roomId: window.selectedRoomId,
        checkInDate: document.getElementById('bookingCheckInDate').value,
        notes: document.getElementById('bookingNotes').value
    };

    const customerData = {
        fullName: document.getElementById('bookingFullName').value.trim(),
        identificationNumber: document.getElementById('bookingIdentificationNumber').value.trim(),
        phoneNumber: document.getElementById('bookingPhoneNumber').value.trim(),
        email: document.getElementById('bookingEmail').value.trim()
    };

    try {
        // Show loading
        const confirmBtn = document.getElementById('confirmBookingBtn');
        confirmBtn.classList.add('btn-loading');
        confirmBtn.disabled = true;

        let customerId;

        // Handle customer (create new or use existing)
        if (document.getElementById('bookingNewCustomerCheck').checked) {
            // Create new customer
            const newCustomer = await API.createCustomer(customerData);
            customerId = newCustomer.id;
        } else {
            // Find existing customer
            const customers = await API.searchCustomers(customerData.identificationNumber);
            if (customers && customers.length > 0) {
                customerId = customers[0].id;
            } else {
                throw new Error('Không tìm thấy thông tin khách hàng');
            }
        }

        bookingData.customerId = customerId;

        // Create booking
        const booking = await API.createBooking(bookingData);

        if (booking) {
            showAlert('Đặt phòng thành công!', 'success');

            // Hide modal
            const bookingModal = document.getElementById('bookingModal');
            if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                const modal = bootstrap.Modal.getInstance(bookingModal);
                if (modal) {
                    modal.hide();
                }
            } else {
                bookingModal.style.display = 'none';
                bookingModal.classList.remove('show');
            }

            // Reload rooms to update status
            await loadRooms();
        }

    } catch (error) {
        console.error('Error creating booking:', error);
        showAlert('Lỗi khi đặt phòng: ' + error.message, 'danger');
    } finally {
        // Hide loading
        const confirmBtn = document.getElementById('confirmBookingBtn');
        confirmBtn.classList.remove('btn-loading');
        confirmBtn.disabled = false;
    }
}

// Validate booking form
function validateBookingForm() {
    const requiredFields = [
        'bookingFullName',
        'bookingIdentificationNumber',
        'bookingPhoneNumber',
        'bookingCheckInDate'
    ];

    let isValid = true;

    requiredFields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        const value = field.value.trim();

        if (!value) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
    });

    if (!isValid) {
        showAlert('Vui lòng điền đầy đủ thông tin bắt buộc!', 'warning');
    }

    return isValid;
}

// Booking function (updated)
function bookRoom(roomId) {
    showBookingModal(roomId);
}

// Login functions
function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert('Vui lòng nhập đầy đủ thông tin!');
        return;
    }

    // Demo login - replace with real authentication
    if (username === 'admin' && password === 'admin123') {
        isAdminLoggedIn = true;
        localStorage.setItem('adminLoggedIn', 'true');

        // Hide login modal
        const loginModal = document.getElementById('loginModal');
        if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
            const modal = bootstrap.Modal.getInstance(loginModal);
            if (modal) modal.hide();
        }

        // Show admin panel
        document.getElementById('adminSection').style.display = 'block';
        document.querySelector('.admin-menu').classList.add('show');

        // Update navbar
        updateNavbar();

        // Show success message
        showAlert('Đăng nhập thành công!', 'success');

        // Scroll to admin section
        document.getElementById('adminSection').scrollIntoView({ behavior: 'smooth' });
    } else {
        showAlert('Tên đăng nhập hoặc mật khẩu không chính xác!', 'danger');
    }
}

function logout() {
    isAdminLoggedIn = false;
    localStorage.removeItem('adminLoggedIn');

    // Hide admin panel
    document.getElementById('adminSection').style.display = 'none';
    document.querySelector('.admin-menu').classList.remove('show');

    // Update navbar
    updateNavbar();

    // Clear login form
    document.getElementById('loginForm').reset();

    showAlert('Đã đăng xuất thành công!', 'info');
}

function checkAdminLogin() {
    if (localStorage.getItem('adminLoggedIn') === 'true') {
        isAdminLoggedIn = true;
        document.getElementById('adminSection').style.display = 'block';
        document.querySelector('.admin-menu').classList.add('show');
        updateNavbar();
    }
}

function updateNavbar() {
    const loginBtn = document.querySelector('[data-bs-target="#loginModal"]');
    if (isAdminLoggedIn) {
        loginBtn.innerHTML = '<i class="bi bi-person-check me-1"></i>Admin';
        loginBtn.onclick = function() {
            document.getElementById('adminSection').scrollIntoView({ behavior: 'smooth' });
        };
        loginBtn.removeAttribute('data-bs-toggle');
        loginBtn.removeAttribute('data-bs-target');
    } else {
        loginBtn.innerHTML = '<i class="bi bi-person-circle me-1"></i>Đăng nhập Admin';
        loginBtn.onclick = null;
        loginBtn.setAttribute('data-bs-toggle', 'modal');
        loginBtn.setAttribute('data-bs-target', '#loginModal');
    }
}

function togglePassword() {
    const passwordField = document.getElementById('password');
    const toggleIcon = document.getElementById('togglePasswordIcon');

    if (passwordField.type === 'password') {
        passwordField.type = 'text';
        toggleIcon.className = 'bi bi-eye-slash';
    } else {
        passwordField.type = 'password';
        toggleIcon.className = 'bi bi-eye';
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount || 0);
}

function getStatusText(status) {
    const statusMap = {
        'AVAILABLE': 'Có sẵn',
        'OCCUPIED': 'Đang thuê',
        'BOOKED': 'Đã đặt',
        'MAINTENANCE': 'Bảo trì'
    };
    return statusMap[status] || status;
}

function getStatusIcon(status) {
    const iconMap = {
        'AVAILABLE': 'bi-check-circle',
        'OCCUPIED': 'bi-person-fill',
        'BOOKED': 'bi-calendar-check',
        'MAINTENANCE': 'bi-tools'
    };
    return iconMap[status] || 'bi-question-circle';
}

function showAlert(message, type) {
    type = type || 'info';
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-' + type + ' alert-dismissible fade show position-fixed';
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';

    document.body.appendChild(alertDiv);

    setTimeout(function() {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

function scrollToRooms() {
    document.getElementById('rooms').scrollIntoView({ behavior: 'smooth' });
}

function scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Setup event listeners
function setupEventListeners() {
    // Login form submit
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        login();
    });

    // Real-time filtering
    document.getElementById('roomTypeFilter').addEventListener('change', applyFilters);
    document.getElementById('statusFilter').addEventListener('change', applyFilters);
    document.getElementById('priceFilter').addEventListener('change', applyFilters);
    document.getElementById('searchInput').addEventListener('input', applyFilters);

    // Booking modal event listeners
    document.getElementById('bookingSearchCustomerBtn').addEventListener('click', searchBookingCustomer);

    document.getElementById('bookingCustomerSearch').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchBookingCustomer();
        }
    });

    document.getElementById('bookingNewCustomerCheck').addEventListener('change', function(e) {
        if (e.target.checked) {
            // Enable customer fields for new customer
            toggleBookingCustomerFields(true);
            // Clear existing data
            document.getElementById('bookingFullName').value = '';
            document.getElementById('bookingIdentificationNumber').value = '';
            document.getElementById('bookingPhoneNumber').value = '';
            document.getElementById('bookingEmail').value = '';
        } else {
            // Disable customer fields
            toggleBookingCustomerFields(false);
        }
    });

    document.getElementById('confirmBookingBtn').addEventListener('click', confirmBooking);

    // Form validation on input
    const bookingFields = ['bookingFullName', 'bookingIdentificationNumber', 'bookingPhoneNumber', 'bookingCheckInDate'];
    bookingFields.forEach(function(fieldId) {
        document.getElementById(fieldId).addEventListener('input', function(e) {
            if (e.target.value.trim()) {
                e.target.classList.remove('is-invalid');
                e.target.classList.add('is-valid');
            } else {
                e.target.classList.remove('is-valid');
            }
        });
    });

    // Handle floating action button
    window.addEventListener('scroll', function() {
        const floatingBtn = document.querySelector('.floating-action');
        if (window.scrollY > 500) {
            floatingBtn.style.display = 'block';
        } else {
            floatingBtn.style.display = 'none';
        }
    });

    // Add smooth scrolling for navbar links
    document.querySelectorAll('a[href^="#"]').forEach(function(anchor) {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Enter key for search
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });

    // Enter key for login
    document.getElementById('password').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            login();
        }
    });

    // Reset form when booking modal is hidden
    if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
        document.getElementById('bookingModal').addEventListener('hidden.bs.modal', function() {
            resetBookingForm();
            // Clear validation classes
            document.querySelectorAll('.is-valid, .is-invalid').forEach(function(el) {
                el.classList.remove('is-valid', 'is-invalid');
            });
        });
    }

    // Auto-search when typing in customer search field
    let searchTimeout;
    document.getElementById('bookingCustomerSearch').addEventListener('input', function(e) {
        clearTimeout(searchTimeout);
        const value = e.target.value.trim();

        if (value.length >= 3) {
            searchTimeout = setTimeout(function() {
                searchBookingCustomer();
            }, 500);
        }
    });
}