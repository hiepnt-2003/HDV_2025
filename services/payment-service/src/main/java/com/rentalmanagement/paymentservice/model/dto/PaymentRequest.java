// model/dto/PaymentRequest.java
package com.rentalmanagement.paymentservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRequest {
    @NotNull(message = "ID hóa đơn không được để trống")
    private Long invoiceId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền thanh toán phải lớn hơn 0")
    private BigDecimal amountPaid;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private LocalDateTime paymentDate = LocalDateTime.now();
    private String referenceNumber;
    private String notes;
    private String createdBy;
}