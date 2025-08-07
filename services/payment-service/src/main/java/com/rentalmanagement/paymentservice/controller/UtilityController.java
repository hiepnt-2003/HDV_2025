// controller/UtilityController.java
package com.rentalmanagement.paymentservice.controller;

import com.rentalmanagement.paymentservice.model.ServiceRate;
import com.rentalmanagement.paymentservice.model.UtilityReading;
import com.rentalmanagement.paymentservice.model.dto.UtilityReadingRequest;
import com.rentalmanagement.paymentservice.service.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
public class UtilityController {

    private final UtilityService utilityService;

    // Utility Readings endpoints
    @GetMapping("/readings")
    public ResponseEntity<List<UtilityReading>> getAllReadings() {
        return ResponseEntity.ok(utilityService.getAllReadings());
    }

    @GetMapping("/readings/room/{roomId}")
    public ResponseEntity<List<UtilityReading>> getReadingsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(utilityService.getReadingsByRoomId(roomId));
    }

    @GetMapping("/readings/room/{roomId}/period")
    public ResponseEntity<UtilityReading> getReadingByRoomAndPeriod(
            @PathVariable Long roomId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Optional<UtilityReading> reading = utilityService.getReadingByRoomAndPeriod(roomId, year, month);
        return reading.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/readings/room/{roomId}/latest")
    public ResponseEntity<UtilityReading> getLatestReadingByRoomId(@PathVariable Long roomId) {
        Optional<UtilityReading> reading = utilityService.getLatestReadingByRoomId(roomId);
        return reading.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/readings")
    public ResponseEntity<UtilityReading> saveReading(@Valid @RequestBody UtilityReadingRequest request) {
        return new ResponseEntity<>(utilityService.saveReading(request), HttpStatus.CREATED);
    }

    @PutMapping("/readings/{id}")
    public ResponseEntity<UtilityReading> updateReading(
            @PathVariable Long id,
            @Valid @RequestBody UtilityReadingRequest request) {
        return ResponseEntity.ok(utilityService.updateReading(id, request));
    }

    @DeleteMapping("/readings/{id}")
    public ResponseEntity<Void> deleteReading(@PathVariable Long id) {
        utilityService.deleteReading(id);
        return ResponseEntity.noContent().build();
    }

    // Service Rates endpoints
    @GetMapping("/rates")
    public ResponseEntity<List<ServiceRate>> getAllServiceRates() {
        return ResponseEntity.ok(utilityService.getAllServiceRates());
    }

    @GetMapping("/rates/current")
    public ResponseEntity<List<ServiceRate>> getCurrentServiceRates() {
        return ResponseEntity.ok(utilityService.getCurrentServiceRates());
    }

    @GetMapping("/rates/current/{serviceType}")
    public ResponseEntity<ServiceRate> getCurrentRateByServiceType(
            @PathVariable ServiceRate.ServiceType serviceType) {
        Optional<ServiceRate> rate = utilityService.getCurrentRateByServiceType(serviceType);
        return rate.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/rates")
    public ResponseEntity<ServiceRate> saveServiceRate(@Valid @RequestBody ServiceRate serviceRate) {
        return new ResponseEntity<>(utilityService.saveServiceRate(serviceRate), HttpStatus.CREATED);
    }
}