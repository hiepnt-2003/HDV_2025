// repository/PaymentHistoryRepository.java
package com.rentalmanagement.paymentservice.repository;

import com.rentalmanagement.paymentservice.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    // Lấy lịch sử thanh toán theo hóa đơn
    List<PaymentHistory> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId);

    // Lấy lịch sử thanh toán theo phương thức thanh toán
    List<PaymentHistory> findByPaymentMethod(String paymentMethod);

    // Lấy lịch sử thanh toán theo khoảng thời gian
    List<PaymentHistory> findByPaymentDateBetweenOrderByPaymentDateDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    // Thống kê tổng tiền đã thanh toán theo hóa đơn
    @Query("SELECT COALESCE(SUM(ph.amountPaid), 0) FROM PaymentHistory ph WHERE ph.invoiceId = :invoiceId")
    Double getTotalPaidByInvoiceId(Long invoiceId);

    // Thống kê thanh toán theo phương thức trong khoảng thời gian
    @Query("SELECT ph.paymentMethod, COALESCE(SUM(ph.amountPaid), 0) " +
            "FROM PaymentHistory ph " +
            "WHERE ph.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ph.paymentMethod")
    List<Object[]> getPaymentStatsByMethod(LocalDateTime startDate, LocalDateTime endDate);
}