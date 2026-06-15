// // // package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.service.SmartSuggestionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Smart suggestions controller for intelligent logistics recommendations
 */
@RestController
@RequestMapping("/api/v1/suggestions")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class SmartSuggestionsController {

    private final SmartSuggestionsService smartSuggestionsService;

    @Autowired
    public SmartSuggestionsController(SmartSuggestionsService smartSuggestionsService) {
        this.smartSuggestionsService = smartSuggestionsService;
    }

    /**
     * Get driver suggestions for shipment
     */
    @GetMapping("/driver/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<DriverSuggestionDTO> getDriverSuggestions(@PathVariable Long shipmentId) {
        try {
            DriverSuggestionDTO suggestions = smartSuggestionsService.suggestBestDriver(shipmentId);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("Error getting driver suggestions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get route suggestions for shipment
     */
    @GetMapping("/route/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<RouteSuggestionDTO> getRouteSuggestions(@PathVariable Long shipmentId) {
        try {
            RouteSuggestionDTO suggestions = smartSuggestionsService.suggestOptimalRoute(shipmentId);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("Error getting route suggestions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get pricing estimate
     */
    @PostMapping("/pricing")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CUSTOMER')")
    public ResponseEntity<PricingSuggestionDTO> getPricingEstimate(
            @Valid @RequestBody PricingRequestDTO request) {
        try {
            PricingSuggestionDTO estimate = smartSuggestionsService.suggestPricingEstimate(request);
            return ResponseEntity.ok(estimate);
        } catch (Exception e) {
            log.error("Error getting pricing estimate: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Detect delayed shipments
     */
    @GetMapping("/delays")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<DelayedShipmentDTO>> detectDelayedShipments() {
        try {
            List<DelayedShipmentDTO> delayedShipments = smartSuggestionsService.detectDelayedShipments();
            return ResponseEntity.ok(delayedShipments);
        } catch (Exception e) {
            log.error("Error detecting delayed shipments: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generic smart suggestions endpoint
     */
    @PostMapping("/smart")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<SmartSuggestionResponseDTO> getSmartSuggestion(
            @Valid @RequestBody SmartSuggestionRequestDTO request) {
        try {
            SmartSuggestionResponseDTO response = SmartSuggestionResponseDTO.builder()
                    .success(true)
                    .suggestionType(request.getSuggestionType())
                    .calculatedAt(LocalDateTime.now())
                    .build();

            switch (request.getSuggestionType().toUpperCase()) {
                case "DRIVER":
                    if (request.getShipmentId() != null) {
                        response.setSuggestionData(smartSuggestionsService.suggestBestDriver(request.getShipmentId()));
                    }
                    break;
                case "ROUTE":
                    if (request.getShipmentId() != null) {
                        response.setSuggestionData(smartSuggestionsService.suggestOptimalRoute(request.getShipmentId()));
                    }
                    break;
                case "PRICING":
                    if (request.getPricingRequest() != null) {
                        response.setSuggestionData(smartSuggestionsService.suggestPricingEstimate(request.getPricingRequest()));
                    }
                    break;
                case "DELAY_DETECTION":
                    response.setSuggestionData(smartSuggestionsService.detectDelayedShipments());
                    break;
                default:
                    response.setSuccess(false);
                    response.setError("Unsupported suggestion type: " + request.getSuggestionType());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting smart suggestion: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    SmartSuggestionResponseDTO.builder()
                            .success(false)
                            .error("Internal server error: " + e.getMessage())
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Test smart suggestions system
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SmartSuggestionResponseDTO> testSmartSuggestions() {
        try {
            // Test driver suggestion
            DriverSuggestionDTO driverSuggestion = DriverSuggestionDTO.builder()
                    .shipmentId(1L)
                    .totalAvailableDrivers(5)
                    .calculatedAt(LocalDateTime.now())
                    .build();

            // Test pricing suggestion
            PricingSuggestionDTO pricingSuggestion = PricingSuggestionDTO.builder()
                    .basePrice(50.0)
                    .finalPrice(65.5)
                    .confidenceLevel(0.85)
                    .calculatedAt(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(
                    SmartSuggestionResponseDTO.builder()
                            .success(true)
                            .suggestionType("TEST")
                            .suggestionData(Map.of(
                                    "driverSuggestion", driverSuggestion,
                                    "pricingSuggestion", pricingSuggestion
                            ))
                            .message("Smart suggestions system is operational")
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error testing smart suggestions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    SmartSuggestionResponseDTO.builder()
                            .success(false)
                            .error("Test failed: " + e.getMessage())
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }
}
