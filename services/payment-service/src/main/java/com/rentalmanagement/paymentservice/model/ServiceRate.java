// model/ServiceRate.java
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
@Table(name = "service_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Loại dịch vụ không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @NotNull(message = "Giá dịch vụ không được để trống")
    @DecimalMin(value = "0.0", message = "Giá dịch vụ phải lớn hơn hoặc bằng 0")
    @Column(name = "rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    @NotNull(message = "Đơn vị không được để trống")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Ngày hiệu lực không được để trống")
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ServiceType {
        ELECTRICITY, WATER, INTERNET, SERVICE, PARKING
    }
}