// repository/InvoiceRepository.java - VERSION ĐÃ LOẠI BỎ TẤT CẢ QUERY PHỨC TẠP
package com.rentalmanagement.paymentservice.repository;

import com.rentalmanagement.paymentservice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Tìm hóa đơn theo khách hàng
    List<Invoice> findByCustomerId(Long customerId);

    // Tìm hóa đơn theo phòng
    List<Invoice> findByRoomId(Long roomId);

    // Tìm hóa đơn theo check-in
    List<Invoice> findByCheckInId(Long checkInId);

    // Tìm hóa đơn theo trạng thái
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    // Tìm hóa đơn theo kỳ thanh toán
    List<Invoice> findByBillingPeriodYearAndBillingPeriodMonth(Integer year, Integer month);

    // Tìm hóa đơn theo khách hàng và kỳ thanh toán
    Optional<Invoice> findByCustomerIdAndBillingPeriodYearAndBillingPeriodMonth(
            Long customerId, Integer year, Integer month);

    // Tìm hóa đơn theo phòng và kỳ thanh toán
    Optional<Invoice> findByRoomIdAndBillingPeriodYearAndBillingPeriodMonth(
            Long roomId, Integer year, Integer month);

    // Tìm hóa đơn quá hạn - QUERY ĐƠN GIẢN
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(LocalDate currentDate);

    // Tìm hóa đơn sắp đến hạn - QUERY ĐƠN GIẢN
    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :startDate AND :endDate AND i.status = 'PENDING'")
    List<Invoice> findInvoicesDueSoon(LocalDate startDate, LocalDate endDate);

    // BỎ TẤT CẢ QUERY THỐNG KÊ PHỨC TẠP - SẼ TÍNH TRONG SERVICE
}