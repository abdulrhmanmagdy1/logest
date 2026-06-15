package com.edham.logistics.repository;

import com.edham.logistics.model.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
    List<Emergency> findByStatus(String status);
    List<Emergency> findByDriverId(Long driverId);
    List<Emergency> findBySeverity(String severity);
}
