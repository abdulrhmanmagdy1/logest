package com.edham.logistics.repository;

import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.TrackingEvent;
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
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByShipmentOrderByTimestamp(Shipment shipment);

    @Query("SELECT t FROM TrackingEvent t WHERE t.shipment.driver.id = :driverId AND t.eventType = 'LOCATION_UPDATE' ORDER BY t.timestamp DESC")
    List<TrackingEvent> findLatestLocationByDriver(Long driverId, Pageable pageable);

    default Optional<TrackingEvent> findLatestLocationByDriverId(Long driverId) {
        List<TrackingEvent> events = findLatestLocationByDriver(driverId, Pageable.ofSize(1));
        return events.isEmpty() ? Optional.empty() : Optional.of(events.get(0));
    }

    @Query("SELECT COUNT(t) FROM TrackingEvent t WHERE t.timestamp >= :date")
    Long countSince(LocalDateTime date);

    @Query("SELECT t FROM TrackingEvent t WHERE t.description LIKE %:query% OR t.location LIKE %:query%")
    Page<TrackingEvent> searchTrackingEvents(String query, Pageable pageable);

    @Query("SELECT t FROM TrackingEvent t WHERE t.shipment.id = :shipmentId ORDER BY t.timestamp DESC")
    List<TrackingEvent> findLatestByShipment(Long shipmentId, Pageable pageable);

    default Optional<TrackingEvent> findLatestByShipmentId(Long shipmentId) {
        List<TrackingEvent> events = findLatestByShipment(shipmentId, Pageable.ofSize(1));
        return events.isEmpty() ? Optional.empty() : Optional.of(events.get(0));
    }
}
