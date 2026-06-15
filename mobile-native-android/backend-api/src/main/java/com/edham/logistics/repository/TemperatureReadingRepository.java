package com.edham.logistics.repository;

import com.edham.logistics.model.TemperatureReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {
    List<TemperatureReading> findByShipmentIdOrderByTimestampAsc(Long shipmentId);
    List<TemperatureReading> findByShipmentIdOrderByTimestampDesc(Long shipmentId);
    Optional<TemperatureReading> findTopByShipmentIdOrderByTimestampDesc(Long shipmentId);
    List<TemperatureReading> findByShipmentIdAndTimestampBetweenOrderByTimestampDesc(Long shipmentId, LocalDateTime start, LocalDateTime end);
    List<TemperatureReading> findByShipmentIdAndTimestampAfterOrderByTimestampDesc(Long shipmentId, LocalDateTime start);
    long deleteByTimestampBefore(LocalDateTime date);
}
