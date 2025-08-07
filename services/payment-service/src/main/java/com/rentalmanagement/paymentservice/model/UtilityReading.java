// model/UtilityReading.java
package com.rentalmanagement.paymentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "utility_readings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "reading_year", "reading_month"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilityReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "ID phòng không được để trống")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // Thông tin phòng (từ room service)
    @Transient
    private String roomNumber;

    @NotNull(message = "Tháng ghi số không được để trống")
    @Column(name = "reading_month", nullable = false)
    private Integer readingMonth;

    @NotNull(message = "Năm ghi số không được để trống")
    @Column(name = "reading_year", nullable = false)
    private Integer readingYear;

    @DecimalMin(value = "0.0", message = "Số điện phải lớn hơn hoặc bằng 0")
    @Column(name = "electricity_reading", precision = 10, scale = 2)
    private BigDecimal electricityReading = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Số nước phải lớn hơn hoặc bằng 0")
    @Column(name = "water_reading", precision = 10, scale = 2)
    private BigDecimal waterReading = BigDecimal.ZERO;

    @NotNull(message = "Ngày ghi số không được để trống")
    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "recorded_by", length = 100)
    private String recordedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}