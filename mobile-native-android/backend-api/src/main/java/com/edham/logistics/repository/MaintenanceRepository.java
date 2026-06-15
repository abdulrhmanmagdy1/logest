package com.edham.logistics.repository;

import com.edham.logistics.model.Maintenance;
import com.edham.logistics.model.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByVehicleId(Long vehicleId);
    List<Maintenance> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM Maintenance m WHERE m.status = 'SCHEDULED' OR m.status = 'IN_PROGRESS'")
    List<Maintenance> findActiveMaintenance();
}
