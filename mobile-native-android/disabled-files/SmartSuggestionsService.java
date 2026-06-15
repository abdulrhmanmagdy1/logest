package com.edham.logistics.service;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart suggestions service using rule-based logic
 * Provides intelligent recommendations for logistics operations
 */
@Slf4j
@Service
public class SmartSuggestionsService {

    private final UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;
    private final VehicleRepository vehicleRepository;
    private final TrackingService trackingService;

    @Autowired
    public SmartSuggestionsService(UserRepository userRepository,
                                 ShipmentRepository shipmentRepository,
                                 VehicleRepository vehicleRepository,
                                 TrackingService trackingService) {
        this.userRepository = userRepository;
        this.shipmentRepository = shipmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.trackingService = trackingService;
    }

    /**
     * Suggest best driver for shipment based on multiple factors
     */
    public DriverSuggestionDTO suggestBestDriver(Long shipmentId) {
        try {
            // Get shipment details
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            // Get available drivers
            List<User> availableDrivers = userRepository.findByRole(UserRole.DRIVER)
                    .stream()
                    .filter(driver -> !hasActiveEmergency(driver.getId()))
                    .collect(Collectors.toList());

            // Calculate driver scores
            List<DriverScoreDTO> driverScores = new ArrayList<>();
            for (User driver : availableDrivers) {
                double score = calculateDriverScore(driver, shipment);
                driverScores.add(DriverScoreDTO.builder()
                        .driverId(driver.getId())
                        .driverName(driver.getFullName())
                        .score(score)
                        .reasons(getDriverScoreReasons(driver, shipment))
                        .build());
            }

            // Sort by score (highest first)
            driverScores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

            return DriverSuggestionDTO.builder()
                    .shipmentId(shipmentId)
                    .suggestedDrivers(driverScores.stream()
                            .limit(3)
                            .collect(Collectors.toList()))
                    .totalAvailableDrivers(availableDrivers.size())
                    .calculatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error suggesting best driver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to suggest best driver", e);
        }
    }

    /**
     * Suggest optimal routes for shipment
     */
    public RouteSuggestionDTO suggestOptimalRoute(Long shipmentId) {
        try {
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            // Calculate route options (simplified rule-based logic)
            List<RouteOptionDTO> routes = calculateRouteOptions(shipment);

            return RouteSuggestionDTO.builder()
                    .shipmentId(shipmentId)
                    .routeOptions(routes)
                    .recommendedRoute(routes.stream()
                            .filter(RouteOptionDTO::isRecommended)
                            .findFirst()
                            .orElse(routes.get(0)))
                    .calculatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error suggesting optimal route: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to suggest optimal route", e);
        }
    }

    /**
     * Suggest pricing estimates for shipment
     */
    public PricingSuggestionDTO suggestPricingEstimate(PricingRequestDTO request) {
        try {
            // Base pricing calculation
            double basePrice = calculateBasePrice(request);
            
            // Apply modifiers
            double distanceModifier = calculateDistanceModifier(request.getDistance());
            double weightModifier = calculateWeightModifier(request.getWeight());
            double urgencyModifier = calculateUrgencyModifier(request.getPriority());
            double vehicleModifier = calculateVehicleModifier(request.getVehicleType());

            double finalPrice = basePrice * (1 + distanceModifier + weightModifier + urgencyModifier + vehicleModifier);

            return PricingSuggestionDTO.builder()
                    .basePrice(basePrice)
                    .finalPrice(finalPrice)
                    .modifiers(Map.of(
                            "distance", distanceModifier,
                            "weight", weightModifier,
                            "urgency", urgencyModifier,
                            "vehicle", vehicleModifier
                    ))
                    .estimatedDeliveryTime(calculateEstimatedDeliveryTime(request))
                    .confidenceLevel(calculatePricingConfidence(request))
                    .calculatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error suggesting pricing estimate: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to suggest pricing estimate", e);
        }
    }

    /**
     * Detect delayed shipments automatically
     */
    public List<DelayedShipmentDTO> detectDelayedShipments() {
        try {
            List<Shipment> activeShipments = shipmentRepository.findByStatus(com.edham.logistics.dto.ShipmentStatus.IN_TRANSIT);
            List<DelayedShipmentDTO> delayedShipments = new ArrayList<>();

            for (Shipment shipment : activeShipments) {
                if (isShipmentDelayed(shipment)) {
                    DelayedShipmentDTO delayedShipment = DelayedShipmentDTO.builder()
                            .shipmentId(shipment.getId())
                            .trackingNumber(shipment.getTrackingNumber())
                            .estimatedDelivery(shipment.getEstimatedDelivery())
                            .currentDelay(calculateDelayDuration(shipment))
                            .delayReason(determineDelayReason(shipment))
                            .recommendedActions(getRecommendedActions(shipment))
                            .detectedAt(LocalDateTime.now())
                            .build();

                    delayedShipments.add(delayedShipment);
                }
            }

            return delayedShipments;

        } catch (Exception e) {
            log.error("Error detecting delayed shipments: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to detect delayed shipments", e);
        }
    }

    // Helper methods for driver scoring
    private double calculateDriverScore(User driver, Shipment shipment) {
        double score = 0.0;

        // Base score for availability
        score += 50.0;

        // Add score based on historical performance (simplified)
        score += getPerformanceScore(driver.getId());

        // Add score based on proximity to pickup location
        score += getProximityScore(driver.getId(), shipment);

        // Add score based on current workload
        score += getWorkloadScore(driver.getId());

        return Math.min(100.0, score);
    }

    private List<String> getDriverScoreReasons(User driver, Shipment shipment) {
        List<String> reasons = new ArrayList<>();
        
        if (getPerformanceScore(driver.getId()) > 20) {
            reasons.add("Excellent performance record");
        }
        
        if (getProximityScore(driver.getId(), shipment) > 15) {
            reasons.add("Close to pickup location");
        }
        
        if (getWorkloadScore(driver.getId()) > 10) {
            reasons.add("Low current workload");
        }

        return reasons;
    }

    private double getPerformanceScore(Long driverId) {
        // Simplified performance calculation
        // In real implementation, this would query historical data
        return 15.0 + Math.random() * 25.0;
    }

    private double getProximityScore(Long driverId, Shipment shipment) {
        // Simplified proximity calculation
        // In real implementation, this would use actual GPS coordinates
        return 10.0 + Math.random() * 20.0;
    }

    private double getWorkloadScore(Long driverId) {
        // Simplified workload calculation
        // In real implementation, this would query active assignments
        return 5.0 + Math.random() * 15.0;
    }

    private boolean hasActiveEmergency(Long driverId) {
        // Check if driver has active emergency
        // This would integrate with the emergency system
        return false;
    }

    // Helper methods for route calculation
    private List<RouteOptionDTO> calculateRouteOptions(Shipment shipment) {
        List<RouteOptionDTO> routes = new ArrayList<>();

        // Route 1: Fastest route
        routes.add(RouteOptionDTO.builder()
                .routeId("fastest")
                .name("Fastest Route")
                .estimatedTime(Duration.ofHours(2).plusMinutes(30))
                .estimatedDistance(150.5)
                .estimatedCost(45.0)
                .trafficLevel("Medium")
                .recommended(true)
                .build());

        // Route 2: Economical route
        routes.add(RouteOptionDTO.builder()
                .routeId("economical")
                .name("Economical Route")
                .estimatedTime(Duration.ofHours(3).plusMinutes(15))
                .estimatedDistance(145.2)
                .estimatedCost(38.0)
                .trafficLevel("Low")
                .recommended(false)
                .build());

        // Route 3: Scenic route (avoid traffic)
        routes.add(RouteOptionDTO.builder()
                .routeId("scenic")
                .name("Avoid Traffic Route")
                .estimatedTime(Duration.ofHours(2).plusMinutes(45))
                .estimatedDistance(160.8)
                .estimatedCost(42.0)
                .trafficLevel("Very Low")
                .recommended(false)
                .build());

        return routes;
    }

    // Helper methods for pricing
    private double calculateBasePrice(PricingRequestDTO request) {
        // Base price calculation
        return 25.0 + (request.getDistance() * 0.5) + (request.getWeight() * 0.2);
    }

    private double calculateDistanceModifier(Double distance) {
        if (distance < 50) return -0.1;      // Short distance discount
        if (distance < 100) return 0.0;       // Standard rate
        if (distance < 200) return 0.1;       // Medium distance surcharge
        return 0.2;                            // Long distance surcharge
    }

    private double calculateWeightModifier(Double weight) {
        if (weight < 100) return -0.05;       // Light weight discount
        if (weight < 500) return 0.0;         // Standard rate
        if (weight < 1000) return 0.05;       // Heavy weight surcharge
        return 0.1;                            // Very heavy surcharge
    }

    private double calculateUrgencyModifier(String priority) {
        switch (priority.toLowerCase()) {
            case "urgent": return 0.3;
            case "high": return 0.15;
            case "standard": return 0.0;
            case "low": return -0.1;
            default: return 0.0;
        }
    }

    private double calculateVehicleModifier(String vehicleType) {
        switch (vehicleType.toLowerCase()) {
            case "motorcycle": return -0.2;
            case "van": return 0.0;
            case "truck": return 0.1;
            case "heavy_truck": return 0.2;
            default: return 0.0;
        }
    }

    private Duration calculateEstimatedDeliveryTime(PricingRequestDTO request) {
        // Simplified delivery time calculation
        double baseTime = request.getDistance() / 60.0; // 60 km/h average
        return Duration.ofHours((long) baseTime);
    }

    private double calculatePricingConfidence(PricingRequestDTO request) {
        // Confidence level based on data availability
        double confidence = 0.8; // Base confidence
        
        if (request.getDistance() != null && request.getDistance() > 0) confidence += 0.1;
        if (request.getWeight() != null && request.getWeight() > 0) confidence += 0.05;
        if (request.getVehicleType() != null) confidence += 0.05;
        
        return Math.min(1.0, confidence);
    }

    // Helper methods for delay detection
    private boolean isShipmentDelayed(Shipment shipment) {
        if (shipment.getEstimatedDelivery() == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime estimatedDelivery = shipment.getEstimatedDelivery();
        
        // Consider delayed if past estimated delivery by more than 30 minutes
        return now.isAfter(estimatedDelivery.plusMinutes(30));
    }

    private Duration calculateDelayDuration(Shipment shipment) {
        if (shipment.getEstimatedDelivery() == null) return Duration.ZERO;
        
        return Duration.between(shipment.getEstimatedDelivery(), LocalDateTime.now());
    }

    private String determineDelayReason(Shipment shipment) {
        // Simplified delay reason detection
        // In real implementation, this would analyze tracking data
        List<String> possibleReasons = Arrays.asList(
                "Traffic congestion",
                "Weather conditions",
                "Vehicle breakdown",
                "Route deviation",
                "Loading delays"
        );
        
        return possibleReasons.get((int) (Math.random() * possibleReasons.size()));
    }

    private List<String> getRecommendedActions(Shipment shipment) {
        List<String> actions = new ArrayList<>();
        actions.add("Notify customer about delay");
        actions.add("Update delivery estimate");
        actions.add("Check driver status");
        actions.add("Consider alternative route");
        
        return actions;
    }
}
