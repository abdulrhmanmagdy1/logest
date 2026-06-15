package com.edham.logistics.repository;

import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByShipmentOrderByTimestamp(Shipment shipment);
}
