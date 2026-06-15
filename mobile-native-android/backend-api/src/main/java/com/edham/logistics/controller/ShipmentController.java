package com.edham.logistics.controller;

import com.edham.logistics.dto.ShipmentRequest;
import com.edham.logistics.dto.ShipmentResponse;
import com.edham.logistics.dto.ShipmentUpdateRequest;
import com.edham.logistics.service.ShipmentService;
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
 * Shipment Controller
 * Handles shipment management operations
 */
@RestController
@RequestMapping("/api/v1/shipments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Get all shipments (paginated)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ShipmentResponse>>> getAllShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String trackingNumber,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long driverId) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<ShipmentResponse> response = shipmentService.getAllShipments(
                pageable, status, trackingNumber, customerId, driverId);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(true)
                    .message("Shipments retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve shipments: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipment by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentById(@PathVariable Long id) {
        try {
            ShipmentResponse response = shipmentService.getShipmentById(id);
            
            return ResponseEntity.ok(
                ApiResponse.<ShipmentResponse>builder()
                    .success(true)
                    .message("Shipment retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(false)
                    .message("Shipment not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Create new shipment
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody ShipmentRequest shipmentRequest,
            HttpServletRequest request) {
        
        try {
            ShipmentResponse response = shipmentService.createShipment(shipmentRequest, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(true)
                    .message("Shipment created successfully")
                    .data(response)
                    .build()
                );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(false)
                    .message("Failed to create shipment: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Update shipment
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        try {
            ShipmentResponse response = shipmentService.updateShipment(id, updateRequest, request);
            
            return ResponseEntity.ok(
                ApiResponse.<ShipmentResponse>builder()
                    .success(true)
                    .message("Shipment updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(false)
                    .message("Failed to update shipment: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Delete shipment
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable Long id) {
        try {
            shipmentService.deleteShipment(id);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Shipment deleted successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete shipment: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipments for current driver
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ShipmentResponse>>> getMyShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<ShipmentResponse> response = shipmentService.getDriverShipments(
                pageable, status);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(true)
                    .message("Driver shipments retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve shipments: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipments for current customer
     */
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ShipmentResponse>>> getCustomerShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<ShipmentResponse> response = shipmentService.getCustomerShipments(
                pageable, status);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(true)
                    .message("Customer shipments retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve shipments: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Search shipments
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ShipmentResponse>>> searchShipments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<ShipmentResponse> response = shipmentService.searchShipments(
                query, pageable, status);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(true)
                    .message("Search completed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<ShipmentResponse>>builder()
                    .success(false)
                    .message("Search failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipment statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getShipmentStats() {
        try {
            Object stats = shipmentService.getShipmentStatistics();
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Statistics retrieved successfully")
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
     * Update shipment status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String notes,
            HttpServletRequest request) {
        
        try {
            ShipmentResponse response = shipmentService.updateShipmentStatus(
                id, status, notes, request);
            
            return ResponseEntity.ok(
                ApiResponse.<ShipmentResponse>builder()
                    .success(true)
                    .message("Shipment status updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(false)
                    .message("Failed to update status: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Assign driver to shipment
     */
    @PatchMapping("/{id}/assign-driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignDriver(
            @PathVariable Long id,
            @RequestParam Long driverId,
            HttpServletRequest request) {
        
        try {
            ShipmentResponse response = shipmentService.assignDriver(id, driverId, request);
            
            return ResponseEntity.ok(
                ApiResponse.<ShipmentResponse>builder()
                    .success(true)
                    .message("Driver assigned successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ShipmentResponse>builder()
                    .success(false)
                    .message("Failed to assign driver: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get shipment tracking history
     */
    @GetMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<Object>> getShipmentTracking(@PathVariable Long id) {
        try {
            Object tracking = shipmentService.getShipmentTracking(id);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Tracking history retrieved successfully")
                    .data(tracking)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Tracking not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Add tracking update
     */
    @PostMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<Object>> addTrackingUpdate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> trackingData,
            HttpServletRequest request) {
        
        try {
            Object tracking = shipmentService.addTrackingUpdate(id, trackingData, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Object>builder()
                    .success(true)
                    .message("Tracking update added successfully")
                    .data(tracking)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to add tracking update: " + e.getMessage())
                    .build()
                );
        }
    }
}
