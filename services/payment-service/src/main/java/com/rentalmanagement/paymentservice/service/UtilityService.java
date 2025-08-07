// service/UtilityService.java
package com.rentalmanagement.paymentservice.service;

import com.rentalmanagement.paymentservice.client.RoomClient;
import com.rentalmanagement.paymentservice.exception.ResourceNotFoundException;
import com.rentalmanagement.paymentservice.model.ServiceRate;
import com.rentalmanagement.paymentservice.model.UtilityReading;
import com.rentalmanagement.paymentservice.model.dto.UtilityReadingRequest;
import com.rentalmanagement.paymentservice.repository.ServiceRateRepository;
import com.rentalmanagement.paymentservice.repository.UtilityReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilityService {

    private final UtilityReadingRepository utilityReadingRepository;
    private final ServiceRateRepository serviceRateRepository;
    private final RoomClient roomClient;

    @Transactional(readOnly = true)
    public List<UtilityReading> getAllReadings() {
        List<UtilityReading> readings = utilityReadingRepository.findAll();
        return readings.stream()
                .map(this::enrichReadingWithRoomInfo)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UtilityReading> getReadingsByRoomId(Long roomId) {
        List<UtilityReading> readings = utilityReadingRepository.findByRoomIdOrderByReadingYearDescReadingMonthDesc(roomId);
        return readings.stream()
                .map(this::enrichReadingWithRoomInfo)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<UtilityReading> getReadingByRoomAndPeriod(Long roomId, Integer year, Integer month) {
        return utilityReadingRepository.findByRoomIdAndReadingYearAndReadingMonth(roomId, year, month)
                .map(this::enrichReadingWithRoomInfo);
    }

    @Transactional(readOnly = true)
    public Optional<UtilityReading> getLatestReadingByRoomId(Long roomId) {
        return utilityReadingRepository.findLatestReadingByRoomId(roomId)
                .map(this::enrichReadingWithRoomInfo);
    }

    @Transactional
    public UtilityReading saveReading(UtilityReadingRequest request) {
        // Kiểm tra xem đã có số đọc cho tháng này chưa
        if (utilityReadingRepository.existsByRoomIdAndReadingYearAndReadingMonth(
                request.getRoomId(), request.getReadingYear(), request.getReadingMonth())) {
            throw new IllegalStateException("Đã có số đọc cho tháng " +
                    request.getReadingMonth() + "/" + request.getReadingYear());
        }

        UtilityReading reading = new UtilityReading();
        reading.setRoomId(request.getRoomId());
        reading.setReadingMonth(request.getReadingMonth());
        reading.setReadingYear(request.getReadingYear());
        reading.setElectricityReading(request.getElectricityReading());
        reading.setWaterReading(request.getWaterReading());
        reading.setReadingDate(request.getReadingDate());
        reading.setRecordedBy(request.getRecordedBy());
        reading.setNotes(request.getNotes());

        UtilityReading savedReading = utilityReadingRepository.save(reading);
        return enrichReadingWithRoomInfo(savedReading);
    }

    @Transactional
    public UtilityReading updateReading(Long id, UtilityReadingRequest request) {
        UtilityReading reading = utilityReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy số đọc với ID: " + id));

        reading.setElectricityReading(request.getElectricityReading());
        reading.setWaterReading(request.getWaterReading());
        reading.setReadingDate(request.getReadingDate());
        reading.setRecordedBy(request.getRecordedBy());
        reading.setNotes(request.getNotes());

        UtilityReading updatedReading = utilityReadingRepository.save(reading);
        return enrichReadingWithRoomInfo(updatedReading);
    }

    @Transactional
    public void deleteReading(Long id) {
        UtilityReading reading = utilityReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy số đọc với ID: " + id));

        utilityReadingRepository.delete(reading);
    }

    // Service Rate methods
    @Transactional(readOnly = true)
    public List<ServiceRate> getAllServiceRates() {
        return serviceRateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceRate> getCurrentServiceRates() {
        return serviceRateRepository.findAllCurrentRates(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Optional<ServiceRate> getCurrentRateByServiceType(ServiceRate.ServiceType serviceType) {
        return serviceRateRepository.findCurrentRateByServiceType(serviceType, LocalDate.now());
    }

    @Transactional
    public ServiceRate saveServiceRate(ServiceRate serviceRate) {
        return serviceRateRepository.save(serviceRate);
    }

    // Helper method
    private UtilityReading enrichReadingWithRoomInfo(UtilityReading reading) {
        try {
            RoomClient.Room room = roomClient.getRoomById(reading.getRoomId()).getBody();
            if (room != null) {
                reading.setRoomNumber(room.getRoomNumber());
            }
        } catch (Exception e) {
            log.warn("Cannot get room info for reading {}", reading.getId());
        }
        return reading;
    }
}