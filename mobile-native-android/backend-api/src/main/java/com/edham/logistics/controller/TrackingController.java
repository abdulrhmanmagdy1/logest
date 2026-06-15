package com.edham.logistics.controller;

import com.edham.logistics.dto.TrackingEventRequest;
import com.edham.logistics.dto.TrackingEventResponse;
import com.edham.logistics.model.Shipment;
import com.edham.logistics.service.TrackingService;
import com.edham.logistics.util.ApiResponse;
import com.edham.logistics.util.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Tracking Controller
 * Handles shipment tracking operations
 */
@RestController
@RequestMapping("/api/v1/tracking")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    /**
     * Get shipment tracking history
     */
    @GetMapping("/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<List<TrackingEventResponse>>> getShipmentTracking(
            @PathVariable Long shipmentId) {
        
        try {
            List<TrackingEventResponse> tracking = trackingService.getShipmentTracking(shipmentId);
            
            return ResponseEntity.ok(
                ApiResponse.<List<TrackingEventResponse>>builder()
                    .success(true)
                    .message("Tracking history retrieved successfully")
                    .data(tracking)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<List<TrackingEventResponse>>builder()
                    .success(false)
                    .message("Tracking not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Add tracking event
     */
    @PostMapping("/shipment/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<TrackingEventResponse>> addTrackingEvent(
            @PathVariable Long shipmentId,
            @Valid @RequestBody TrackingEventRequest trackingRequest,
            HttpServletRequest request) {
        
        try {
            TrackingEventResponse tracking = trackingService.addTrackingEvent(
                shipmentId, trackingRequest, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TrackingEventResponse>builder()
                    .success(true)
                    .message("Tracking event added successfully")
                    .data(tracking)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<TrackingEventResponse>builder()
                    .success(false)
                    .message("Failed to add tracking event: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Update driver location
     */
    @PostMapping("/driver/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<Void>> updateDriverLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String address,
            HttpServletRequest request) {
        
        try {
            trackingService.updateDriverLocation(latitude, longitude, address, request);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Driver location updated successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to update location: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get driver current location
     */
    @GetMapping("/driver/{driverId}/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Object>> getDriverLocation(@PathVariable Long driverId) {
        try {
            Object location = trackingService.getDriverLocation(driverId);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Driver location retrieved successfully")
                    .data(location)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Driver location not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get all active driver locations (Supervisor)
     */
    @GetMapping("/drivers/locations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<com.edham.logistics.model.DriverLocation>>> getAllDriverLocations() {
        try {
            List<com.edham.logistics.model.DriverLocation> locations = trackingService.getAllDriverLocations();
            return ResponseEntity.ok(
                ApiResponse.<List<com.edham.logistics.model.DriverLocation>>builder()
                    .success(true)
                    .message("All driver locations retrieved")
                    .data(locations)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<com.edham.logistics.model.DriverLocation>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get active shipments for driver
     */
    @GetMapping("/driver/active-shipments")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<Shipment>>> getDriverActiveShipments(HttpServletRequest request) {
        try {
            List<Shipment> shipments = trackingService.getDriverActiveShipments(request);
            
            return ResponseEntity.ok(
                ApiResponse.<List<Shipment>>builder()
                    .success(true)
                    .message("Active shipments retrieved successfully")
                    .data(shipments)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<Shipment>>builder()
                    .success(false)
                    .message("Failed to retrieve active shipments: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipment route information
     */
    @GetMapping("/shipment/{shipmentId}/route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Object>> getShipmentRoute(@PathVariable Long shipmentId) {
        try {
            Object route = trackingService.getShipmentRoute(shipmentId);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Route information retrieved successfully")
                    .data(route)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Route not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get estimated delivery time
     */
    @GetMapping("/shipment/{shipmentId}/eta")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Object>> getEstimatedDeliveryTime(@PathVariable Long shipmentId) {
        try {
            Object eta = trackingService.getEstimatedDeliveryTime(shipmentId);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("ETA retrieved successfully")
                    .data(eta)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("ETA not available: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get tracking statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getTrackingStats() {
        try {
            Object stats = trackingService.getTrackingStatistics();
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Tracking statistics retrieved successfully")
                    .data(stats)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve statistics: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get delayed shipments
     */
    @GetMapping("/delayed-shipments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<Shipment>>> getDelayedShipments() {
        try {
            List<Shipment> shipments = trackingService.getDelayedShipments();
            
            return ResponseEntity.ok(
                ApiResponse.<List<Shipment>>builder()
                    .success(true)
                    .message("Delayed shipments retrieved successfully")
                    .data(shipments)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<List<Shipment>>builder()
                    .success(false)
                    .message("Failed to retrieve delayed shipments: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get tracking events with pagination
     */
    @GetMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<TrackingEventResponse>>> getTrackingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) Long shipmentId) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<TrackingEventResponse> events = trackingService.getTrackingEvents(
                pageable, eventType, shipmentId);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<TrackingEventResponse>>builder()
                    .success(true)
                    .message("Tracking events retrieved successfully")
                    .data(events)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<TrackingEventResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve tracking events: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Bulk update tracking events
     */
    @PostMapping("/bulk-update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrackingEventResponse>>> bulkUpdateTracking(
            @RequestBody List<TrackingEventRequest> trackingRequests,
            HttpServletRequest request) {
        
        try {
            List<TrackingEventResponse> events = trackingService.bulkUpdateTracking(
                trackingRequests, request);
            
            return ResponseEntity.ok(
                ApiResponse.<List<TrackingEventResponse>>builder()
                    .success(true)
                    .message("Bulk tracking update completed successfully")
                    .data(events)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<List<TrackingEventResponse>>builder()
                    .success(false)
                    .message("Bulk update failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get tracking analytics
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getTrackingAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            Object analytics = trackingService.getTrackingAnalytics(startDate, endDate);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Tracking analytics retrieved successfully")
                    .data(analytics)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve analytics: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Search tracking events
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<TrackingEventResponse>>> searchTrackingEvents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<TrackingEventResponse> events = trackingService.searchTrackingEvents(
                query, pageable);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<TrackingEventResponse>>builder()
                    .success(true)
                    .message("Search completed successfully")
                    .data(events)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<TrackingEventResponse>>builder()
                    .success(false)
                    .message("Search failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Export tracking data
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<byte[]> exportTrackingData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String format) {
        
        try {
            byte[] data = trackingService.exportTrackingData(startDate, endDate, format);
            
            String filename = "tracking_data." + (format != null ? format : "csv");
            String contentType = format != null && format.equals("json") ? 
                "application/json" : "text/csv";
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", contentType)
                .body(data);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get real-time tracking updates (WebSocket endpoint info)
     */
    @GetMapping("/realtime/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRealtimeTrackingInfo(
            @PathVariable Long shipmentId) {
        
        try {
            Map<String, Object> info = trackingService.getRealtimeTrackingInfo(shipmentId);
            
            return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Real-time tracking info retrieved successfully")
                    .data(info)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Real-time tracking not available: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Verify tracking number
     */
    @GetMapping("/verify/{trackingNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> verifyTrackingNumber(@PathVariable String trackingNumber) {
        try {
            Object verification = trackingService.verifyTrackingNumber(trackingNumber);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Tracking number verified successfully")
                    .data(verification)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Invalid tracking number: " + e.getMessage())
                    .build()
                );
        }
    }
}
