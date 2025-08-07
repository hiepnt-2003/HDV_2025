// service/InvoiceService.java
package com.rentalmanagement.paymentservice.service;

import com.rentalmanagement.paymentservice.client.CustomerClient;
import com.rentalmanagement.paymentservice.client.RoomClient;
import com.rentalmanagement.paymentservice.client.BookingClient;
import com.rentalmanagement.paymentservice.exception.ResourceNotFoundException;
import com.rentalmanagement.paymentservice.model.Invoice;
import com.rentalmanagement.paymentservice.model.ServiceRate;
import com.rentalmanagement.paymentservice.model.UtilityReading;
import com.rentalmanagement.paymentservice.model.dto.InvoiceCreateRequest;
import com.rentalmanagement.paymentservice.model.dto.InvoiceResponse;
import com.rentalmanagement.paymentservice.repository.InvoiceRepository;
import com.rentalmanagement.paymentservice.repository.ServiceRateRepository;
import com.rentalmanagement.paymentservice.repository.UtilityReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ServiceRateRepository serviceRateRepository;
    private final UtilityReadingRepository utilityReadingRepository;
    private final CustomerClient customerClient;
    private final RoomClient roomClient;
    private final BookingClient bookingClient;

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
        return InvoiceResponse.fromEntity(enrichInvoiceWithDetails(invoice));
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomerId(Long customerId) {
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
        return invoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByRoomId(Long roomId) {
        List<Invoice> invoices = invoiceRepository.findByRoomId(roomId);
        return invoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByStatus(status);
        return invoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByPeriod(Integer year, Integer month) {
        List<Invoice> invoices = invoiceRepository.findByBillingPeriodYearAndBillingPeriodMonth(year, month);
        return invoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        // Kiểm tra xem đã có hóa đơn cho phòng này trong tháng chưa
        Optional<Invoice> existingInvoice = invoiceRepository.findByRoomIdAndBillingPeriodYearAndBillingPeriodMonth(
                request.getRoomId(), request.getBillingPeriodYear(), request.getBillingPeriodMonth());

        if (existingInvoice.isPresent()) {
            throw new IllegalStateException("Đã có hóa đơn cho phòng này trong tháng " +
                    request.getBillingPeriodMonth() + "/" + request.getBillingPeriodYear());
        }

        // Lấy thông tin khách hàng và phòng
        CustomerClient.Customer customer = getCustomerInfo(request.getCustomerId());
        RoomClient.Room room = getRoomInfo(request.getRoomId());

        // Tạo hóa đơn mới
        Invoice invoice = new Invoice();
        invoice.setCustomerId(request.getCustomerId());
        invoice.setRoomId(request.getRoomId());
        invoice.setCheckInId(request.getCheckInId());
        invoice.setBillingPeriodMonth(request.getBillingPeriodMonth());
        invoice.setBillingPeriodYear(request.getBillingPeriodYear());
        invoice.setIssueDate(request.getIssueDate());
        invoice.setDueDate(request.getDueDate());

        // Thông tin phòng
        invoice.setRoomNumber(room.getRoomNumber());
        invoice.setMonthlyRent(room.getMonthlyPrice());

        // Thông tin điện nước
        invoice.setElectricityPreviousReading(request.getElectricityPreviousReading());
        invoice.setElectricityCurrentReading(request.getElectricityCurrentReading());
        invoice.setWaterPreviousReading(request.getWaterPreviousReading());
        invoice.setWaterCurrentReading(request.getWaterCurrentReading());

        // Lấy giá điện nước hiện tại
        setCurrentServiceRates(invoice);

        // Các khoản phí
        invoice.setServiceFee(request.getServiceFee());
        invoice.setInternetFee(request.getInternetFee());
        invoice.setParkingFee(request.getParkingFee());
        invoice.setOtherFees(request.getOtherFees());
        invoice.setOtherFeesDescription(request.getOtherFeesDescription());
        invoice.setDiscountAmount(request.getDiscountAmount());

        invoice.setNotes(request.getNotes());
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Lưu số đọc điện nước vào bảng utility_readings
        saveUtilityReadings(request);

        return InvoiceResponse.fromEntity(enrichInvoiceWithDetails(savedInvoice));
    }

    @Transactional
    public InvoiceResponse updateInvoiceStatus(Long id, Invoice.InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));

        invoice.setStatus(status);
        if (status == Invoice.InvoiceStatus.PAID) {
            invoice.setPaymentDate(java.time.LocalDateTime.now());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponse.fromEntity(enrichInvoiceWithDetails(updatedInvoice));
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Không thể xóa hóa đơn đã thanh toán");
        }

        invoiceRepository.delete(invoice);
    }

    // Tự động tạo hóa đơn cho tất cả phòng đang thuê trong tháng
    @Transactional
    public List<InvoiceResponse> generateMonthlyInvoices(Integer year, Integer month) {
        // Lấy danh sách tất cả check-in đang hoạt động
        // Implementaion này cần được gọi từ controller với logic phù hợp
        // Ở đây chỉ là ví dụ cơ bản
        return List.of(); // Placeholder
    }

    // Helper methods
    private CustomerClient.Customer getCustomerInfo(Long customerId) {
        try {
            return customerClient.getCustomerById(customerId).getBody();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin khách hàng với ID: " + customerId);
        }
    }

    private RoomClient.Room getRoomInfo(Long roomId) {
        try {
            return roomClient.getRoomById(roomId).getBody();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin phòng với ID: " + roomId);
        }
    }

    private void setCurrentServiceRates(Invoice invoice) {
        LocalDate currentDate = LocalDate.now();

        // Lấy giá điện hiện tại
        serviceRateRepository.findCurrentRateByServiceType(ServiceRate.ServiceType.ELECTRICITY, currentDate)
                .ifPresent(rate -> invoice.setElectricityUnitPrice(rate.getRate()));

        // Lấy giá nước hiện tại
        serviceRateRepository.findCurrentRateByServiceType(ServiceRate.ServiceType.WATER, currentDate)
                .ifPresent(rate -> invoice.setWaterUnitPrice(rate.getRate()));

        // Lấy phí internet hiện tại
        serviceRateRepository.findCurrentRateByServiceType(ServiceRate.ServiceType.INTERNET, currentDate)
                .ifPresent(rate -> invoice.setInternetFee(rate.getRate()));
    }

    private void saveUtilityReadings(InvoiceCreateRequest request) {
        // Lưu số điện nước mới nhất
        UtilityReading reading = new UtilityReading();
        reading.setRoomId(request.getRoomId());
        reading.setReadingMonth(request.getBillingPeriodMonth());
        reading.setReadingYear(request.getBillingPeriodYear());
        reading.setElectricityReading(request.getElectricityCurrentReading());
        reading.setWaterReading(request.getWaterCurrentReading());
        reading.setReadingDate(LocalDate.now());

        // Chỉ lưu nếu chưa có số đọc cho tháng này
        if (!utilityReadingRepository.existsByRoomIdAndReadingYearAndReadingMonth(
                request.getRoomId(), request.getBillingPeriodYear(), request.getBillingPeriodMonth())) {
            utilityReadingRepository.save(reading);
        }
    }

    private Invoice enrichInvoiceWithDetails(Invoice invoice) {
        try {
            // Lấy thông tin khách hàng
            CustomerClient.Customer customer = customerClient.getCustomerById(invoice.getCustomerId()).getBody();
            if (customer != null) {
                invoice.setCustomerName(customer.getFullName());
            }
        } catch (Exception e) {
            log.warn("Cannot get customer info for invoice {}", invoice.getId());
        }

        return invoice;
    }

    // Thêm các methods này vào cuối class InvoiceService (trước closing brace)

    // Thống kê doanh thu theo tháng
    @Transactional(readOnly = true)
    public Double getRevenueByMonth(Integer year, Integer month) {
        List<Invoice> invoices = invoiceRepository.findByBillingPeriodYearAndBillingPeriodMonth(year, month);

        return invoices.stream()
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.PAID)
                .mapToDouble(invoice -> invoice.getTotalAmount().doubleValue())
                .sum();
    }

    // Thống kê doanh thu theo năm
    @Transactional(readOnly = true)
    public Double getRevenueByYear(Integer year) {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        return allInvoices.stream()
                .filter(invoice -> invoice.getBillingPeriodYear().equals(year))
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.PAID)
                .mapToDouble(invoice -> invoice.getTotalAmount().doubleValue())
                .sum();
    }

    // Thống kê hóa đơn quá hạn
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        return overdueInvoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Thống kê hóa đơn sắp đến hạn (trong 7 ngày)
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesDueSoon() {
        LocalDate now = LocalDate.now();
        LocalDate dueDate = now.plusDays(7);
        List<Invoice> dueSoonInvoices = invoiceRepository.findInvoicesDueSoon(now, dueDate);
        return dueSoonInvoices.stream()
                .map(this::enrichInvoiceWithDetails)
                .map(InvoiceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Thống kê tổng quan
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Tổng số hóa đơn
        long totalInvoices = invoiceRepository.count();
        stats.put("totalInvoices", totalInvoices);

        // Hóa đơn chưa thanh toán
        List<Invoice> pendingInvoices = invoiceRepository.findByStatus(Invoice.InvoiceStatus.PENDING);
        stats.put("pendingInvoicesCount", pendingInvoices.size());

        // Tổng tiền chưa thu
        double pendingAmount = pendingInvoices.stream()
                .mapToDouble(invoice -> invoice.getTotalAmount().doubleValue())
                .sum();
        stats.put("pendingAmount", pendingAmount);

        // Hóa đơn đã thanh toán trong tháng
        LocalDate now = LocalDate.now();
        double monthlyRevenue = getRevenueByMonth(now.getYear(), now.getMonthValue());
        stats.put("monthlyRevenue", monthlyRevenue);

        // Hóa đơn quá hạn
        List<Invoice> overdue = invoiceRepository.findOverdueInvoices(now);
        stats.put("overdueCount", overdue.size());

        return stats;
    }
}