package com.rentalmanagement.bookingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_ins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "ID khách hàng không được để trống")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Thêm trường để lưu tên khách hàng để hiển thị
    @Transient
    private String customerName;

    @NotNull(message = "ID phòng không được để trống")
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // Thêm trường để lưu số phòng để hiển thị
    @Transient
    private String roomNumber;

    @NotNull(message = "Ngày nhận phòng không được để trống")
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "expected_check_out_date")
    private LocalDate expectedCheckOutDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CheckInStatus status = CheckInStatus.ACTIVE;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CheckInStatus {
        ACTIVE, CHECKED_OUT
    }
}