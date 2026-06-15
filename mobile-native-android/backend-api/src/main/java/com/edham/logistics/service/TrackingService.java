package com.edham.logistics.service;

import com.edham.logistics.dto.TrackingEventRequest;
import com.edham.logistics.dto.TrackingEventResponse;
import com.edham.logistics.model.TrackingEvent;
import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.DriverLocation;
import com.edham.logistics.repository.TrackingEventRepository;
import com.edham.logistics.repository.ShipmentRepository;
import com.edham.logistics.repository.DriverLocationRepository;
import com.edham.logistics.util.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrackingService {

    @Autowired
    private TrackingEventRepository trackingEventRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    public TrackingService() {
        // We will start simulation after beans are injected, using @PostConstruct or similar in a real app
        // But for this environment, let's trigger it manually or assume it runs.
    }

    @Autowired
    private com.edham.logistics.websocket.WebSocketService webSocketService;

    @jakarta.annotation.PostConstruct
    public void startSimulation() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::simulateMovement, 5, 5, TimeUnit.SECONDS);
    }

    private void simulateMovement() {
        List<DriverLocation> locations = driverLocationRepository.findAll();
        if (locations.isEmpty()) {
            initializeDrivers();
            return;
        }

        locations.forEach(loc -> {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            double heading = loc.getHeading();
            double speed = loc.getSpeed() / 3600.0; // km per second

            lat += speed * Math.cos(Math.toRadians(heading)) / 111.0;
            lng += speed * Math.sin(Math.toRadians(heading)) / (111.0 * Math.cos(Math.toRadians(lat)));
            
            loc.setLatitude(lat);
            loc.setLongitude(lng);
            loc.setUpdatedAt(LocalDateTime.now());

            if (Math.random() > 0.8) {
                loc.setHeading(heading + (Math.random() * 20 - 10));
            }
            driverLocationRepository.save(loc);

            // Geofencing checks
            checkGeofences(loc);

            // Broadcast via WebSocket
            webSocketService.sendDriverLocationUpdate(loc.getDriverId(), lat, lng);
        });
    }

    private void checkGeofences(DriverLocation loc) {
        // Riyadh Hub (~24.7136, 46.6753)
        double distRiyadh = calculateDistance(loc.getLatitude(), loc.getLongitude(), 24.7136, 46.6753);
        if (distRiyadh < 5.0) { // 5km radius
            webSocketService.sendSystemAlert("GEOFENCE_ENTER", "Driver " + loc.getDriverId() + " entered Riyadh Hub", loc);
        }
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private void initializeDrivers() {
        Long[] ids = {1L, 2L, 3L, 4L, 5L};
        Double[][] coords = {
            {24.7136, 46.6753}, {21.4858, 39.1925}, {26.4207, 50.0888}, {24.4672, 39.6068}, {18.2162, 42.5053}
        };
        for (int i = 0; i < ids.length; i++) {
            DriverLocation loc = DriverLocation.builder()
                    .driverId(ids[i]).latitude(coords[i][0]).longitude(coords[i][1])
                    .heading(Math.random() * 360).speed(70.0).updatedAt(LocalDateTime.now()).build();
            driverLocationRepository.save(loc);
        }
    }

    public List<TrackingEventResponse> getShipmentTracking(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        return trackingEventRepository.findByShipmentOrderByTimestamp(shipment).stream()
            .map(this::convertToResponse).collect(Collectors.toList());
    }

    public TrackingEventResponse addTrackingEvent(Long shipmentId, TrackingEventRequest request, 
                                               HttpServletRequest httpRequest) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        TrackingEvent event = TrackingEvent.builder()
                .shipment(shipment).eventType(request.getEventType())
                .description(request.getDescription()).location(request.getLocation())
                .latitude(request.getLatitude()).longitude(request.getLongitude())
                .timestamp(LocalDateTime.now()).build();
        
        return convertToResponse(trackingEventRepository.save(event));
    }

    public void updateDriverLocation(Double latitude, Double longitude, String address, 
                                  HttpServletRequest request) {
        Long driverId = 1L; // Get from session
        DriverLocation loc = driverLocationRepository.findById(driverId)
                .orElse(DriverLocation.builder().driverId(driverId).build());
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setAddress(address);
        loc.setUpdatedAt(LocalDateTime.now());
        driverLocationRepository.save(loc);
    }

    public DriverLocation getDriverLocation(Long driverId) { 
        return driverLocationRepository.findById(driverId).orElse(null);
    }

    public List<DriverLocation> getAllDriverLocations() {
        return driverLocationRepository.findAll();
    }

    public List<Shipment> getDriverActiveShipments(HttpServletRequest request) {
        // Implementation stub
        return java.util.Collections.emptyList();
    }

    public List<DriverLocation> getShipmentRoute(Long shipmentId) {
        // Implementation stub
        return java.util.Collections.emptyList();
    }

    public LocalDateTime getEstimatedDeliveryTime(Long shipmentId) {
        // Implementation stub
        return LocalDateTime.now().plusHours(2);
    }

    public Map<String, Object> getTrackingStatistics() {
        // Implementation stub
        return new java.util.HashMap<>();
    }

    public List<Shipment> getDelayedShipments() {
        // Implementation stub
        return java.util.Collections.emptyList();
    }

    public List<TrackingEventResponse> bulkUpdateTracking(List<TrackingEventRequest> requests, HttpServletRequest request) {
        return requests.stream()
            .map(req -> addTrackingEvent(req.getShipmentId(), req, request))
            .collect(Collectors.toList());
    }

    public Map<String, Object> getTrackingAnalytics(String startDate, String endDate) {
        // Implementation stub
        return new java.util.HashMap<>();
    }

    public byte[] exportTrackingData(String format, String startDate, String endDate) {
        // Implementation stub
        return new byte[0];
    }

    public Map<String, Object> getRealtimeTrackingInfo(Long shipmentId) {
        // Implementation stub
        return new java.util.HashMap<>();
    }

    public boolean verifyTrackingNumber(String trackingNumber) {
        // Implementation stub
        return true;
    }

    public PaginatedResponse<TrackingEventResponse> getTrackingEvents(Pageable pageable, 
                                                                 String eventType, Long shipmentId) {
        Page<TrackingEvent> events = trackingEventRepository.findAll(pageable);
        return new PaginatedResponse<>(
            events.getContent().stream().map(this::convertToResponse).collect(Collectors.toList()),
            events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast()
        );
    }

    public PaginatedResponse<TrackingEventResponse> searchTrackingEvents(String query, Pageable pageable) {
        Page<TrackingEvent> events = trackingEventRepository.searchTrackingEvents(query, pageable);
        return new PaginatedResponse<>(
            events.getContent().stream().map(this::convertToResponse).collect(Collectors.toList()),
            events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages(), events.isLast()
        );
    }

    private TrackingEventResponse convertToResponse(TrackingEvent event) {
        TrackingEventResponse response = new TrackingEventResponse();
        response.setId(event.getId());
        response.setShipmentId(event.getShipment().getId());
        response.setEventType(event.getEventType());
        response.setDescription(event.getDescription());
        response.setLocation(event.getLocation());
        response.setLatitude(event.getLatitude());
        response.setLongitude(event.getLongitude());
        response.setTimestamp(event.getTimestamp());
        return response;
    }
}
