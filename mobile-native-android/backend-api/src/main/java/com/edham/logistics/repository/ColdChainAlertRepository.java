package com.edham.logistics.repository;

import com.edham.logistics.model.ColdChainAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ColdChainAlertRepository extends JpaRepository<ColdChainAlert, Long> {
    List<ColdChainAlert> findByShipmentIdOrderByTimestampDesc(Long shipmentId);
    List<ColdChainAlert> findByShipmentIdAndSeverityOrderByTimestampDesc(Long shipmentId, ColdChainAlert.AlertSeverity severity);
    List<ColdChainAlert> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    long deleteByTimestampBefore(LocalDateTime date);
}
