package com.edham.logistics.service;

import com.edham.logistics.dto.ShipmentRequest;
import com.edham.logistics.dto.ShipmentResponse;
import com.edham.logistics.dto.ShipmentUpdateRequest;
import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.ShipmentStatus;
import com.edham.logistics.model.TrackingEvent;
import com.edham.logistics.model.User;
import com.edham.logistics.repository.ShipmentRepository;
import com.edham.logistics.repository.TrackingEventRepository;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.util.PaginatedResponse;
import com.edham.logistics.util.AuditLogger;
import com.edham.logistics.websocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private TrackingEventRepository trackingEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    private WebSocketService webSocketService;

    public PaginatedResponse<ShipmentResponse> getAllShipments(
            Pageable pageable, String status, String trackingNumber, 
            Long customerId, Long driverId) {
        
        Page<Shipment> shipments = shipmentRepository.findAllWithFilters(
            pageable, status, trackingNumber, customerId, driverId);
        
        List<ShipmentResponse> shipmentResponses = shipments.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            shipmentResponses,
            shipments.getNumber(),
            shipments.getSize(),
            shipments.getTotalElements(),
            shipments.getTotalPages(),
            shipments.isLast()
        );
    }

    public ShipmentResponse getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        return convertToResponse(shipment);
    }

    public ShipmentResponse createShipment(ShipmentRequest request, HttpServletRequest httpRequest) {
        try {
            User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            User driver = null;
            if (request.getDriverId() != null) {
                driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            }
            
            Shipment shipment = new Shipment();
            shipment.setTrackingNumber(generateTrackingNumber());
            shipment.setCustomer(customer);
            shipment.setDriver(driver);
            shipment.setOrigin(request.getOrigin());
            shipment.setDestination(request.getDestination());
            shipment.setPickupAddress(request.getPickupAddress());
            shipment.setDeliveryAddress(request.getDeliveryAddress());
            shipment.setRecipientName(request.getRecipientName());
            shipment.setRecipientPhone(request.getRecipientPhone());
            shipment.setPackageDescription(request.getPackageDescription());
            shipment.setWeight(request.getWeight());
            shipment.setDimensions(request.getDimensions());
            shipment.setValue(request.getValue());
            shipment.setSpecialInstructions(request.getSpecialInstructions());
            shipment.setStatus(ShipmentStatus.PENDING);
            shipment.setCreatedAt(LocalDateTime.now());
            shipment.setUpdatedAt(LocalDateTime.now());
            
            shipment = shipmentRepository.save(shipment);
            
            TrackingEvent initialEvent = new TrackingEvent();
            initialEvent.setShipment(shipment);
            initialEvent.setEventType("CREATED");
            initialEvent.setDescription("Shipment created and pending pickup");
            initialEvent.setLocation(request.getOrigin());
            initialEvent.setTimestamp(LocalDateTime.now());
            trackingEventRepository.save(initialEvent);
            
            webSocketService.sendShipmentUpdate(shipment.getCustomer().getId(), 
                convertToResponse(shipment));
            
            return convertToResponse(shipment);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create shipment: " + e.getMessage());
        }
    }

    public ShipmentResponse updateShipment(Long id, ShipmentUpdateRequest request, 
                                        HttpServletRequest httpRequest) {
        try {
            Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
            
            if (request.getOrigin() != null) shipment.setOrigin(request.getOrigin());
            if (request.getDestination() != null) shipment.setDestination(request.getDestination());
            if (request.getPickupAddress() != null) shipment.setPickupAddress(request.getPickupAddress());
            if (request.getDeliveryAddress() != null) shipment.setDeliveryAddress(request.getDeliveryAddress());
            if (request.getRecipientName() != null) shipment.setRecipientName(request.getRecipientName());
            if (request.getRecipientPhone() != null) shipment.setRecipientPhone(request.getRecipientPhone());
            if (request.getPackageDescription() != null) shipment.setPackageDescription(request.getPackageDescription());
            if (request.getWeight() != null) shipment.setWeight(request.getWeight());
            if (request.getDimensions() != null) shipment.setDimensions(request.getDimensions());
            if (request.getValue() != null) shipment.setValue(request.getValue());
            if (request.getSpecialInstructions() != null) shipment.setSpecialInstructions(request.getSpecialInstructions());
            
            if (request.getDriverId() != null) {
                User driver = userRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
                shipment.setDriver(driver);
            }
            
            shipment.setUpdatedAt(LocalDateTime.now());
            shipment = shipmentRepository.save(shipment);
            
            webSocketService.sendShipmentUpdate(shipment.getCustomer().getId(), 
                convertToResponse(shipment));
            
            return convertToResponse(shipment);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to update shipment: " + e.getMessage());
        }
    }

    public void deleteShipment(Long id) {
        shipmentRepository.deleteById(id);
    }

    public PaginatedResponse<ShipmentResponse> getDriverShipments(Pageable pageable, String status) {
        Long driverId = 1L; // Placeholder
        Page<Shipment> shipments = shipmentRepository.findByDriverId(driverId, status, pageable);
        
        List<ShipmentResponse> shipmentResponses = shipments.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            shipmentResponses,
            shipments.getNumber(),
            shipments.getSize(),
            shipments.getTotalElements(),
            shipments.getTotalPages(),
            shipments.isLast()
        );
    }

    public PaginatedResponse<ShipmentResponse> getCustomerShipments(Pageable pageable, String status) {
        Long customerId = 1L; // Placeholder
        Page<Shipment> shipments = shipmentRepository.findByCustomerId(customerId, status, pageable);
        
        List<ShipmentResponse> shipmentResponses = shipments.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            shipmentResponses,
            shipments.getNumber(),
            shipments.getSize(),
            shipments.getTotalElements(),
            shipments.getTotalPages(),
            shipments.isLast()
        );
    }

    public PaginatedResponse<ShipmentResponse> searchShipments(String query, Pageable pageable, 
                                                           String status) {
        Page<Shipment> shipments = shipmentRepository.searchShipments(query, status, pageable);
        
        List<ShipmentResponse> shipmentResponses = shipments.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return new PaginatedResponse<>(
            shipmentResponses,
            shipments.getNumber(),
            shipments.getSize(),
            shipments.getTotalElements(),
            shipments.getTotalPages(),
            shipments.isLast()
        );
    }

    public Map<String, Object> getShipmentStatistics() {
        return shipmentRepository.countByStatus().stream()
            .collect(Collectors.toMap(m -> (String)m.get("status"), m -> m.get("count")));
    }

    public ShipmentResponse updateShipmentStatus(Long id, String status, String notes, 
                                             HttpServletRequest httpRequest) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        shipment.setStatus(ShipmentStatus.valueOf(status.toUpperCase()));
        shipment.setUpdatedAt(LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return convertToResponse(shipment);
    }

    public ShipmentResponse assignDriver(Long id, Long driverId, HttpServletRequest httpRequest) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        User driver = userRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        shipment.setDriver(driver);
        shipment.setUpdatedAt(LocalDateTime.now());
        shipment = shipmentRepository.save(shipment);
        
        return convertToResponse(shipment);
    }

    public List<TrackingEvent> getShipmentTracking(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        return trackingEventRepository.findByShipmentOrderByTimestamp(shipment);
    }

    public TrackingEvent addTrackingUpdate(Long id, Map<String, Object> trackingData, 
                                         HttpServletRequest httpRequest) {
        Shipment shipment = shipmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        TrackingEvent event = new TrackingEvent();
        event.setShipment(shipment);
        event.setEventType((String) trackingData.get("eventType"));
        event.setDescription((String) trackingData.get("description"));
        event.setLocation((String) trackingData.get("location"));
        event.setTimestamp(LocalDateTime.now());
        
        return trackingEventRepository.save(event);
    }

    private String generateTrackingNumber() {
        return "EDH-" + System.currentTimeMillis();
    }

    private ShipmentResponse convertToResponse(Shipment shipment) {
        ShipmentResponse response = new ShipmentResponse();
        response.setId(shipment.getId());
        response.setTrackingNumber(shipment.getTrackingNumber());
        if (shipment.getCustomer() != null) {
            response.setCustomerId(shipment.getCustomer().getId());
            response.setCustomerName(shipment.getCustomer().getFirstName() + " " + shipment.getCustomer().getLastName());
        }
        if (shipment.getDriver() != null) {
            response.setDriverId(shipment.getDriver().getId());
            response.setDriverName(shipment.getDriver().getFirstName() + " " + shipment.getDriver().getLastName());
        }
        response.setOrigin(shipment.getOrigin());
        response.setDestination(shipment.getDestination());
        response.setStatus(shipment.getStatus().name());
        response.setCreatedAt(shipment.getCreatedAt());
        response.setUpdatedAt(shipment.getUpdatedAt());
        return response;
    }
}
