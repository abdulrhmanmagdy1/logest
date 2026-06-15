package com.edham.logistics.repository;

import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    @Query("SELECT s FROM Shipment s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:trackingNumber IS NULL OR s.trackingNumber LIKE %:trackingNumber%) AND " +
           "(:customerId IS NULL OR s.customer.id = :customerId) AND " +
           "(:driverId IS NULL OR s.driver.id = :driverId)")
    Page<Shipment> findAllWithFilters(Pageable pageable, String status, String trackingNumber, Long customerId, Long driverId);

    @Query("SELECT s FROM Shipment s WHERE s.driver.id = :driverId AND (:status IS NULL OR s.status = :status)")
    Page<Shipment> findByDriverId(Long driverId, String status, Pageable pageable);

    @Query("SELECT s FROM Shipment s WHERE s.customer.id = :customerId AND (:status IS NULL OR s.status = :status)")
    Page<Shipment> findByCustomerId(Long customerId, String status, Pageable pageable);

    @Query("SELECT s FROM Shipment s WHERE " +
           "(s.origin LIKE %:query% OR s.destination LIKE %:query% OR s.trackingNumber LIKE %:query%) AND " +
           "(:status IS NULL OR s.status = :status)")
    Page<Shipment> searchShipments(String query, String status, Pageable pageable);

    @Query("SELECT s.status as status, COUNT(s) as count FROM Shipment s GROUP BY s.status")
    List<Map<String, Object>> countByStatus();

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.createdAt >= :date")
    Long countSince(LocalDateTime date);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status IN ('PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY')")
    Long countActiveShipments();

    @Query("SELECT s FROM Shipment s WHERE s.driver.id = :driverId AND s.status NOT IN ('DELIVERED', 'CANCELLED')")
    List<Shipment> findActiveByDriverId(Long driverId);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatusAndCreatedAtBetween(ShipmentStatus status, LocalDateTime startDate, LocalDateTime endDate);

    List<Shipment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Shipment> findByStatusAndCreatedAtBetween(ShipmentStatus status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM Shipment s WHERE s.status = 'DELIVERED' AND s.actualDeliveryTime > s.expectedDeliveryTime AND s.createdAt BETWEEN :startDate AND :endDate")
    List<Shipment> findDelayedShipments(LocalDateTime startDate, LocalDateTime endDate);
}
