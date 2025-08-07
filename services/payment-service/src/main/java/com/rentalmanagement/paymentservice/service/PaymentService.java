// service/PaymentService.java
package com.rentalmanagement.paymentservice.service;

import com.rentalmanagement.paymentservice.exception.ResourceNotFoundException;
import com.rentalmanagement.paymentservice.model.Invoice;
import com.rentalmanagement.paymentservice.model.PaymentHistory;
import com.rentalmanagement.paymentservice.model.dto.PaymentRequest;
import com.rentalmanagement.paymentservice.repository.InvoiceRepository;
import com.rentalmanagement.paymentservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional(readOnly = true)
    public List<PaymentHistory> getAllPayments() {
        return paymentHistoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PaymentHistory> getPaymentsByInvoiceId(Long invoiceId) {
        return paymentHistoryRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId);
    }

    @Transactional(readOnly = true)
    public List<PaymentHistory> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentHistoryRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(startDate, endDate);
    }

    @Transactional
    public PaymentHistory processPayment(PaymentRequest request) {
        // Lấy thông tin hóa đơn
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + request.getInvoiceId()));

        // Kiểm tra trạng thái hóa đơn
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Hóa đơn đã được thanh toán");
        }

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new IllegalStateException("Hóa đơn đã bị hủy");
        }

        // Tính tổng số tiền đã thanh toán
        Double totalPaid = paymentHistoryRepository.getTotalPaidByInvoiceId(request.getInvoiceId());
        if (totalPaid == null) totalPaid = 0.0;

        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(BigDecimal.valueOf(totalPaid));

        // Kiểm tra số tiền thanh toán
        if (request.getAmountPaid().compareTo(remainingAmount) > 0) {
            throw new IllegalStateException("Số tiền thanh toán vượt quá số tiền còn lại");
        }

        // Tạo bản ghi thanh toán
        PaymentHistory payment = new PaymentHistory();
        payment.setInvoiceId(request.getInvoiceId());
        payment.setAmountPaid(request.getAmountPaid());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setNotes(request.getNotes());
        payment.setCreatedBy(request.getCreatedBy());

        PaymentHistory savedPayment = paymentHistoryRepository.save(payment);

        // Cập nhật trạng thái hóa đơn nếu đã thanh toán đủ
        BigDecimal newTotalPaid = BigDecimal.valueOf(totalPaid).add(request.getAmountPaid());
        if (newTotalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoice.setPaymentDate(request.getPaymentDate());
            invoice.setPaymentMethod(request.getPaymentMethod());
            invoiceRepository.save(invoice);
        }

        return savedPayment;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidByInvoiceId(Long invoiceId) {
        Double totalPaid = paymentHistoryRepository.getTotalPaidByInvoiceId(invoiceId);
        return BigDecimal.valueOf(totalPaid != null ? totalPaid : 0.0);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPaymentStatsByMethod(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentHistoryRepository.getPaymentStatsByMethod(startDate, endDate);
    }
}