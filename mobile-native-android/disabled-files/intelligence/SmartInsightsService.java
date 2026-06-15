// // package com.edham.logistics.intelligence;



import com.edham.logistics.model.*;

import com.edham.logistics.repository.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

import java.time.Duration;

import java.time.temporal.ChronoUnit;

import java.util.*;

import java.util.stream.Collectors;



/**

 * Smart Insights Service - Business Intelligence Layer

 * Uses rule-based logic for predictions and recommendations

 */

@Service

@Slf4j

public class SmartInsightsService {



    private final ShipmentRepository shipmentRepository;

    private final UserRepository userRepository;

    private final VehicleRepository vehicleRepository;

    private final EmergencyRepository emergencyRepository;

    private final InvoiceRepository invoiceRepository;



    @Autowired

    public SmartInsightsService(ShipmentRepository shipmentRepository,

                              UserRepository userRepository,

                              VehicleRepository vehicleRepository,

                              EmergencyRepository emergencyRepository,

                              InvoiceRepository invoiceRepository) {

        this.shipmentRepository = shipmentRepository;

        this.userRepository = userRepository;

        this.vehicleRepository = vehicleRepository;

        this.emergencyRepository = emergencyRepository;

        this.invoiceRepository = invoiceRepository;

    }



    /**

     * Predict delayed shipments based on historical data and current conditions

     */

    public DelayPrediction predictDelayedShipment(Shipment shipment) {

        try {

            log.debug("Predicting delay for shipment: {}", shipment.getTrackingNumber());

            

            List<DelayFactor> factors = new ArrayList<>();

            double delayProbability = 0.0;

            String predictedDelayReason = "";

            

            // Factor 1: Historical route performance

            double routeDelayRate = calculateRouteDelayRate(shipment.getOrigin(), shipment.getDestination());

            if (routeDelayRate > 0.3) {

                factors.add(new DelayFactor("HIGH_ROUTE_DELAY_RATE", routeDelayRate, "Route has 30%+ historical delay rate"));

                delayProbability += routeDelayRate * 0.3;

                predictedDelayReason = "ROUTE_HISTORY";

            }

            

            // Factor 2: Weather conditions (simplified - would integrate with weather API)

            boolean isBadWeather = isBadWeatherCondition(shipment.getOrigin());

            if (isBadWeather) {

                factors.add(new DelayFactor("BAD_WEATHER", 0.4, "Poor weather conditions detected"));

                delayProbability += 0.4;

                predictedDelayReason = "WEATHER";

            }

            

            // Factor 3: Driver workload

            double driverWorkload = calculateDriverWorkload(shipment.getDriverId());

            if (driverWorkload > 0.8) {

                factors.add(new DelayFactor("HIGH_DRIVER_WORKLOAD", driverWorkload, "Driver has 80%+ workload"));

                delayProbability += driverWorkload * 0.2;

                if (predictedDelayReason.isEmpty()) predictedDelayReason = "DRIVER_WORKLOAD";

            }

            

            // Factor 4: Vehicle condition

            double vehicleRisk = calculateVehicleRisk(shipment.getVehicleId());

            if (vehicleRisk > 0.5) {

                factors.add(new DelayFactor("VEHICLE_RISK", vehicleRisk, "Vehicle has maintenance issues"));

                delayProbability += vehicleRisk * 0.25;

                if (predictedDelayReason.isEmpty()) predictedDelayReason = "VEHICLE_CONDITION";

            }

            

            // Factor 5: Time of day

            boolean isRushHour = isRushHour(LocalDateTime.now());

            if (isRushHour) {

                factors.add(new DelayFactor("RUSH_HOUR", 0.2, "Current time is rush hour"));

                delayProbability += 0.2;

                if (predictedDelayReason.isEmpty()) predictedDelayReason = "TRAFFIC";

            }

            

            // Factor 6: Distance complexity

            double distanceComplexity = calculateDistanceComplexity(shipment.getOrigin(), shipment.getDestination());

            if (distanceComplexity > 0.7) {

                factors.add(new DelayFactor("LONG_DISTANCE", distanceComplexity, "Long/complex route"));

                delayProbability += distanceComplexity * 0.15;

                if (predictedDelayReason.isEmpty()) predictedDelayReason = "DISTANCE";

            }

            

            // Cap probability at 95%

            delayProbability = Math.min(delayProbability, 0.95);

            

            // Calculate estimated delay hours

            int estimatedDelayHours = calculateEstimatedDelayHours(delayProbability, factors);

            

            DelayPrediction prediction = new DelayPrediction(

                shipment.getTrackingNumber(),

                delayProbability,

                predictedDelayReason,

                estimatedDelayHours,

                factors,

                getDelayRecommendation(delayProbability, predictedDelayReason)

            );

            

            log.info("Delay prediction completed for {}: {}% probability", 

                    shipment.getTrackingNumber(), delayProbability * 100);

            

            return prediction;

            

        } catch (Exception e) {

            log.error("Error predicting delay for shipment: {}", shipment.getTrackingNumber(), e);

            return new DelayPrediction(shipment.getTrackingNumber(), 0.0, "ERROR", 0, 

                                     new ArrayList<>(), "Unable to predict due to system error");

        }

    }



    /**

     * Suggest optimal driver allocation for new shipment

     */

    public DriverAllocationSuggestion suggestDriverAllocation(Shipment newShipment) {

        try {

            log.debug("Suggesting driver allocation for shipment: {}", newShipment.getTrackingNumber());

            

            List<DriverCandidate> candidates = new ArrayList<>();

            

            // Get all available drivers

            List<User> availableDrivers = userRepository.findByRoleAndStatus(UserRole.DRIVER, "ACTIVE");

            

            for (User driver : availableDrivers) {

                DriverCandidate candidate = evaluateDriverForShipment(driver, newShipment);

                candidates.add(candidate);

            }

            

            // Sort by score (highest first)

            candidates.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

            

            // Get top 3 candidates

            List<DriverCandidate> topCandidates = candidates.stream()

                    .limit(3)

                    .collect(Collectors.toList());

            

            // Generate recommendation

            String recommendation = generateDriverRecommendation(topCandidates, newShipment);

            

            DriverAllocationSuggestion suggestion = new DriverAllocationSuggestion(

                newShipment.getTrackingNumber(),

                topCandidates,

                recommendation,

                LocalDateTime.now()

            );

            

            log.info("Driver allocation suggestion generated for {}: {} candidates evaluated", 

                    newShipment.getTrackingNumber(), candidates.size());

            

            return suggestion;

            

        } catch (Exception e) {

            log.error("Error generating driver allocation suggestion", e);

            return new DriverAllocationSuggestion(newShipment.getTrackingNumber(), 

                                               new ArrayList<>(), "Error generating suggestion", 

                                               LocalDateTime.now());

        }

    }



    /**

     * Detect inefficient routes based on performance data

     */

    public List<InefficientRoute> detectInefficientRoutes() {

        try {

            log.debug("Detecting inefficient routes");

            

            List<InefficientRoute> inefficientRoutes = new ArrayList<>();

            

            // Get all completed shipments from last 30 days

            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

            List<Shipment> completedShipments = shipmentRepository.findByStatusAndCreatedAtAfter(

                    ShipmentStatus.DELIVERED, thirtyDaysAgo);

            

            // Group by route

            Map<String, List<Shipment>> routeGroups = completedShipments.stream()

                    .collect(Collectors.groupingBy(s -> s.getOrigin() + " -> " + s.getDestination()));

            

            // Analyze each route

            for (Map.Entry<String, List<Shipment>> entry : routeGroups.entrySet()) {

                String route = entry.getKey();

                List<Shipment> routeShipments = entry.getValue();

                

                if (routeShipments.size() >= 5) { // Only analyze routes with sufficient data

                    InefficientRoute inefficientRoute = analyzeRouteEfficiency(route, routeShipments);

                    if (inefficientRoute.getEfficiencyScore() < 0.7) {

                        inefficientRoutes.add(inefficientRoute);

                    }

                }

            }

            

            // Sort by efficiency score (lowest first)

            inefficientRoutes.sort((a, b) -> Double.compare(a.getEfficiencyScore(), b.getEfficiencyScore()));

            

            log.info("Detected {} inefficient routes", inefficientRoutes.size());

            

            return inefficientRoutes;

            

        } catch (Exception e) {

            log.error("Error detecting inefficient routes", e);

            return new ArrayList<>();

        }

    }



    /**

     * Highlight system bottlenecks based on performance metrics

     */

    public SystemBottlenecks detectSystemBottlenecks() {

        try {

            log.debug("Detecting system bottlenecks");

            

            List<Bottleneck> bottlenecks = new ArrayList<>();

            

            // Bottleneck 1: Driver shortage

            Bottleneck driverShortage = detectDriverShortage();

            if (driverShortage != null) {

                bottlenecks.add(driverShortage);

            }

            

            // Bottleneck 2: Vehicle maintenance backlog

            Bottleneck maintenanceBacklog = detectMaintenanceBacklog();

            if (maintenanceBacklog != null) {

                bottlenecks.add(maintenanceBacklog);

            }

            

            // Bottleneck 3: High delay rate routes

            Bottleneck highDelayRoutes = detectHighDelayRoutes();

            if (highDelayRoutes != null) {

                bottlenecks.add(highDelayRoutes);

            }

            

            // Bottleneck 4: Payment collection issues

            Bottleneck paymentIssues = detectPaymentCollectionIssues();

            if (paymentIssues != null) {

                bottlenecks.add(paymentIssues);

            }

            

            // Bottleneck 5: Emergency response delays

            Bottleneck emergencyDelays = detectEmergencyResponseDelays();

            if (emergencyDelays != null) {

                bottlenecks.add(emergencyDelays);

            }

            

            // Bottleneck 6: Temperature violations

            Bottleneck temperatureViolations = detectTemperatureViolations();

            if (temperatureViolations != null) {

                bottlenecks.add(temperatureViolations);

            }

            

            // Sort by severity (highest first)

            bottlenecks.sort((a, b) -> Double.compare(b.getSeverity(), a.getSeverity()));

            

            SystemBottlenecks systemBottlenecks = new SystemBottlenecks(

                bottlenecks,

                generateSystemHealthScore(bottlenecks),

                LocalDateTime.now()

            );

            

            log.info("Detected {} system bottlenecks", bottlenecks.size());

            

            return systemBottlenecks;

            

        } catch (Exception e) {

            log.error("Error detecting system bottlenecks", e);

            return new SystemBottlenecks(new ArrayList<>(), 0.0, LocalDateTime.now());

        }

    }



    // Helper methods for delay prediction

    private double calculateRouteDelayRate(String origin, String destination) {

        String routeKey = origin + " -> " + destination;

        

        // Get historical shipments for this route

        List<Shipment> routeShipments = shipmentRepository.findByOriginAndDestination(origin, destination);

        

        if (routeShipments.size() < 5) {

            return 0.1; // Default low risk for insufficient data

        }

        

        long delayedCount = routeShipments.stream()

                .filter(s -> s.getExpectedDeliveryTime() != null && 

                           s.getActualDeliveryTime() != null &&

                           s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))

                .count();

        

        return (double) delayedCount / routeShipments.size();

    }



    private boolean isBadWeatherCondition(String location) {

        // Simplified weather check - would integrate with weather API

        // For demo, assume 20% chance of bad weather

        return Math.random() < 0.2;

    }



    private double calculateDriverWorkload(Long driverId) {

        if (driverId == null) return 0.5; // Default medium risk

        

        // Get active shipments for this driver

        List<Shipment> driverShipments = shipmentRepository.findByDriverIdAndStatusIn(

                driverId, Arrays.asList(ShipmentStatus.ASSIGNED, ShipmentStatus.IN_TRANSIT));

        

        // Calculate workload (0-1 scale)

        int maxWorkload = 5; // Maximum shipments a driver can handle

        return Math.min((double) driverShipments.size() / maxWorkload, 1.0);

    }



    private double calculateVehicleRisk(Long vehicleId) {

        if (vehicleId == null) return 0.3; // Default low-medium risk

        

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);

        if (!vehicleOpt.isPresent()) {

            return 0.5; // Default medium risk

        }

        

        Vehicle vehicle = vehicleOpt.get();

        

        // Calculate risk based on vehicle condition

        double risk = 0.0;

        

        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {

            risk += 0.8;

        } else if (vehicle.getStatus() == VehicleStatus.ISSUE_REPORTED) {

            risk += 0.6;

        }

        

        // Add risk based on mileage (simplified)

        if (vehicle.getMileage() > 100000) {

            risk += 0.2;

        }

        

        return Math.min(risk, 1.0);

    }



    private boolean isRushHour(LocalDateTime dateTime) {

        int hour = dateTime.getHour();

        return (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19);

    }



    private double calculateDistanceComplexity(String origin, String destination) {

        // Simplified distance complexity calculation

        // Would use actual distance calculation with mapping API

        String[] originParts = origin.split(" ");

        String[] destParts = destination.split(" ");

        

        // Assume longer route names indicate longer distances

        int totalLength = origin.length() + destination.length();

        

        if (totalLength > 50) return 0.8; // Long distance

        if (totalLength > 30) return 0.6; // Medium distance

        return 0.3; // Short distance

    }



    private int calculateEstimatedDelayHours(double probability, List<DelayFactor> factors) {

        if (probability < 0.3) return 0;

        if (probability < 0.5) return 2;

        if (probability < 0.7) return 4;

        if (probability < 0.9) return 8;

        return 12; // Severe delay

    }



    private String getDelayRecommendation(double probability, String reason) {

        if (probability < 0.3) {

            return "Low risk of delay. Monitor shipment progress.";

        } else if (probability < 0.6) {

            return "Moderate risk of delay. Consider proactive communication with customer.";

        } else {

            return "High risk of delay. Recommend route optimization or driver reallocation.";

        }

    }



    // Helper methods for driver allocation

    private DriverCandidate evaluateDriverForShipment(User driver, Shipment shipment) {

        double score = 0.0;

        List<String> strengths = new ArrayList<>();

        List<String> weaknesses = new ArrayList<>();

        

        // Factor 1: Driver performance score (30%)

        double performanceScore = calculateDriverPerformanceScore(driver.getId());

        score += performanceScore * 0.3;

        if (performanceScore > 0.8) {

            strengths.add("High performance rating");

        } else if (performanceScore < 0.5) {

            weaknesses.add("Low performance rating");

        }

        

        // Factor 2: Current workload (25%)

        double workloadScore = 1.0 - calculateDriverWorkload(driver.getId());

        score += workloadScore * 0.25;

        if (workloadScore > 0.7) {

            strengths.add("Low current workload");

        } else if (workloadScore < 0.3) {

            weaknesses.add("High current workload");

        }

        

        // Factor 3: Route familiarity (20%)

        double routeFamiliarity = calculateRouteFamiliarity(driver.getId(), shipment.getOrigin(), shipment.getDestination());

        score += routeFamiliarity * 0.2;

        if (routeFamiliarity > 0.7) {

            strengths.add("Familiar with route");

        } else if (routeFamiliarity < 0.3) {

            weaknesses.add("Unfamiliar with route");

        }

        

        // Factor 4: Vehicle condition (15%)

        double vehicleCondition = 1.0 - calculateVehicleRisk(driver.getVehicleId());

        score += vehicleCondition * 0.15;

        if (vehicleCondition > 0.8) {

            strengths.add("Good vehicle condition");

        } else if (vehicleCondition < 0.5) {

            weaknesses.add("Vehicle maintenance issues");

        }

        

        // Factor 5: Availability (10%)

        double availabilityScore = driver.getStatus().equals("ACTIVE") ? 1.0 : 0.0;

        score += availabilityScore * 0.1;

        if (availabilityScore < 1.0) {

            weaknesses.add("Not currently active");

        }

        

        return new DriverCandidate(

            driver.getId(),

            driver.getName(),

            score,

            strengths,

            weaknesses,

            generateDriverJustification(score, strengths, weaknesses)

        );

    }



    private double calculateDriverPerformanceScore(Long driverId) {

        // Calculate based on historical performance

        List<Shipment> driverShipments = shipmentRepository.findByDriverId(driverId);

        

        if (driverShipments.size() < 5) {

            return 0.7; // Default score for new drivers

        }

        

        long onTimeDeliveries = driverShipments.stream()

                .filter(s -> s.getExpectedDeliveryTime() != null && 

                           s.getActualDeliveryTime() != null &&

                           !s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))

                .count();

        

        return (double) onTimeDeliveries / driverShipments.size();

    }



    private double calculateRouteFamiliarity(Long driverId, String origin, String destination) {

        // Calculate based on previous shipments on same route

        List<Shipment> driverShipments = shipmentRepository.findByDriverId(driverId);

        

        long routeShipments = driverShipments.stream()

                .filter(s -> origin.equals(s.getOrigin()) && destination.equals(s.getDestination()))

                .count();

        

        if (routeShipments >= 10) return 1.0;

        if (routeShipments >= 5) return 0.8;

        if (routeShipments >= 2) return 0.6;

        if (routeShipments >= 1) return 0.4;

        return 0.1; // No experience with route

    }



    private String generateDriverJustification(double score, List<String> strengths, List<String> weaknesses) {

        if (score > 0.8) {

            return "Excellent candidate with strong performance metrics";

        } else if (score > 0.6) {

            return "Good candidate with some considerations";

        } else if (score > 0.4) {

            return "Acceptable candidate but may need support";

        } else {

            return "Not recommended for this assignment";

        }

    }



    private String generateDriverRecommendation(List<DriverCandidate> candidates, Shipment shipment) {

        if (candidates.isEmpty()) {

            return "No suitable drivers available";

        }

        

        DriverCandidate topCandidate = candidates.get(0);

        

        if (topCandidate.getScore() > 0.8) {

            return String.format("Strongly recommend %s (Score: %.1f) - %s", 

                               topCandidate.getDriverName(), topCandidate.getScore(), 

                               topCandidate.getJustification());

        } else if (topCandidate.getScore() > 0.6) {

            return String.format("Recommend %s (Score: %.1f) - %s", 

                               topCandidate.getDriverName(), topCandidate.getScore(), 

                               topCandidate.getJustification());

        } else {

            return String.format("Consider %s (Score: %.1f) - %s. May need additional support.", 

                               topCandidate.getDriverName(), topCandidate.getScore(), 

                               topCandidate.getJustification());

        }

    }



    // Helper methods for route efficiency

    private InefficientRoute analyzeRouteEfficiency(String route, List<Shipment> shipments) {

        double totalDistance = shipments.stream()

                .mapToDouble(s -> s.getDistance() != null ? s.getDistance() : 100.0)

                .sum();

        

        double totalTime = shipments.stream()

                .mapToLong(s -> {

                    if (s.getActualDeliveryTime() != null && s.getCreatedAt() != null) {

                        return Duration.between(s.getCreatedAt(), s.getActualDeliveryTime()).toHours();

                    }

                    return 24L; // Default 24 hours

                })

                .sum();

        

        double averageSpeed = totalDistance / totalTime;

        double onTimeRate = shipments.stream()

                .filter(s -> s.getExpectedDeliveryTime() != null && 

                           s.getActualDeliveryTime() != null &&

                           !s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))

                .count() / (double) shipments.size();

        

        // Calculate efficiency score (0-1)

        double efficiencyScore = (averageSpeed / 60.0) * 0.5 + onTimeRate * 0.5; // Assuming 60 km/h as optimal

        

        List<String> issues = new ArrayList<>();

        if (averageSpeed < 30) issues.add("Low average speed");

        if (onTimeRate < 0.8) issues.add("Low on-time delivery rate");

        if (totalTime / shipments.size() > 48) issues.add("Long average delivery time");

        

        return new InefficientRoute(

            route,

            shipments.size(),

            efficiencyScore,

            averageSpeed,

            onTimeRate,

            issues,

            generateRouteRecommendation(efficiencyScore, issues)

        );

    }



    private String generateRouteRecommendation(double efficiencyScore, List<String> issues) {

        if (efficiencyScore > 0.8) {

            return "Route is performing well";

        } else if (efficiencyScore > 0.6) {

            return "Route needs minor optimization";

        } else {

            return "Route requires significant optimization: " + String.join(", ", issues);

        }

    }



    // Helper methods for bottleneck detection

    private Bottleneck detectDriverShortage() {

        long totalDrivers = userRepository.countByRole(UserRole.DRIVER);

        long activeDrivers = userRepository.countByRoleAndStatus(UserRole.DRIVER, "ACTIVE");

        long activeShipments = shipmentRepository.countByStatusIn(Arrays.asList(

                ShipmentStatus.ASSIGNED, ShipmentStatus.IN_TRANSIT));

        

        double driverUtilization = (double) activeShipments / activeDrivers;

        

        if (driverUtilization > 0.9) {

            return new Bottleneck(

                "DRIVER_SHORTAGE",

                "High driver utilization",

                driverUtilization,

                String.format("%.1f shipments per active driver (threshold: 0.9)", driverUtilization),

                "Consider hiring more drivers or optimizing routes"

            );

        }

        

        return null;

    }



    private Bottleneck detectMaintenanceBacklog() {

        long totalVehicles = vehicleRepository.count();

        long maintenanceVehicles = vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);

        long issueVehicles = vehicleRepository.countByStatus(VehicleStatus.ISSUE_REPORTED);

        

        double maintenanceRate = (double) (maintenanceVehicles + issueVehicles) / totalVehicles;

        

        if (maintenanceRate > 0.2) {

            return new Bottleneck(

                "MAINTENANCE_BACKLOG",

                "High vehicle maintenance rate",

                maintenanceRate,

                String.format("%.1f%% of vehicles in maintenance (threshold: 20%%)", maintenanceRate * 100),

                "Increase maintenance capacity or vehicle fleet"

            );

        }

        

        return null;

    }



    private Bottleneck detectHighDelayRoutes() {

        // Get routes with highest delay rates

        Map<String, List<Shipment>> routeGroups = shipmentRepository.findAll().stream()

                .filter(s -> s.getExpectedDeliveryTime() != null && s.getActualDeliveryTime() != null)

                .collect(Collectors.groupingBy(s -> s.getOrigin() + " -> " + s.getDestination()));

        

        String worstRoute = "";

        double worstDelayRate = 0.0;

        

        for (Map.Entry<String, List<Shipment>> entry : routeGroups.entrySet()) {

            if (entry.getValue().size() >= 5) {

                long delayedCount = entry.getValue().stream()

                        .filter(s -> s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))

                        .count();

                double delayRate = (double) delayedCount / entry.getValue().size();

                

                if (delayRate > worstDelayRate) {

                    worstDelayRate = delayRate;

                    worstRoute = entry.getKey();

                }

            }

        }

        

        if (worstDelayRate > 0.4) {

            return new Bottleneck(

                "HIGH_DELAY_ROUTES",

                "Routes with excessive delays",

                worstDelayRate,

                String.format("Route '%s' has %.1f%% delay rate", worstRoute, worstDelayRate * 100),

                "Optimize routing or allocate more resources to problematic routes"

            );

        }

        

        return null;

    }



    private Bottleneck detectPaymentCollectionIssues() {

        List<Invoice> unpaidInvoices = invoiceRepository.findByStatus(InvoiceStatus.UNPAID);

        List<Invoice> overdueInvoices = unpaidInvoices.stream()

                .filter(i -> i.getDueDate() != null && i.getDueDate().isBefore(LocalDateTime.now()))

                .collect(Collectors.toList());

        

        double overdueRate = (double) overdueInvoices.size() / unpaidInvoices.size();

        

        if (overdueRate > 0.3) {

            return new Bottleneck(

                "PAYMENT_COLLECTION",

                "High overdue payment rate",

                overdueRate,

                String.format("%.1f%% of invoices are overdue", overdueRate * 100),

                "Strengthen payment collection processes"

            );

        }

        

        return null;

    }



    private Bottleneck detectEmergencyResponseDelays() {

        List<Emergency> recentEmergencies = emergencyRepository.findByCreatedAtAfter(

                LocalDateTime.now().minusDays(7));

        

        if (recentEmergencies.size() >= 5) {

            long delayedResponses = recentEmergencies.stream()

                    .filter(e -> e.getResponseTime() != null && e.getResponseTime() > 30)

                    .count();

            

            double delayRate = (double) delayedResponses / recentEmergencies.size();

            

            if (delayRate > 0.3) {

                return new Bottleneck(

                    "EMERGENCY_RESPONSE",

                    "Slow emergency response times",

                    delayRate,

                    String.format("%.1f%% of emergencies have delayed response", delayRate * 100),

                    "Review emergency response procedures"

                );

            }

        }

        

        return null;

    }



    private Bottleneck detectTemperatureViolations() {

        // This would integrate with cold chain monitoring service

        // For demo, assume some violations exist

        double violationRate = 0.15; // 15% violation rate

        

        if (violationRate > 0.1) {

            return new Bottleneck(

                "TEMPERATURE_VIOLATIONS",

                "High temperature violation rate",

                violationRate,

                String.format("%.1f%% of shipments have temperature violations", violationRate * 100),

                "Review cold chain procedures and equipment"

            );

        }

        

        return null;

    }



    private double generateSystemHealthScore(List<Bottleneck> bottlenecks) {

        if (bottlenecks.isEmpty()) {

            return 1.0; // Perfect health

        }

        

        double totalSeverity = bottlenecks.stream()

                .mapToDouble(Bottleneck::getSeverity)

                .sum();

        

        // Health score decreases with bottleneck severity

        return Math.max(0.0, 1.0 - totalSeverity);

    }



    // Data classes

    public static class DelayPrediction {

        private final String trackingNumber;

        private final double delayProbability;

        private final String predictedReason;

        private final int estimatedDelayHours;

        private final List<DelayFactor> factors;

        private final String recommendation;

        

        public DelayPrediction(String trackingNumber, double delayProbability, String predictedReason, 

                              int estimatedDelayHours, List<DelayFactor> factors, String recommendation) {

            this.trackingNumber = trackingNumber;

            this.delayProbability = delayProbability;

            this.predictedReason = predictedReason;

            this.estimatedDelayHours = estimatedDelayHours;

            this.factors = factors;

            this.recommendation = recommendation;

        }

        

        // Getters

        public String getTrackingNumber() { return trackingNumber; }

        public double getDelayProbability() { return delayProbability; }

        public String getPredictedReason() { return predictedReason; }

        public int getEstimatedDelayHours() { return estimatedDelayHours; }

        public List<DelayFactor> getFactors() { return factors; }

        public String getRecommendation() { return recommendation; }

    }



    public static class DelayFactor {

        private final String factor;

        private final double impact;

        private final String description;

        

        public DelayFactor(String factor, double impact, String description) {

            this.factor = factor;

            this.impact = impact;

            this.description = description;

        }

        

        // Getters

        public String getFactor() { return factor; }

        public double getImpact() { return impact; }

        public String getDescription() { return description; }

    }



    public static class DriverAllocationSuggestion {

        private final String shipmentTrackingNumber;

        private final List<DriverCandidate> candidates;

        private final String recommendation;

        private final LocalDateTime generatedAt;

        

        public DriverAllocationSuggestion(String shipmentTrackingNumber, List<DriverCandidate> candidates, 

                                       String recommendation, LocalDateTime generatedAt) {

            this.shipmentTrackingNumber = shipmentTrackingNumber;

            this.candidates = candidates;

            this.recommendation = recommendation;

            this.generatedAt = generatedAt;

        }

        

        // Getters

        public String getShipmentTrackingNumber() { return shipmentTrackingNumber; }

        public List<DriverCandidate> getCandidates() { return candidates; }

        public String getRecommendation() { return recommendation; }

        public LocalDateTime getGeneratedAt() { return generatedAt; }

    }



    public static class DriverCandidate {

        private final Long driverId;

        private final String driverName;

        private final double score;

        private final List<String> strengths;

        private final List<String> weaknesses;

        private final String justification;

        

        public DriverCandidate(Long driverId, String driverName, double score, List<String> strengths, 

                             List<String> weaknesses, String justification) {

            this.driverId = driverId;

            this.driverName = driverName;

            this.score = score;

            this.strengths = strengths;

            this.weaknesses = weaknesses;

            this.justification = justification;

        }

        

        // Getters

        public Long getDriverId() { return driverId; }

        public String getDriverName() { return driverName; }

        public double getScore() { return score; }

        public List<String> getStrengths() { return strengths; }

        public List<String> getWeaknesses() { return weaknesses; }

        public String getJustification() { return justification; }

    }



    public static class InefficientRoute {

        private final String route;

        private final int shipmentCount;

        private final double efficiencyScore;

        private final double averageSpeed;

        private final double onTimeRate;

        private final List<String> issues;

        private final String recommendation;

        

        public InefficientRoute(String route, int shipmentCount, double efficiencyScore, 

                               double averageSpeed, double onTimeRate, List<String> issues, 

                               String recommendation) {

            this.route = route;

            this.shipmentCount = shipmentCount;

            this.efficiencyScore = efficiencyScore;

            this.averageSpeed = averageSpeed;

            this.onTimeRate = onTimeRate;

            this.issues = issues;

            this.recommendation = recommendation;

        }

        

        // Getters

        public String getRoute() { return route; }

        public int getShipmentCount() { return shipmentCount; }

        public double getEfficiencyScore() { return efficiencyScore; }

        public double getAverageSpeed() { return averageSpeed; }

        public double getOnTimeRate() { return onTimeRate; }

        public List<String> getIssues() { return issues; }

        public String getRecommendation() { return recommendation; }

    }



    public static class Bottleneck {

        private final String type;

        private final String description;

        private final double severity;

        private final String details;

        private final String recommendation;

        

        public Bottleneck(String type, String description, double severity, String details, String recommendation) {

            this.type = type;

            this.description = description;

            this.severity = severity;

            this.details = details;

            this.recommendation = recommendation;

        }

        

        // Getters

        public String getType() { return type; }

        public String getDescription() { return description; }

        public double getSeverity() { return severity; }

        public String getDetails() { return details; }

        public String getRecommendation() { return recommendation; }

    }



    public static class SystemBottlenecks {

        private final List<Bottleneck> bottlenecks;

        private final double systemHealthScore;

        private final LocalDateTime analyzedAt;

        

        public SystemBottlenecks(List<Bottleneck> bottlenecks, double systemHealthScore, LocalDateTime analyzedAt) {

            this.bottlenecks = bottlenecks;

            this.systemHealthScore = systemHealthScore;

            this.analyzedAt = analyzedAt;

        }

        

        // Getters

        public List<Bottleneck> getBottlenecks() { return bottlenecks; }

        public double getSystemHealthScore() { return systemHealthScore; }

        public LocalDateTime getAnalyzedAt() { return analyzedAt; }

    }

}

