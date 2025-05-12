package com.rentalmanagement.bookingservice.controller;

import com.rentalmanagement.bookingservice.model.CheckIn;
import com.rentalmanagement.bookingservice.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/check-ins")
@RequiredArgsConstructor
public class CheckInController {
    private final CheckInService checkInService;

    @GetMapping
    public ResponseEntity<List<CheckIn>> getAllCheckIns() {
        return ResponseEntity.ok(checkInService.getAllCheckIns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> getCheckInById(@PathVariable Long id) {
        return ResponseEntity.ok(checkInService.getCheckInById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CheckIn>> getCheckInsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(checkInService.getCheckInsByCustomerId(customerId));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<CheckIn>> getCheckInsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(checkInService.getCheckInsByRoomId(roomId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<CheckIn> getCheckInByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(checkInService.getCheckInByBookingId(bookingId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CheckIn>> getCheckInsByStatus(@PathVariable CheckIn.CheckInStatus status) {
        return ResponseEntity.ok(checkInService.getCheckInsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<CheckIn> createCheckIn(@Valid @RequestBody CheckIn checkIn) {
        return new ResponseEntity<>(checkInService.createCheckIn(checkIn), HttpStatus.CREATED);
    }

    @PostMapping("/from-booking/{bookingId}")
    public ResponseEntity<CheckIn> createCheckInFromBooking(@PathVariable Long bookingId) {
        return new ResponseEntity<>(checkInService.createCheckInFromBooking(bookingId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckIn> updateCheckIn(@PathVariable Long id, @Valid @RequestBody CheckIn checkIn) {
        return ResponseEntity.ok(checkInService.updateCheckIn(id, checkIn));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CheckIn> updateCheckInStatus(@PathVariable Long id, @RequestBody CheckIn.CheckInStatus status) {
        return ResponseEntity.ok(checkInService.updateCheckInStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckIn(@PathVariable Long id) {
        checkInService.deleteCheckIn(id);
        return ResponseEntity.noContent().build();
    }
}