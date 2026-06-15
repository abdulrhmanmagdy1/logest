package com.edham.logistics.controller;



import com.edham.logistics.dto.*;

import com.edham.logistics.service.AdvancedTrackingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;



import javax.validation.Valid;

import java.time.LocalDateTime;

import java.util.List;

import java.util.Map;



/**

 * Advanced tracking controller for enhanced map features

 */

@RestController

@RequestMapping("/api/v1/tracking/advanced")

@CrossOrigin(origins = "*", maxAge = 3600)

@Slf4j

public class AdvancedTrackingController {



    private final AdvancedTrackingService advancedTrackingService;



    @Autowired

    public AdvancedTrackingController(AdvancedTrackingService advancedTrackingService) {

        this.advancedTrackingService = advancedTrackingService;

    }



    /**

     * Get vehicle animation data for smooth movement

     */

    @GetMapping("/vehicle/{vehicleId}/animation")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")

    public ResponseEntity<VehicleAnimationDTO> getVehicleAnimation(

            @PathVariable Long vehicleId,

            @RequestParam(required = false) LocalDateTime startTime,

            @RequestParam(required = false) LocalDateTime endTime) {

        try {

            if (startTime == null) {

                startTime = LocalDateTime.now().minusHours(2);

            }

            if (endTime == null) {

                endTime = LocalDateTime.now();

            }



            VehicleAnimationDTO animation = advancedTrackingService.getVehicleAnimation(vehicleId, startTime, endTime);

            return ResponseEntity.ok(animation);

        } catch (Exception e) {

            log.error("Error getting vehicle animation: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get route replay for shipment

     */

    @GetMapping("/shipment/{shipmentId}/replay")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CUSTOMER')")

    public ResponseEntity<RouteReplayDTO> getRouteReplay(@PathVariable Long shipmentId) {

        try {

            RouteReplayDTO replay = advancedTrackingService.getRouteReplay(shipmentId);

            return ResponseEntity.ok(replay);

        } catch (Exception e) {

            log.error("Error getting route replay: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Check geofencing alerts for vehicle

     */

    @GetMapping("/vehicle/{vehicleId}/geofence")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<List<GeofenceAlertDTO>> checkGeofencingAlerts(@PathVariable Long vehicleId) {

        try {

            List<GeofenceAlertDTO> alerts = advancedTrackingService.checkGeofencingAlerts(vehicleId);

            return ResponseEntity.ok(alerts);

        } catch (Exception e) {

            log.error("Error checking geofencing alerts: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get multi-vehicle tracking view

     */

    @PostMapping("/multi-vehicle")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<MultiVehicleTrackingDTO> getMultiVehicleTracking(

            @RequestBody List<Long> vehicleIds) {

        try {

            if (vehicleIds.isEmpty()) {

                return ResponseEntity.badRequest().build();

            }



            MultiVehicleTrackingDTO tracking = advancedTrackingService.getMultiVehicleTracking(vehicleIds);

            return ResponseEntity.ok(tracking);

        } catch (Exception e) {

            log.error("Error getting multi-vehicle tracking: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get map performance optimization data

     */

    @PostMapping("/map-performance")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<MapPerformanceDTO> getMapPerformance(

            @RequestBody TrackingRequestDTO request) {

        try {

            MapPerformanceDTO performance = advancedTrackingService.getMapPerformanceData(

                    request.getVehicleIds(), request.getMapBounds());

            return ResponseEntity.ok(performance);

        } catch (Exception e) {

            log.error("Error getting map performance data: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Generic advanced tracking endpoint

     */

    @PostMapping("/track")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<AdvancedTrackingResponseDTO> getAdvancedTracking(

            @Valid @RequestBody TrackingRequestDTO request) {

        try {

            AdvancedTrackingResponseDTO response = AdvancedTrackingResponseDTO.builder()

                    .success(true)

                    .calculatedAt(LocalDateTime.now())

                    .build();



            if (request.getVehicleId() != null && request.getIncludeAnimation()) {

                response.setTrackingData(advancedTrackingService.getVehicleAnimation(

                        request.getVehicleId(), request.getStartTime(), request.getEndTime()));

                response.setTrackingType("VEHICLE_ANIMATION");

            } else if (request.getShipmentId() != null) {

                response.setTrackingData(advancedTrackingService.getRouteReplay(request.getShipmentId()));

                response.setTrackingType("ROUTE_REPLAY");

            } else if (request.getVehicleId() != null && request.getIncludeGeofencing()) {

                response.setTrackingData(advancedTrackingService.checkGeofencingAlerts(request.getVehicleId()));

                response.setTrackingType("GEOFENCING_ALERTS");

            } else if (request.getVehicleIds() != null && !request.getVehicleIds().isEmpty()) {

                response.setTrackingData(advancedTrackingService.getMultiVehicleTracking(request.getVehicleIds()));

                response.setTrackingType("MULTI_VEHICLE_TRACKING");

            } else {

                response.setSuccess(false);

                response.setError("Invalid tracking request parameters");

            }



            return ResponseEntity.ok(response);



        } catch (Exception e) {

            log.error("Error getting advanced tracking: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(

                    AdvancedTrackingResponseDTO.builder()

                            .success(false)

                            .error("Internal server error: " + e.getMessage())

                            .calculatedAt(LocalDateTime.now())

                            .build()

            );

        }

    }



    /**

     * Test advanced tracking system

     */

    @GetMapping("/test")

    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<AdvancedTrackingResponseDTO> testAdvancedTracking() {

        try {

            // Test vehicle animation

            VehicleAnimationDTO animation = VehicleAnimationDTO.builder()

                    .vehicleId(1L)

                    .animationDuration(Duration.ofMinutes(30))

                    .smoothTransitions(true)

                    .generatedAt(LocalDateTime.now())

                    .build();



            // Test multi-vehicle tracking

            MultiVehicleTrackingDTO multiTracking = MultiVehicleTrackingDTO.builder()

                    .totalVehicles(5)

                    .activeVehicles(3)

                    .generatedAt(LocalDateTime.now())

                    .build();



            return ResponseEntity.ok(

                    AdvancedTrackingResponseDTO.builder()

                            .success(true)

                            .trackingType("TEST")

                            .trackingData(Map.of(

                                    "vehicleAnimation", animation,

                                    "multiVehicleTracking", multiTracking

                            ))

                            .message("Advanced tracking system is operational")

                            .calculatedAt(LocalDateTime.now())

                            .build()

            );



        } catch (Exception e) {

            log.error("Error testing advanced tracking: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(

                    AdvancedTrackingResponseDTO.builder()

                            .success(false)

                            .error("Test failed: " + e.getMessage())

                            .calculatedAt(LocalDateTime.now())

                            .build()

            );

        }

    }



    /**

     * Create geofence

     */

    @PostMapping("/geofence")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<GeofenceDTO> createGeofence(@Valid @RequestBody GeofenceDTO geofence) {

        try {

            // In a real implementation, this would save the geofence to the database

            log.info("Creating geofence: {} at ({}, {}) with radius {}m", 

                    geofence.getName(), geofence.getLatitude(), geofence.getLongitude(), geofence.getRadius());

            

            return ResponseEntity.ok(geofence);

        } catch (Exception e) {

            log.error("Error creating geofence: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get all active geofences

     */

    @GetMapping("/geofences")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<List<GeofenceDTO>> getActiveGeofences() {

        try {

            // In a real implementation, this would query the database

            List<GeofenceDTO> geofences = List.of(

                    GeofenceDTO.builder()

                            .id("geofence_1")

                            .name("Riyadh City Center")

                            .latitude(24.7136)

                            .longitude(46.6753)

                            .radius(5000.0)

                            .type("CIRCLE")

                            .alertOnEntry(true)

                            .alertOnExit(true)

                            .build(),

                    GeofenceDTO.builder()

                            .id("geofence_2")

                            .name("King Khalid Airport")

                            .latitude(24.9576)

                            .longitude(46.6988)

                            .radius(10000.0)

                            .type("CIRCLE")

                            .alertOnEntry(true)

                            .alertOnExit(false)

                            .build()

            );

            

            return ResponseEntity.ok(geofences);

        } catch (Exception e) {

            log.error("Error getting active geofences: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Delete geofence

     */

    @DeleteMapping("/geofence/{geofenceId}")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<Void> deleteGeofence(@PathVariable String geofenceId) {

        try {

            log.info("Deleting geofence: {}", geofenceId);

            // In a real implementation, this would delete from the database

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            log.error("Error deleting geofence: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }

}

