// model/dto/InvoiceCreateRequest.java
package com.rentalmanagement.paymentservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceCreateRequest {
    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "ID phòng không được để trống")
    private Long roomId;

    private Long checkInId;

    @NotNull(message = "Tháng thanh toán không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1 đến 12")
    @Max(value = 12, message = "Tháng phải từ 1 đến 12")
    private Integer billingPeriodMonth;

    @NotNull(message = "Năm thanh toán không được để trống")
    @Min(value = 2020, message = "Năm phải lớn hơn 2020")
    private Integer billingPeriodYear;

    private LocalDate issueDate = LocalDate.now();
    private LocalDate dueDate = LocalDate.now().plusDays(15);

    // Số điện nước
    @DecimalMin(value = "0.0", message = "Số điện cũ phải lớn hơn hoặc bằng 0")
    private BigDecimal electricityPreviousReading = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Số điện mới phải lớn hơn hoặc bằng 0")
    private BigDecimal electricityCurrentReading = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Số nước cũ phải lớn hơn hoặc bằng 0")
    private BigDecimal waterPreviousReading = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Số nước mới phải lớn hơn hoặc bằng 0")
    private BigDecimal waterCurrentReading = BigDecimal.ZERO;

    // Các khoản phí
    @DecimalMin(value = "0.0", message = "Phí dịch vụ phải lớn hơn hoặc bằng 0")
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Phí internet phải lớn hơn hoặc bằng 0")
    private BigDecimal internetFee = new BigDecimal("150000");

    @DecimalMin(value = "0.0", message = "Phí gửi xe phải lớn hơn hoặc bằng 0")
    private BigDecimal parkingFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Phí khác phải lớn hơn hoặc bằng 0")
    private BigDecimal otherFees = BigDecimal.ZERO;

    private String otherFeesDescription;

    @DecimalMin(value = "0.0", message = "Số tiền giảm giá phải lớn hơn hoặc bằng 0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String notes;
}