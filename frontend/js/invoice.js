// js/invoice.js
document.addEventListener('DOMContentLoaded', () => {
    let allInvoices = [];
    let customers = [];
    let rooms = [];
    let currentServiceRates = {};

    // Initialize page
    init();

    async function init() {
        try {
            await Promise.all([
                loadDashboardStats(),
                loadInvoices(),
                loadCustomers(),
                loadRooms(),
                loadServiceRates()
            ]);

            setupEventListeners();
            setDefaultDates();
        } catch (error) {
            console.error('Error initializing page:', error);
            showAlert('Lỗi khi tải dữ liệu. Vui lòng refresh trang.', 'danger');
        }
    }

    // Load dashboard statistics
    async function loadDashboardStats() {
        try {
            const stats = await API.getDashboardStats();

            document.getElementById('totalInvoices').textContent = stats.totalInvoices || 0;
            document.getElementById('monthlyRevenue').textContent = formatCurrency(stats.monthlyRevenue || 0);
            document.getElementById('pendingCount').textContent = stats.pendingInvoicesCount || 0;
            document.getElementById('overdueCount').textContent = stats.overdueCount || 0;
        } catch (error) {
            console.error('Error loading dashboard stats:', error);
        }
    }

    // Load all invoices
    async function loadInvoices() {
        try {
            showLoading();
            allInvoices = await API.getAllInvoices();
            displayInvoices(allInvoices);
        } catch (error) {
            console.error('Error loading invoices:', error);
            showAlert('Lỗi khi tải danh sách hóa đơn', 'danger');
            document.getElementById('invoicesTableBody').innerHTML =
                '<tr><td colspan="9" class="text-center text-muted">Không thể tải dữ liệu</td></tr>';
        }
    }

    // Load customers for dropdown
    async function loadCustomers() {
        try {
            customers = await API.getAllCustomers();
            populateCustomerDropdown();
        } catch (error) {
            console.error('Error loading customers:', error);
        }
    }

    // Load rooms for dropdown
    async function loadRooms() {
        try {
            rooms = await API.getAllRooms();
            populateRoomDropdown();
        } catch (error) {
            console.error('Error loading rooms:', error);
        }
    }

    // Load service rates
    async function loadServiceRates() {
        try {
            const rates = await API.getCurrentServiceRates();
            currentServiceRates = {};
            rates.forEach(rate => {
                currentServiceRates[rate.serviceType] = rate.rate;
            });
        } catch (error) {
            console.error('Error loading service rates:', error);
        }
    }

    // Display invoices in table
    function displayInvoices(invoices) {
        const tbody = document.getElementById('invoicesTableBody');

        if (!invoices || invoices.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center text-muted">Không có hóa đơn nào</td></tr>';
            return;
        }

        tbody.innerHTML = invoices.map(invoice => `
            <tr class="invoice-row" data-id="${invoice.id}">
                <td>
                    <strong class="text-primary">${invoice.invoiceNumber || `HD${String(invoice.id).padStart(4, '0')}`}</strong>
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <i class="bi bi-person-circle me-2 text-muted"></i>
                        ${invoice.customerName || 'N/A'}
                    </div>
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <i class="bi bi-door-open me-2 text-muted"></i>
                        <strong>${invoice.roomNumber || 'N/A'}</strong>
                    </div>
                </td>
                <td>
                    <span class="badge bg-info">${invoice.billingPeriodMonth}/${invoice.billingPeriodYear}</span>
                </td>
                <td>${formatDate(invoice.issueDate)}</td>
                <td>
                    <span class="${isOverdue(invoice.dueDate, invoice.status) ? 'text-danger fw-bold' : ''}">
                        ${formatDate(invoice.dueDate)}
                    </span>
                </td>
                <td>
                    <strong class="text-success">${formatCurrency(invoice.totalAmount)}</strong>
                </td>
                <td>${getStatusBadge(invoice.status)}</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="viewInvoiceDetail(${invoice.id})" title="Xem chi tiết">
                            <i class="bi bi-eye"></i>
                        </button>
                        ${invoice.status === 'PENDING' || invoice.status === 'OVERDUE' ?
            `<button class="btn btn-outline-success" onclick="showPaymentModal(${invoice.id})" title="Thanh toán">
                                <i class="bi bi-credit-card"></i>
                            </button>` : ''
        }
                        ${invoice.status === 'PENDING' ?
            `<button class="btn btn-outline-danger" onclick="updateInvoiceStatus(${invoice.id}, 'CANCELLED')" title="Hủy">
                                <i class="bi bi-x-circle"></i>
                            </button>` : ''
        }
                    </div>
                </td>
            </tr>
        `).join('');
    }

    // Filter invoices
    function filterInvoices() {
        const statusFilter = document.getElementById('statusFilter').value;
        const monthFilter = document.getElementById('monthFilter').value;
        const yearFilter = document.getElementById('yearFilter').value;
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();

        let filtered = allInvoices.filter(invoice => {
            const matchStatus = !statusFilter || invoice.status === statusFilter;
            const matchMonth = !monthFilter || invoice.billingPeriodMonth == monthFilter;
            const matchYear = !yearFilter || invoice.billingPeriodYear == yearFilter;
            const matchSearch = !searchTerm ||
                (invoice.customerName && invoice.customerName.toLowerCase().includes(searchTerm)) ||
                (invoice.roomNumber && invoice.roomNumber.toLowerCase().includes(searchTerm)) ||
                (invoice.invoiceNumber && invoice.invoiceNumber.toLowerCase().includes(searchTerm));

            return matchStatus && matchMonth && matchYear && matchSearch;
        });

        displayInvoices(filtered);
    }

    // Setup event listeners
    function setupEventListeners() {
        // Filter change events
        document.getElementById('statusFilter').addEventListener('change', filterInvoices);
        document.getElementById('monthFilter').addEventListener('change', filterInvoices);
        document.getElementById('yearFilter').addEventListener('change', filterInvoices);
        document.getElementById('searchInput').addEventListener('input', filterInvoices);

        // Create invoice form
        document.getElementById('createInvoiceForm').addEventListener('submit', handleCreateInvoice);

        // Payment form
        document.getElementById('paymentForm').addEventListener('submit', handlePayment);

        // Room selection change
        document.getElementById('roomId').addEventListener('change', updateRoomInfo);

        // Real-time calculation for invoice preview
        const calculationInputs = [
            'electricityPrevious', 'electricityCurrent',
            'waterPrevious', 'waterCurrent',
            'serviceFee', 'internetFee', 'parkingFee', 'otherFees', 'discountAmount'
        ];

        calculationInputs.forEach(id => {
            document.getElementById(id).addEventListener('input', updateInvoicePreview);
        });
    }

    // Set default dates
    function setDefaultDates() {
        const today = new Date();
        const nextMonth = new Date(today.getFullYear(), today.getMonth() + 1, 15);

        document.getElementById('issueDate').value = today.toISOString().split('T')[0];
        document.getElementById('dueDate').value = nextMonth.toISOString().split('T')[0];

        // Set current month
        document.getElementById('billingMonth').value = today.getMonth() + 1;
    }

    // Populate customer dropdown
    function populateCustomerDropdown() {
        const select = document.getElementById('customerId');
        select.innerHTML = '<option value="">-- Chọn khách hàng --</option>';

        customers.forEach(customer => {
            select.innerHTML += `
                <option value="${customer.id}">
                    ${customer.fullName} - ${customer.phoneNumber}
                </option>
            `;
        });
    }

    // Populate room dropdown
    function populateRoomDropdown() {
        const select = document.getElementById('roomId');
        select.innerHTML = '<option value="">-- Chọn phòng --</option>';

        rooms.forEach(room => {
            select.innerHTML += `
                <option value="${room.id}" data-price="${room.monthlyPrice}" data-number="${room.roomNumber}">
                    ${room.roomNumber} - ${formatCurrency(room.monthlyPrice)}/tháng
                </option>
            `;
        });
    }

    // Update room info when selected
    function updateRoomInfo() {
        const select = document.getElementById('roomId');
        const selectedOption = select.options[select.selectedIndex];

        if (selectedOption.value) {
            updateInvoicePreview();
        }
    }

    // Update invoice preview calculation
    function updateInvoicePreview() {
        const roomSelect = document.getElementById('roomId');
        const selectedRoom = roomSelect.options[roomSelect.selectedIndex];

        if (!selectedRoom.value) return;

        const monthlyRent = parseFloat(selectedRoom.dataset.price || 0);

        const electricityPrev = parseFloat(document.getElementById('electricityPrevious').value || 0);
        const electricityCurr = parseFloat(document.getElementById('electricityCurrent').value || 0);
        const electricityUsage = Math.max(0, electricityCurr - electricityPrev);
        const electricityAmount = electricityUsage * (currentServiceRates.ELECTRICITY || 3500);

        const waterPrev = parseFloat(document.getElementById('waterPrevious').value || 0);
        const waterCurr = parseFloat(document.getElementById('waterCurrent').value || 0);
        const waterUsage = Math.max(0, waterCurr - waterPrev);
        const waterAmount = waterUsage * (currentServiceRates.WATER || 25000);

        const serviceFee = parseFloat(document.getElementById('serviceFee').value || 0);
        const internetFee = parseFloat(document.getElementById('internetFee').value || 0);
        const parkingFee = parseFloat(document.getElementById('parkingFee').value || 0);
        const otherFees = parseFloat(document.getElementById('otherFees').value || 0);
        const discountAmount = parseFloat(document.getElementById('discountAmount').value || 0);

        const totalServiceFees = serviceFee + internetFee + parkingFee + otherFees;
        const total = monthlyRent + electricityAmount + waterAmount + totalServiceFees - discountAmount;

        // Update preview
        document.getElementById('previewRent').textContent = formatCurrency(monthlyRent);
        document.getElementById('previewElectricity').textContent = formatCurrency(electricityAmount);
        document.getElementById('previewWater').textContent = formatCurrency(waterAmount);
        document.getElementById('previewService').textContent = formatCurrency(totalServiceFees);
        document.getElementById('previewDiscount').textContent = formatCurrency(discountAmount);
        document.getElementById('previewTotal').textContent = formatCurrency(total);
    }

    // Handle create invoice
    async function handleCreateInvoice(e) {
        e.preventDefault();

        const formData = {
            customerId: parseInt(document.getElementById('customerId').value),
            roomId: parseInt(document.getElementById('roomId').value),
            billingPeriodMonth: parseInt(document.getElementById('billingMonth').value),
            billingPeriodYear: parseInt(document.getElementById('billingYear').value),
            issueDate: document.getElementById('issueDate').value,
            dueDate: document.getElementById('dueDate').value,
            electricityPreviousReading: parseFloat(document.getElementById('electricityPrevious').value || 0),
            electricityCurrentReading: parseFloat(document.getElementById('electricityCurrent').value || 0),
            waterPreviousReading: parseFloat(document.getElementById('waterPrevious').value || 0),
            waterCurrentReading: parseFloat(document.getElementById('waterCurrent').value || 0),
            serviceFee: parseFloat(document.getElementById('serviceFee').value || 0),
            internetFee: parseFloat(document.getElementById('internetFee').value || 0),
            parkingFee: parseFloat(document.getElementById('parkingFee').value || 0),
            otherFees: parseFloat(document.getElementById('otherFees').value || 0),
            discountAmount: parseFloat(document.getElementById('discountAmount').value || 0),
            notes: document.getElementById('invoiceNotes').value
        };

        try {
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang tạo...';
            submitBtn.disabled = true;

            await API.createInvoice(formData);

            showAlert('Tạo hóa đơn thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('createInvoiceModal')).hide();

            // Reset form
            document.getElementById('createInvoiceForm').reset();
            setDefaultDates();

            // Reload data
            await Promise.all([loadInvoices(), loadDashboardStats()]);

        } catch (error) {
            console.error('Error creating invoice:', error);
            showAlert('Lỗi khi tạo hóa đơn: ' + error.message, 'danger');
        } finally {
            const submitBtn = e.target.querySelector('button[type="submit"]');
            submitBtn.innerHTML = '<i class="bi bi-check-circle me-1"></i>Tạo hóa đơn';
            submitBtn.disabled = false;
        }
    }

    // View invoice detail
    window.viewInvoiceDetail = async function(invoiceId) {
        try {
            const invoice = await API.getInvoiceById(invoiceId);
            const payments = await API.getPaymentsByInvoiceId(invoiceId);

            const content = `
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="fw-bold text-primary">Thông tin hóa đơn</h6>
                        <table class="table table-sm">
                            <tr><td><strong>Mã hóa đơn:</strong></td><td>${invoice.invoiceNumber || `HD${String(invoice.id).padStart(4, '0')}`}</td></tr>
                            <tr><td><strong>Khách hàng:</strong></td><td>${invoice.customerName}</td></tr>
                            <tr><td><strong>Phòng:</strong></td><td>${invoice.roomNumber}</td></tr>
                            <tr><td><strong>Kỳ thanh toán:</strong></td><td>${invoice.billingPeriodMonth}/${invoice.billingPeriodYear}</td></tr>
                            <tr><td><strong>Ngày lập:</strong></td><td>${formatDate(invoice.issueDate)}</td></tr>
                            <tr><td><strong>Hạn thanh toán:</strong></td><td>${formatDate(invoice.dueDate)}</td></tr>
                            <tr><td><strong>Trạng thái:</strong></td><td>${getStatusBadge(invoice.status)}</td></tr>
                        </table>
                    </div>
                    <div class="col-md-6">
                        <h6 class="fw-bold text-primary">Chi tiết tính toán</h6>
                        <table class="table table-sm">
                            <tr><td>Tiền thuê phòng:</td><td class="text-end"><strong>${formatCurrency(invoice.monthlyRent)}</strong></td></tr>
                            <tr><td>Điện (${invoice.electricityUsage} kWh):</td><td class="text-end">${formatCurrency(invoice.electricityAmount)}</td></tr>
                            <tr><td>Nước (${invoice.waterUsage} m³):</td><td class="text-end">${formatCurrency(invoice.waterAmount)}</td></tr>
                            <tr><td>Phí dịch vụ:</td><td class="text-end">${formatCurrency(invoice.serviceFee)}</td></tr>
                            <tr><td>Phí internet:</td><td class="text-end">${formatCurrency(invoice.internetFee)}</td></tr>
                            <tr><td>Phí gửi xe:</td><td class="text-end">${formatCurrency(invoice.parkingFee)}</td></tr>
                            <tr><td>Phí khác:</td><td class="text-end">${formatCurrency(invoice.otherFees)}</td></tr>
                            <tr><td>Giảm giá:</td><td class="text-end text-danger">-${formatCurrency(invoice.discountAmount)}</td></tr>
                            <tr class="table-dark"><td><strong>Tổng cộng:</strong></td><td class="text-end"><strong>${formatCurrency(invoice.totalAmount)}</strong></td></tr>
                        </table>
                    </div>
                </div>
                
                ${payments.length > 0 ? `
                    <hr>
                    <h6 class="fw-bold text-primary">Lịch sử thanh toán</h6>
                    <div class="table-responsive">
                        <table class="table table-sm table-striped">
                            <thead>
                                <tr>
                                    <th>Ngày thanh toán</th>
                                    <th>Số tiền</th>
                                    <th>Phương thức</th>
                                    <th>Mã tham chiếu</th>
                                    <th>Ghi chú</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${payments.map(payment => `
                                    <tr>
                                        <td>${formatDateTime(payment.paymentDate)}</td>
                                        <td><strong class="text-success">${formatCurrency(payment.amountPaid)}</strong></td>
                                        <td><span class="badge bg-info">${payment.paymentMethod}</span></td>
                                        <td>${payment.referenceNumber || '-'}</td>
                                        <td>${payment.notes || '-'}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                ` : ''}
                
                ${invoice.notes ? `
                    <hr>
                    <h6 class="fw-bold text-primary">Ghi chú</h6>
                    <p class="text-muted">${invoice.notes}</p>
                ` : ''}
            `;

            document.getElementById('invoiceDetailContent').innerHTML = content;

            // Show/hide payment button
            const payBtn = document.getElementById('payInvoiceBtn');
            if (invoice.status === 'PENDING' || invoice.status === 'OVERDUE') {
                payBtn.style.display = 'inline-block';
                payBtn.onclick = () => showPaymentModal(invoiceId);
            } else {
                payBtn.style.display = 'none';
            }

            new bootstrap.Modal(document.getElementById('invoiceDetailModal')).show();

        } catch (error) {
            console.error('Error loading invoice detail:', error);
            showAlert('Lỗi khi tải chi tiết hóa đơn', 'danger');
        }
    };

    // Show payment modal
    window.showPaymentModal = async function(invoiceId) {
        try {
            const invoice = await API.getInvoiceById(invoiceId);
            const totalPaid = await API.getTotalPaidByInvoiceId(invoiceId);
            const remainingAmount = invoice.totalAmount - totalPaid;

            document.getElementById('paymentInvoiceId').value = invoiceId;
            document.getElementById('amountPaid').value = remainingAmount;
            document.getElementById('amountPaid').max = remainingAmount;

            // Hide detail modal if open
            const detailModal = bootstrap.Modal.getInstance(document.getElementById('invoiceDetailModal'));
            if (detailModal) detailModal.hide();

            new bootstrap.Modal(document.getElementById('paymentModal')).show();

        } catch (error) {
            console.error('Error preparing payment:', error);
            showAlert('Lỗi khi chuẩn bị thanh toán', 'danger');
        }
    };

    // Handle payment
    async function handlePayment(e) {
        e.preventDefault();

        const paymentData = {
            invoiceId: parseInt(document.getElementById('paymentInvoiceId').value),
            amountPaid: parseFloat(document.getElementById('amountPaid').value),
            paymentMethod: document.getElementById('paymentMethod').value,
            referenceNumber: document.getElementById('referenceNumber').value,
            notes: document.getElementById('paymentNotes').value,
            createdBy: 'admin'
        };

        try {
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';
            submitBtn.disabled = true;

            await API.processPayment(paymentData);

            showAlert('Thanh toán thành công!', 'success');
            bootstrap.Modal.getInstance(document.getElementById('paymentModal')).hide();

            // Reset form
            document.getElementById('paymentForm').reset();

            // Reload data
            await Promise.all([loadInvoices(), loadDashboardStats()]);

        } catch (error) {
            console.error('Error processing payment:', error);
            showAlert('Lỗi khi xử lý thanh toán: ' + error.message, 'danger');
        } finally {
            const submitBtn = e.target.querySelector('button[type="submit"]');
            submitBtn.innerHTML = '<i class="bi bi-check-circle me-1"></i>Xác nhận thanh toán';
            submitBtn.disabled = false;
        }
    }

    // Update invoice status
    window.updateInvoiceStatus = async function(invoiceId, newStatus) {
        if (!confirm(`Bạn có chắc chắn muốn ${newStatus === 'CANCELLED' ? 'hủy' : 'cập nhật'} hóa đơn này?`)) {
            return;
        }

        try {
            await API.updateInvoiceStatus(invoiceId, newStatus);
            showAlert('Cập nhật trạng thái thành công!', 'success');
            await Promise.all([loadInvoices(), loadDashboardStats()]);
        } catch (error) {
            console.error('Error updating invoice status:', error);
            showAlert('Lỗi khi cập nhật trạng thái hóa đơn', 'danger');
        }
    };

    // Make loadInvoices globally accessible
    window.loadInvoices = loadInvoices;

    // Utility functions
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount || 0);
    }

    function formatDate(dateString) {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('vi-VN');
    }

    function formatDateTime(dateString) {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleString('vi-VN');
    }

    function getStatusBadge(status) {
        const statusConfig = {
            'PENDING': { class: 'bg-warning text-dark', text: 'Chờ thanh toán' },
            'PAID': { class: 'bg-success', text: 'Đã thanh toán' },
            'OVERDUE': { class: 'bg-danger', text: 'Quá hạn' },
            'CANCELLED': { class: 'bg-secondary', text: 'Đã hủy' }
        };

        const config = statusConfig[status] || { class: 'bg-secondary', text: status };
        return `<span class="badge ${config.class}">${config.text}</span>`;
    }

    function isOverdue(dueDate, status) {
        if (status !== 'PENDING') return false;
        return new Date(dueDate) < new Date();
    }

    function showLoading() {
        document.getElementById('invoicesTableBody').innerHTML =
            '<tr><td colspan="9" class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Đang tải...</span></div></td></tr>';
    }

    function showAlert(message, type = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(alertDiv);

        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
});