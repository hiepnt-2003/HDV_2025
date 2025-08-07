// client/BookingClient.java
package com.rentalmanagement.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/api/check-ins/{id}")
    ResponseEntity<CheckIn> getCheckInById(@PathVariable Long id);

    @GetMapping("/api/check-ins/room/{roomId}")
    ResponseEntity<java.util.List<CheckIn>> getCheckInsByRoomId(@PathVariable Long roomId);

    // Lớp tĩnh đại diện cho CheckIn model từ booking-service
    class CheckIn {
        private Long id;
        private Long bookingId;
        private Long customerId;
        private String customerName;
        private Long roomId;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate expectedCheckOutDate;
        private CheckInStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public enum CheckInStatus {
            ACTIVE, CHECKED_OUT
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public LocalDate getCheckInDate() { return checkInDate; }
        public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
        public LocalDate getExpectedCheckOutDate() { return expectedCheckOutDate; }
        public void setExpectedCheckOutDate(LocalDate expectedCheckOutDate) { this.expectedCheckOutDate = expectedCheckOutDate; }
        public CheckInStatus getStatus() { return status; }
        public void setStatus(CheckInStatus status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}