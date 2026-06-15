package com.edham.logistics.fleet;



import com.edham.logistics.model.*;

import com.edham.logistics.repository.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;



import java.time.*;

import java.util.*;

import java.util.stream.Collectors;



/**

 * Fleet Optimization Service - Intelligent fleet management system

 */

@Service

@Transactional

public class FleetOptimizationService {

    

    @Autowired

    private VehicleRepository vehicleRepository;

    

    @Autowired

    private DriverRepository driverRepository;

    

    @Autowired

    private ShipmentRepository shipmentRepository;

    

    @Autowired

    private MaintenanceRepository maintenanceRepository;

    

    @Autowired

    private VehicleTelemetryRepository telemetryRepository;

    

    // Configuration constants

    private static final double IDLE_THRESHOLD_HOURS = 2.0; // 2 hours idle threshold

    private static final double HIGH_UTILIZATION_THRESHOLD = 0.8; // 80% utilization

    private static final double LOW_UTILIZATION_THRESHOLD = 0.3; // 30% utilization

    private static final int MAINTENANCE_REMINDER_DAYS = 7; // 7 days before maintenance

    private static final double FUEL_EFFICIENCY_THRESHOLD = 0.85; // 85% of expected efficiency

    

    /**

     * Get comprehensive fleet optimization analysis

     */

    public FleetOptimizationAnalysis getFleetOptimizationAnalysis() {

        FleetOptimizationAnalysis analysis = new FleetOptimizationAnalysis();

        

        // Get all vehicles

        List<Vehicle> allVehicles = vehicleRepository.findAll();

        

        // Analyze each aspect

        analysis.setVehicleUtilization(analyzeVehicleUtilization(allVehicles));

        analysis.setIdleVehicles(detectIdleVehicles(allVehicles));

        analysis.setMaintenanceSuggestions(generateMaintenanceSuggestions(allVehicles));

        analysis.setAssignmentOptimization(optimizeVehicleAssignments());

        analysis.setFleetHealthScore(calculateFleetHealthScore(allVehicles));

        analysis.setOptimizationRecommendations(generateOptimizationRecommendations(allVehicles));

        

        return analysis;

    }

    

    /**

     * Analyze vehicle utilization rates

     */

    public VehicleUtilizationAnalysis analyzeVehicleUtilization(List<Vehicle> vehicles) {

        VehicleUtilizationAnalysis analysis = new VehicleUtilizationAnalysis();

        

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime weekAgo = now.minusWeeks(1);

        LocalDateTime monthAgo = now.minusMonths(1);

        

        List<VehicleUtilization> utilizationList = new ArrayList<>();

        Map<String, Double> utilizationByType = new HashMap<>();

        Map<String, Integer> countByType = new HashMap<>();

        

        double totalUtilization = 0.0;

        int activeVehicles = 0;

        

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getStatus() != VehicleStatus.DECOMMISSIONED) {

                VehicleUtilization utilization = calculateVehicleUtilization(vehicle, weekAgo, now);

                utilizationList.add(utilization);

                

                // Aggregate by type

                String type = vehicle.getType().toString();

                utilizationByType.merge(type, utilization.getWeeklyUtilization(), Double::sum);

                countByType.merge(type, 1, Integer::sum);

                

                totalUtilization += utilization.getWeeklyUtilization();

                activeVehicles++;

            }

        }

        

        // Calculate averages by type

        Map<String, Double> averageUtilizationByType = new HashMap<>();

        utilizationByType.forEach((type, total) -> {

            double average = total / countByType.get(type);

            averageUtilizationByType.put(type, average);

        });

        

        analysis.setVehicleUtilizations(utilizationList);

        analysis.setAverageUtilizationByType(averageUtilizationByType);

        analysis.setOverallUtilization(activeVehicles > 0 ? totalUtilization / activeVehicles : 0.0);

        analysis.setTotalActiveVehicles(activeVehicles);

        analysis.setAnalysisTimestamp(now);

        

        return analysis;

    }

    

    /**

     * Calculate utilization for a specific vehicle

     */

    private VehicleUtilization calculateVehicleUtilization(Vehicle vehicle, LocalDateTime start, LocalDateTime end) {

        VehicleUtilization utilization = new VehicleUtilization();

        utilization.setVehicleId(vehicle.getId());

        utilization.setVehicleType(vehicle.getType());

        utilization.setLicensePlate(vehicle.getLicensePlate());

        

        // Get active shipments for this vehicle in the time period

        List<Shipment> activeShipments = shipmentRepository.findByVehicleIdAndStatusIn(

            vehicle.getId(), 

            Arrays.asList(ShipmentStatus.IN_TRANSIT, ShipmentStatus.LOADED, ShipmentStatus.DELIVERED)

        );

        

        // Filter shipments within the time period

        List<Shipment> periodShipments = activeShipments.stream()

            .filter(s -> {

                LocalDateTime shipmentTime = s.getCreatedAt();

                return !shipmentTime.isBefore(start) && !shipmentTime.isAfter(end);

            })

            .collect(Collectors.toList());

        

        // Calculate total active time

        long totalActiveMinutes = periodShipments.stream()

            .mapToLong(s -> Duration.between(s.getCreatedAt(), s.getUpdatedAt() != null ? s.getUpdatedAt() : LocalDateTime.now()).toMinutes())

            .sum();

        

        long totalPeriodMinutes = Duration.between(start, end).toMinutes();

        double utilizationRate = totalPeriodMinutes > 0 ? (double) totalActiveMinutes / totalPeriodMinutes : 0.0;

        

        utilization.setWeeklyUtilization(utilizationRate);

        utilization.setTotalActiveMinutes(totalActiveMinutes);

        utilization.setTotalShipments(periodShipments.size());

        utilization.setEfficiencyScore(calculateEfficiencyScore(vehicle, periodShipments));

        

        return utilization;

    }

    

    /**

     * Detect idle vehicles

     */

    public List<IdleVehicleDetection> detectIdleVehicles(List<Vehicle> vehicles) {

        List<IdleVehicleDetection> idleVehicles = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime idleThreshold = now.minusHours((long) IDLE_THRESHOLD_HOURS);

        

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {

                // Check last activity

                LocalDateTime lastActivity = getLastVehicleActivity(vehicle.getId());

                

                if (lastActivity != null && lastActivity.isBefore(idleThreshold)) {

                    IdleVehicleDetection detection = new IdleVehicleDetection();

                    detection.setVehicleId(vehicle.getId());

                    detection.setLicensePlate(vehicle.getLicensePlate());

                    detection.setVehicleType(vehicle.getType());

                    detection.setLastActivity(lastActivity);

                    detection.setIdleDuration(Duration.between(lastActivity, now));

                    detection.setLocation(getLastKnownLocation(vehicle.getId()));

                    detection.setPriority(calculateIdlePriority(vehicle, Duration.between(lastActivity, now)));

                    

                    idleVehicles.add(detection);

                }

            }

        }

        

        // Sort by priority (high to low)

        idleVehicles.sort((a, b) -> Double.compare(b.getPriority(), a.getPriority()));

        

        return idleVehicles;

    }

    

    /**

     * Generate maintenance suggestions

     */

    public List<MaintenanceSuggestion> generateMaintenanceSuggestions(List<Vehicle> vehicles) {

        List<MaintenanceSuggestion> suggestions = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getStatus() != VehicleStatus.DECOMMISSIONED) {

                // Check mileage-based maintenance

                if (vehicle.getMileage() > 0 && vehicle.getNextMaintenanceMileage() > 0) {

                    double mileageUntilMaintenance = vehicle.getNextMaintenanceMileage() - vehicle.getMileage();

                    

                    if (mileageUntilMaintenance <= 1000) { // Within 1000 km

                        MaintenanceSuggestion suggestion = new MaintenanceSuggestion();

                        suggestion.setVehicleId(vehicle.getId());

                        suggestion.setLicensePlate(vehicle.getLicensePlate());

                        suggestion.setSuggestionType(MaintenanceSuggestionType.SCHEDULED_MAINTENANCE);

                        suggestion.setPriority(mileageUntilMaintenance <= 100 ? Priority.HIGH : Priority.MEDIUM);

                        suggestion.setDescription("Scheduled maintenance due in " + Math.round(mileageUntilMaintenance) + " km");

                        suggestion.setEstimatedCost(calculateMaintenanceCost(vehicle, MaintenanceType.SCHEDULED));

                        suggestion.setRecommendedDate(now.plusDays(7));

                        

                        suggestions.add(suggestion);

                    }

                }

                

                // Check time-based maintenance

                if (vehicle.getLastMaintenanceDate() != null) {

                    LocalDateTime nextMaintenanceByTime = vehicle.getLastMaintenanceDate().plusMonths(6); // Every 6 months

                    if (nextMaintenanceByTime.isBefore(now.plusDays(MAINTENANCE_REMINDER_DAYS))) {

                        MaintenanceSuggestion suggestion = new MaintenanceSuggestion();

                        suggestion.setVehicleId(vehicle.getId());

                        suggestion.setLicensePlate(vehicle.getLicensePlate());

                        suggestion.setSuggestionType(MaintenanceSuggestionType.TIME_BASED_MAINTENANCE);

                        suggestion.setPriority(Priority.MEDIUM);

                        suggestion.setDescription("Time-based maintenance due");

                        suggestion.setRecommendedDate(nextMaintenanceByTime);

                        suggestion.setEstimatedCost(calculateMaintenanceCost(vehicle, MaintenanceType.SCHEDULED));

                        

                        suggestions.add(suggestion);

                    }

                }

                

                // Check for performance issues

                List<VehicleTelemetry> recentTelemetry = telemetryRepository.findByVehicleIdOrderByTimestampDesc(

                    vehicle.getId(), 

                    java.time.PageRequest.of(0, 100)

                );

                

                if (!recentTelemetry.isEmpty()) {

                    // Check fuel efficiency

                    double avgFuelEfficiency = recentTelemetry.stream()

                        .mapToDouble(VehicleTelemetry::getFuelEfficiency)

                        .average()

                        .orElse(0.0);

                    

                    double expectedEfficiency = getExpectedFuelEfficiency(vehicle.getType());

                    if (avgFuelEfficiency < expectedEfficiency * FUEL_EFFICIENCY_THRESHOLD) {

                        MaintenanceSuggestion suggestion = new MaintenanceSuggestion();

                        suggestion.setVehicleId(vehicle.getId());

                        suggestion.setLicensePlate(vehicle.getLicensePlate());

                        suggestion.setSuggestionType(MaintenanceSuggestionType.PERFORMANCE_ISSUE);

                        suggestion.setPriority(Priority.MEDIUM);

                        suggestion.setDescription("Low fuel efficiency detected: " + 

                            String.format("%.1f", avgFuelEfficiency) + " km/l (expected: " + 

                            String.format("%.1f", expectedEfficiency) + " km/l)");

                        suggestion.setEstimatedCost(calculateMaintenanceCost(vehicle, MaintenanceType.REPAIR));

                        

                        suggestions.add(suggestion);

                    }

                    

                    // Check engine temperature

                    double avgEngineTemp = recentTelemetry.stream()

                        .mapToDouble(VehicleTelemetry::getEngineTemperature)

                        .average()

                        .orElse(0.0);

                    

                    if (avgEngineTemp > 95) { // High engine temperature

                        MaintenanceSuggestion suggestion = new MaintenanceSuggestion();

                        suggestion.setVehicleId(vehicle.getId());

                        suggestion.setLicensePlate(vehicle.getLicensePlate());

                        suggestion.setSuggestionType(MaintenanceSuggestionType.EMERGENCY_MAINTENANCE);

                        suggestion.setPriority(Priority.HIGH);

                        suggestion.setDescription("High engine temperature detected: " + 

                            String.format("%.1f", avgEngineTemp) + "°C");

                        suggestion.setEstimatedCost(calculateMaintenanceCost(vehicle, MaintenanceType.EMERGENCY));

                        

                        suggestions.add(suggestion);

                    }

                }

            }

        }

        

        // Sort by priority

        suggestions.sort((a, b) -> {

            int priorityCompare = b.getPriority().ordinal() - a.getPriority().ordinal();

            if (priorityCompare != 0) return priorityCompare;

            return a.getRecommendedDate().compareTo(b.getRecommendedDate());

        });

        

        return suggestions;

    }

    

    /**

     * Optimize vehicle assignments for pending shipments

     */

    public VehicleAssignmentOptimization optimizeVehicleAssignments() {

        VehicleAssignmentOptimization optimization = new VehicleAssignmentOptimization();

        

        // Get pending shipments

        List<Shipment> pendingShipments = shipmentRepository.findByStatus(ShipmentStatus.CREATED);

        

        // Get available vehicles

        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        

        List<AssignmentSuggestion> suggestions = new ArrayList<>();

        

        for (Shipment shipment : pendingShipments) {

            AssignmentSuggestion suggestion = findBestVehicleAssignment(shipment, availableVehicles);

            if (suggestion != null) {

                suggestions.add(suggestion);

            }

        }

        

        optimization.setPendingShipments(pendingShipments.size());

        optimization.setAvailableVehicles(availableVehicles.size());

        optimization.setAssignmentSuggestions(suggestions);

        optimization.setOptimizationScore(calculateOptimizationScore(suggestions));

        optimization.setAnalysisTimestamp(LocalDateTime.now());

        

        return optimization;

    }

    

    /**

     * Find best vehicle assignment for a shipment

     */

    private AssignmentSuggestion findBestVehicleAssignment(Shipment shipment, List<Vehicle> availableVehicles) {

        AssignmentSuggestion bestSuggestion = null;

        double bestScore = 0.0;

        

        for (Vehicle vehicle : availableVehicles) {

            double score = calculateAssignmentScore(shipment, vehicle);

            

            if (score > bestScore) {

                bestScore = score;

                bestSuggestion = new AssignmentSuggestion();

                bestSuggestion.setShipmentId(shipment.getId());

                bestSuggestion.setVehicleId(vehicle.getId());

                bestSuggestion.setLicensePlate(vehicle.getLicensePlate());

                bestSuggestion.setVehicleType(vehicle.getType());

                bestSuggestion.setScore(score);

                bestSuggestion.setReasoning(getAssignmentReasoning(shipment, vehicle, score));

            }

        }

        

        return bestSuggestion;

    }

    

    /**

     * Calculate assignment score for vehicle-shipment pairing

     */

    private double calculateAssignmentScore(Shipment shipment, Vehicle vehicle) {

        double score = 0.0;

        

        // Capacity match (40% weight)

        if (shipment.getWeight() > 0 && vehicle.getCapacity() > 0) {

            double capacityRatio = (double) shipment.getWeight() / vehicle.getCapacity();

            if (capacityRatio <= 1.0) {

                score += 0.4 * (1.0 - Math.abs(capacityRatio - 0.8)); // Optimal at 80% capacity

            }

        }

        

        // Vehicle type match (30% weight)

        if (isVehicleTypeSuitable(vehicle.getType(), shipment.getCargoType())) {

            score += 0.3;

        }

        

        // Location proximity (20% weight)

        if (vehicle.getCurrentLocation() != null && shipment.getPickupLocation() != null) {

            double distance = calculateDistance(

                vehicle.getCurrentLocation().getLatitude(),

                vehicle.getCurrentLocation().getLongitude(),

                shipment.getPickupLocation().getLatitude(),

                shipment.getPickupLocation().getLongitude()

            );

            // Closer vehicles get higher scores

            score += 0.2 * Math.exp(-distance / 50.0); // Decay over 50km

        }

        

        // Vehicle condition (10% weight)

        if (vehicle.getStatus() == VehicleStatus.AVAILABLE && vehicle.getHealthScore() > 0.7) {

            score += 0.1;

        }

        

        return score;

    }

    

    /**

     * Calculate fleet health score

     */

    public FleetHealthScore calculateFleetHealthScore(List<Vehicle> vehicles) {

        FleetHealthScore score = new FleetHealthScore();

        

        int totalVehicles = vehicles.size();

        if (totalVehicles == 0) {

            return score;

        }

        

        int availableVehicles = 0;

        int vehiclesNeedingMaintenance = 0;

        double totalHealthScore = 0.0;

        double totalUtilization = 0.0;

        

        for (Vehicle vehicle : vehicles) {

            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {

                availableVehicles++;

            }

            

            if (isMaintenanceNeeded(vehicle)) {

                vehiclesNeedingMaintenance++;

            }

            

            totalHealthScore += vehicle.getHealthScore();

            

            // Get recent utilization

            VehicleUtilization utilization = calculateVehicleUtilization(

                vehicle, 

                LocalDateTime.now().minusWeeks(1), 

                LocalDateTime.now()

            );

            totalUtilization += utilization.getWeeklyUtilization();

        }

        

        score.setAvailabilityRate((double) availableVehicles / totalVehicles);

        score.setMaintenanceNeedRate((double) vehiclesNeedingMaintenance / totalVehicles);

        score.setAverageHealthScore(totalHealthScore / totalVehicles);

        score.setAverageUtilization(totalUtilization / totalVehicles);

        

        // Calculate overall health score (0-100)

        double overallScore = (

            score.getAvailabilityRate() * 30 +

            (1.0 - score.getMaintenanceNeedRate()) * 25 +

            score.getAverageHealthScore() * 25 +

            score.getAverageUtilization() * 20

        );

        score.setOverallHealthScore(overallScore);

        

        score.setTotalVehicles(totalVehicles);

        score.setAnalysisTimestamp(LocalDateTime.now());

        

        return score;

    }

    

    /**

     * Generate optimization recommendations

     */

    public List<OptimizationRecommendation> generateOptimizationRecommendations(List<Vehicle> vehicles) {

        List<OptimizationRecommendation> recommendations = new ArrayList<>();

        

        VehicleUtilizationAnalysis utilizationAnalysis = analyzeVehicleUtilization(vehicles);

        List<IdleVehicleDetection> idleVehicles = detectIdleVehicles(vehicles);

        List<MaintenanceSuggestion> maintenanceSuggestions = generateMaintenanceSuggestions(vehicles);

        FleetHealthScore healthScore = calculateFleetHealthScore(vehicles);

        

        // Utilization recommendations

        if (utilizationAnalysis.getOverallUtilization() < LOW_UTILIZATION_THRESHOLD) {

            OptimizationRecommendation recommendation = new OptimizationRecommendation();

            recommendation.setType(RecommendationType.OPTIMIZE_UTILIZATION);

            recommendation.setPriority(Priority.HIGH);

            recommendation.setTitle("Low Fleet Utilization");

            recommendation.setDescription("Overall fleet utilization is " + 

                String.format("%.1f", utilizationAnalysis.getOverallUtilization() * 100) + 

                "%. Consider reducing fleet size or increasing marketing efforts.");

            recommendations.add(recommendation);

        }

        

        // Idle vehicle recommendations

        if (!idleVehicles.isEmpty()) {

            OptimizationRecommendation recommendation = new OptimizationRecommendation();

            recommendation.setType(RecommendationType.REDUCE_IDLE_TIME);

            recommendation.setPriority(Priority.MEDIUM);

            recommendation.setTitle("Idle Vehicles Detected");

            recommendation.setDescription(idleVehicles.size() + " vehicles have been idle for more than " + 

                IDLE_THRESHOLD_HOURS + " hours. Consider reassigning or scheduling maintenance.");

            recommendations.add(recommendation);

        }

        

        // Maintenance recommendations

        long urgentMaintenance = maintenanceSuggestions.stream()

            .filter(s -> s.getPriority() == Priority.HIGH)

            .count();

        

        if (urgentMaintenance > 0) {

            OptimizationRecommendation recommendation = new OptimizationRecommendation();

            recommendation.setType(RecommendationType.MAINTENANCE_SCHEDULING);

            recommendation.setPriority(Priority.HIGH);

            recommendation.setTitle("Urgent Maintenance Required");

            recommendation.setDescription(urgentMaintenance + " vehicles require urgent maintenance to prevent breakdowns.");

            recommendations.add(recommendation);

        }

        

        // Fleet health recommendations

        if (healthScore.getOverallHealthScore() < 70) {

            OptimizationRecommendation recommendation = new OptimizationRecommendation();

            recommendation.setType(RecommendationType.IMPROVE_FLEET_HEALTH);

            recommendation.setPriority(Priority.HIGH);

            recommendation.setTitle("Fleet Health Concern");

            recommendation.setDescription("Overall fleet health score is " + 

                String.format("%.1f", healthScore.getOverallHealthScore()) + 

                ". Implement preventive maintenance program.");

            recommendations.add(recommendation);

        }

        

        return recommendations;

    }

    

    // Helper methods

    private LocalDateTime getLastVehicleActivity(String vehicleId) {

        // Get last shipment or telemetry update

        List<Shipment> recentShipments = shipmentRepository.findByVehicleIdOrderByUpdatedAtDesc(

            vehicleId, java.time.PageRequest.of(0, 1)

        );

        

        if (!recentShipments.isEmpty()) {

            return recentShipments.get(0).getUpdatedAt();

        }

        

        List<VehicleTelemetry> recentTelemetry = telemetryRepository.findByVehicleIdOrderByTimestampDesc(

            vehicleId, java.time.PageRequest.of(0, 1)

        );

        

        return recentTelemetry.isEmpty() ? null : recentTelemetry.get(0).getTimestamp();

    }

    

    private Location getLastKnownLocation(String vehicleId) {

        List<VehicleTelemetry> recentTelemetry = telemetryRepository.findByVehicleIdOrderByTimestampDesc(

            vehicleId, java.time.PageRequest.of(0, 1)

        );

        

        return recentTelemetry.isEmpty() ? null : recentTelemetry.get(0).getLocation();

    }

    

    private double calculateIdlePriority(Vehicle vehicle, Duration idleDuration) {

        double priority = 0.0;

        

        // Base priority on idle duration

        priority += idleDuration.toHours() / 24.0; // 1 point per day

        

        // Higher priority for expensive vehicles

        if (vehicle.getType() == VehicleType.HEAVY_TRUCK) {

            priority += 0.5;

        }

        

        // Lower priority if maintenance is needed

        if (isMaintenanceNeeded(vehicle)) {

            priority -= 0.3;

        }

        

        return Math.max(0.0, priority);

    }

    

    private double calculateEfficiencyScore(Vehicle vehicle, List<Shipment> shipments) {

        if (shipments.isEmpty()) return 0.0;

        

        double totalScore = 0.0;

        for (Shipment shipment : shipments) {

            // Score based on on-time delivery and fuel efficiency

            double score = 0.5; // Base score

            

            if (shipment.getActualDeliveryTime() != null && shipment.getEstimatedDeliveryTime() != null) {

                if (shipment.getActualDeliveryTime().isBefore(shipment.getEstimatedDeliveryTime())) {

                    score += 0.3; // On-time bonus

                }

            }

            

            totalScore += score;

        }

        

        return totalScore / shipments.size();

    }

    

    private double calculateMaintenanceCost(Vehicle vehicle, MaintenanceType type) {

        // Base costs by vehicle type and maintenance type

        Map<VehicleType, Map<MaintenanceType, Double>> costMatrix = Map.of(

            VehicleType.LIGHT_TRUCK, Map.of(

                MaintenanceType.SCHEDULED, 500.0,

                MaintenanceType.REPAIR, 800.0,

                MaintenanceType.EMERGENCY, 1200.0

            ),

            VehicleType.HEAVY_TRUCK, Map.of(

                MaintenanceType.SCHEDULED, 800.0,

                MaintenanceType.REPAIR, 1500.0,

                MaintenanceType.EMERGENCY, 2500.0

            ),

            VehicleType.VAN, Map.of(

                MaintenanceType.SCHEDULED, 300.0,

                MaintenanceType.REPAIR, 500.0,

                MaintenanceType.EMERGENCY, 800.0

            )

        );

        

        return costMatrix.getOrDefault(vehicle.getType(), Map.of()).getOrDefault(type, 500.0);

    }

    

    private double getExpectedFuelEfficiency(VehicleType type) {

        return switch (type) {

            case LIGHT_TRUCK -> 8.0; // km/l

            case HEAVY_TRUCK -> 4.0; // km/l

            case VAN -> 10.0; // km/l

            default -> 6.0; // km/l

        };

    }

    

    private boolean isVehicleTypeSuitable(VehicleType vehicleType, String cargoType) {

        // Simple suitability check (can be enhanced)

        if (cargoType == null) return true;

        

        return switch (vehicleType) {

            case HEAVY_TRUCK -> cargoType.toLowerCase().contains("heavy") || 

                               cargoType.toLowerCase().contains("bulk");

            case VAN -> cargoType.toLowerCase().contains("parcel") || 

                       cargoType.toLowerCase().contains("package");

            case LIGHT_TRUCK -> true; // General purpose

        };

    }

    

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

        final int R = 6371; // Earth's radius in kilometers

        

        double latDistance = Math.toRadians(lat2 - lat1);

        double lngDistance = Math.toRadians(lng2 - lng1);

        

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)

                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))

                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        

        return R * c;

    }

    

    private String getAssignmentReasoning(Shipment shipment, Vehicle vehicle, double score) {

        StringBuilder reasoning = new StringBuilder();

        

        reasoning.append("Score: ").append(String.format("%.2f", score)).append(". ");

        

        if (shipment.getWeight() > 0 && vehicle.getCapacity() > 0) {

            double capacityRatio = (double) shipment.getWeight() / vehicle.getCapacity();

            reasoning.append("Capacity utilization: ").append(String.format("%.1f", capacityRatio * 100)).append("%. ");

        }

        

        reasoning.append("Vehicle type: ").append(vehicle.getType()).append(". ");

        

        return reasoning.toString();

    }

    

    private boolean isMaintenanceNeeded(Vehicle vehicle) {

        // Check if maintenance is needed based on mileage or time

        if (vehicle.getMileage() > 0 && vehicle.getNextMaintenanceMileage() > 0) {

            if (vehicle.getMileage() >= vehicle.getNextMaintenanceMileage()) {

                return true;

            }

        }

        

        if (vehicle.getLastMaintenanceDate() != null) {

            LocalDateTime nextMaintenance = vehicle.getLastMaintenanceDate().plusMonths(6);

            if (LocalDateTime.now().isAfter(nextMaintenance)) {

                return true;

            }

        }

        

        return false;

    }

    

    private double calculateOptimizationScore(List<AssignmentSuggestion> suggestions) {

        if (suggestions.isEmpty()) return 0.0;

        

        return suggestions.stream()

            .mapToDouble(AssignmentSuggestion::getScore)

            .average()

            .orElse(0.0);

    }

}

