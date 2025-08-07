// repository/UtilityReadingRepository.java
package com.rentalmanagement.paymentservice.repository;

import com.rentalmanagement.paymentservice.model.UtilityReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityReadingRepository extends JpaRepository<UtilityReading, Long> {

    // Lấy số đọc theo phòng và tháng
    Optional<UtilityReading> findByRoomIdAndReadingYearAndReadingMonth(
            Long roomId, Integer year, Integer month);

    // Lấy tất cả số đọc của một phòng
    List<UtilityReading> findByRoomIdOrderByReadingYearDescReadingMonthDesc(Long roomId);

    // Lấy số đọc tháng trước
    @Query("SELECT ur FROM UtilityReading ur WHERE ur.roomId = :roomId " +
            "AND ((ur.readingYear = :year AND ur.readingMonth = :month - 1) " +
            "OR (ur.readingYear = :year - 1 AND ur.readingMonth = 12 AND :month = 1))")
    Optional<UtilityReading> findPreviousReading(Long roomId, Integer year, Integer month);

    // Lấy số đọc mới nhất của phòng
    @Query("SELECT ur FROM UtilityReading ur WHERE ur.roomId = :roomId " +
            "ORDER BY ur.readingYear DESC, ur.readingMonth DESC")
    Optional<UtilityReading> findLatestReadingByRoomId(Long roomId);

    // Lấy tất cả số đọc theo tháng
    List<UtilityReading> findByReadingYearAndReadingMonth(Integer year, Integer month);

    // Kiểm tra xem đã có số đọc cho phòng trong tháng chưa
    boolean existsByRoomIdAndReadingYearAndReadingMonth(Long roomId, Integer year, Integer month);
}