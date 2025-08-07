// model/dto/UtilityReadingRequest.java
package com.rentalmanagement.paymentservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UtilityReadingRequest {
    @NotNull(message = "ID phòng không được để trống")
    private Long roomId;

    @NotNull(message = "Tháng ghi số không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1 đến 12")
    @Max(value = 12, message = "Tháng phải từ 1 đến 12")
    private Integer readingMonth;

    @NotNull(message = "Năm ghi số không được để trống")
    @Min(value = 2020, message = "Năm phải lớn hơn 2020")
    private Integer readingYear;

    @DecimalMin(value = "0.0", message = "Số điện phải lớn hơn hoặc bằng 0")
    private BigDecimal electricityReading = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Số nước phải lớn hơn hoặc bằng 0")
    private BigDecimal waterReading = BigDecimal.ZERO;

    private LocalDate readingDate = LocalDate.now();
    private String recordedBy;
    private String notes;
}