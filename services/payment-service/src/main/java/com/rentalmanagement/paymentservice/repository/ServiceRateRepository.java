// repository/ServiceRateRepository.java
package com.rentalmanagement.paymentservice.repository;

import com.rentalmanagement.paymentservice.model.ServiceRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRateRepository extends JpaRepository<ServiceRate, Long> {

    // Lấy giá dịch vụ hiện tại theo loại
    @Query("SELECT sr FROM ServiceRate sr WHERE sr.serviceType = :serviceType " +
            "AND sr.isActive = true AND sr.effectiveDate <= :currentDate " +
            "ORDER BY sr.effectiveDate DESC")
    Optional<ServiceRate> findCurrentRateByServiceType(
            ServiceRate.ServiceType serviceType, LocalDate currentDate);

    // Lấy tất cả giá dịch vụ hiện tại
    @Query("SELECT sr FROM ServiceRate sr WHERE sr.isActive = true " +
            "AND sr.effectiveDate <= :currentDate " +
            "AND NOT EXISTS (SELECT sr2 FROM ServiceRate sr2 " +
            "WHERE sr2.serviceType = sr.serviceType AND sr2.isActive = true " +
            "AND sr2.effectiveDate > sr.effectiveDate AND sr2.effectiveDate <= :currentDate)")
    List<ServiceRate> findAllCurrentRates(LocalDate currentDate);

    // Lấy lịch sử giá theo loại dịch vụ
    List<ServiceRate> findByServiceTypeOrderByEffectiveDateDesc(ServiceRate.ServiceType serviceType);

    // Lấy các giá đang hoạt động
    List<ServiceRate> findByIsActiveTrue();
}