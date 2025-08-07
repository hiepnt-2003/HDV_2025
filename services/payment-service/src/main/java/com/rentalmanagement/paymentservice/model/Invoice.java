// model/Invoice.java
package com.rentalmanagement.paymentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, length = 20)
    private String invoiceNumber;

    @NotNull(message = "ID khách hàng không được để trống")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Thông tin khách hàng (từ customer service)
    @Transient
    private String customerName;

    @NotNull(message = "ID phòng không được để trống")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "check_in_id")
    private Long checkInId;

    // Thông tin thời gian
    @NotNull(message = "Tháng thanh toán không được để trống")
    @Column(name = "billing_period_month", nullable = false)
    private Integer billingPeriodMonth;

    @NotNull(message = "Năm thanh toán không được để trống")
    @Column(name = "billing_period_year", nullable = false)
    private Integer billingPeriodYear;

    @NotNull(message = "Ngày lập hóa đơn không được để trống")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull(message = "Ngày đến hạn không được để trống")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Thông tin phòng
    @Column(name = "room_number", nullable = false, length = 10)
    private String roomNumber;

    @NotNull(message = "Tiền thuê phòng không được để trống")
    @DecimalMin(value = "0.0", message = "Tiền thuê phòng phải lớn hơn hoặc bằng 0")
    @Column(name = "monthly_rent", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyRent;

    // Thông tin điện
    @Column(name = "electricity_previous_reading", precision = 10, scale = 2)
    private BigDecimal electricityPreviousReading = BigDecimal.ZERO;

    @Column(name = "electricity_current_reading", precision = 10, scale = 2)
    private BigDecimal electricityCurrentReading = BigDecimal.ZERO;

    @Column(name = "electricity_unit_price", precision = 10, scale = 2)
    private BigDecimal electricityUnitPrice = new BigDecimal("3500");

    // Thông tin nước
    @Column(name = "water_previous_reading", precision = 10, scale = 2)
    private BigDecimal waterPreviousReading = BigDecimal.ZERO;

    @Column(name = "water_current_reading", precision = 10, scale = 2)
    private BigDecimal waterCurrentReading = BigDecimal.ZERO;

    @Column(name = "water_unit_price", precision = 10, scale = 2)
    private BigDecimal waterUnitPrice = new BigDecimal("25000");

    // Các khoản phí khác
    @Column(name = "service_fee", precision = 15, scale = 2)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "internet_fee", precision = 15, scale = 2)
    private BigDecimal internetFee = new BigDecimal("150000");

    @Column(name = "parking_fee", precision = 15, scale = 2)
    private BigDecimal parkingFee = BigDecimal.ZERO;

    @Column(name = "other_fees", precision = 15, scale = 2)
    private BigDecimal otherFees = BigDecimal.ZERO;

    @Column(name = "other_fees_description")
    private String otherFeesDescription;

    // Giảm giá
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    // Trạng thái thanh toán
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum InvoiceStatus {
        PENDING, PAID, OVERDUE, CANCELLED
    }

    // Các phương thức tính toán
    public BigDecimal getElectricityUsage() {
        if (electricityCurrentReading == null || electricityPreviousReading == null) {
            return BigDecimal.ZERO;
        }
        return electricityCurrentReading.subtract(electricityPreviousReading);
    }

    public BigDecimal getElectricityAmount() {
        return getElectricityUsage().multiply(electricityUnitPrice != null ? electricityUnitPrice : BigDecimal.ZERO);
    }

    public BigDecimal getWaterUsage() {
        if (waterCurrentReading == null || waterPreviousReading == null) {
            return BigDecimal.ZERO;
        }
        return waterCurrentReading.subtract(waterPreviousReading);
    }

    public BigDecimal getWaterAmount() {
        return getWaterUsage().multiply(waterUnitPrice != null ? waterUnitPrice : BigDecimal.ZERO);
    }

    public BigDecimal getSubtotal() {
        BigDecimal total = monthlyRent != null ? monthlyRent : BigDecimal.ZERO;
        total = total.add(getElectricityAmount());
        total = total.add(getWaterAmount());
        total = total.add(serviceFee != null ? serviceFee : BigDecimal.ZERO);
        total = total.add(internetFee != null ? internetFee : BigDecimal.ZERO);
        total = total.add(parkingFee != null ? parkingFee : BigDecimal.ZERO);
        total = total.add(otherFees != null ? otherFees : BigDecimal.ZERO);
        return total;
    }

    public BigDecimal getTotalAmount() {
        return getSubtotal().subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }
}