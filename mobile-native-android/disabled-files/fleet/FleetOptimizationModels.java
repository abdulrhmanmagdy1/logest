package com.edham.logistics.fleet;

import com.edham.logistics.model.*;
import java.time.*;
import java.util.*;

/**
 * Fleet Optimization Data Models
 */
public class FleetOptimizationModels {
    
    /**
     * Main fleet optimization analysis container
     */
    public static class FleetOptimizationAnalysis {
        private VehicleUtilizationAnalysis vehicleUtilization;
        private List<IdleVehicleDetection> idleVehicles;
        private List<MaintenanceSuggestion> maintenanceSuggestions;
        private VehicleAssignmentOptimization assignmentOptimization;
        private FleetHealthScore fleetHealthScore;
        private List<OptimizationRecommendation> optimizationRecommendations;
        private LocalDateTime analysisTimestamp;
        
        // Getters and Setters
        public VehicleUtilizationAnalysis getVehicleUtilization() { return vehicleUtilization; }
        public void setVehicleUtilization(VehicleUtilizationAnalysis vehicleUtilization) { this.vehicleUtilization = vehicleUtilization; }
        
        public List<IdleVehicleDetection> getIdleVehicles() { return idleVehicles; }
        public void setIdleVehicles(List<IdleVehicleDetection> idleVehicles) { this.idleVehicles = idleVehicles; }
        
        public List<MaintenanceSuggestion> getMaintenanceSuggestions() { return maintenanceSuggestions; }
        public void setMaintenanceSuggestions(List<MaintenanceSuggestion> maintenanceSuggestions) { this.maintenanceSuggestions = maintenanceSuggestions; }
        
        public VehicleAssignmentOptimization getAssignmentOptimization() { return assignmentOptimization; }
        public void setAssignmentOptimization(VehicleAssignmentOptimization assignmentOptimization) { this.assignmentOptimization = assignmentOptimization; }
        
        public FleetHealthScore getFleetHealthScore() { return fleetHealthScore; }
        public void setFleetHealthScore(FleetHealthScore fleetHealthScore) { this.fleetHealthScore = fleetHealthScore; }
        
        public List<OptimizationRecommendation> getOptimizationRecommendations() { return optimizationRecommendations; }
        public void setOptimizationRecommendations(List<OptimizationRecommendation> optimizationRecommendations) { this.optimizationRecommendations = optimizationRecommendations; }
        
        public LocalDateTime getAnalysisTimestamp() { return analysisTimestamp; }
        public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) { this.analysisTimestamp = analysisTimestamp; }
    }
    
    /**
     * Vehicle utilization analysis
     */
    public static class VehicleUtilizationAnalysis {
        private List<VehicleUtilization> vehicleUtilizations;
        private Map<String, Double> averageUtilizationByType;
        private double overallUtilization;
        private int totalActiveVehicles;
        private LocalDateTime analysisTimestamp;
        
        // Getters and Setters
        public List<VehicleUtilization> getVehicleUtilizations() { return vehicleUtilizations; }
        public void setVehicleUtilizations(List<VehicleUtilization> vehicleUtilizations) { this.vehicleUtilizations = vehicleUtilizations; }
        
        public Map<String, Double> getAverageUtilizationByType() { return averageUtilizationByType; }
        public void setAverageUtilizationByType(Map<String, Double> averageUtilizationByType) { this.averageUtilizationByType = averageUtilizationByType; }
        
        public double getOverallUtilization() { return overallUtilization; }
        public void setOverallUtilization(double overallUtilization) { this.overallUtilization = overallUtilization; }
        
        public int getTotalActiveVehicles() { return totalActiveVehicles; }
        public void setTotalActiveVehicles(int totalActiveVehicles) { this.totalActiveVehicles = totalActiveVehicles; }
        
        public LocalDateTime getAnalysisTimestamp() { return analysisTimestamp; }
        public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) { this.analysisTimestamp = analysisTimestamp; }
    }
    
    /**
     * Individual vehicle utilization data
     */
    public static class VehicleUtilization {
        private String vehicleId;
        private VehicleType vehicleType;
        private String licensePlate;
        private double weeklyUtilization;
        private long totalActiveMinutes;
        private int totalShipments;
        private double efficiencyScore;
        
        // Getters and Setters
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public VehicleType getVehicleType() { return vehicleType; }
        public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
        
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        
        public double getWeeklyUtilization() { return weeklyUtilization; }
        public void setWeeklyUtilization(double weeklyUtilization) { this.weeklyUtilization = weeklyUtilization; }
        
        public long getTotalActiveMinutes() { return totalActiveMinutes; }
        public void setTotalActiveMinutes(long totalActiveMinutes) { this.totalActiveMinutes = totalActiveMinutes; }
        
        public int getTotalShipments() { return totalShipments; }
        public void setTotalShipments(int totalShipments) { this.totalShipments = totalShipments; }
        
        public double getEfficiencyScore() { return efficiencyScore; }
        public void setEfficiencyScore(double efficiencyScore) { this.efficiencyScore = efficiencyScore; }
    }
    
    /**
     * Idle vehicle detection
     */
    public static class IdleVehicleDetection {
        private String vehicleId;
        private String licensePlate;
        private VehicleType vehicleType;
        private LocalDateTime lastActivity;
        private Duration idleDuration;
        private Location location;
        private double priority;
        
        // Getters and Setters
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        
        public VehicleType getVehicleType() { return vehicleType; }
        public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
        
        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
        
        public Duration getIdleDuration() { return idleDuration; }
        public void setIdleDuration(Duration idleDuration) { this.idleDuration = idleDuration; }
        
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
        
        public double getPriority() { return priority; }
        public void setPriority(double priority) { this.priority = priority; }
    }
    
    /**
     * Maintenance suggestion
     */
    public static class MaintenanceSuggestion {
        private String vehicleId;
        private String licensePlate;
        private MaintenanceSuggestionType suggestionType;
        private Priority priority;
        private String description;
        private double estimatedCost;
        private LocalDateTime recommendedDate;
        
        // Getters and Setters
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        
        public MaintenanceSuggestionType getSuggestionType() { return suggestionType; }
        public void setSuggestionType(MaintenanceSuggestionType suggestionType) { this.suggestionType = suggestionType; }
        
        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public double getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
        
        public LocalDateTime getRecommendedDate() { return recommendedDate; }
        public void setRecommendedDate(LocalDateTime recommendedDate) { this.recommendedDate = recommendedDate; }
    }
    
    /**
     * Vehicle assignment optimization
     */
    public static class VehicleAssignmentOptimization {
        private int pendingShipments;
        private int availableVehicles;
        private List<AssignmentSuggestion> assignmentSuggestions;
        private double optimizationScore;
        private LocalDateTime analysisTimestamp;
        
        // Getters and Setters
        public int getPendingShipments() { return pendingShipments; }
        public void setPendingShipments(int pendingShipments) { this.pendingShipments = pendingShipments; }
        
        public int getAvailableVehicles() { return availableVehicles; }
        public void setAvailableVehicles(int availableVehicles) { this.availableVehicles = availableVehicles; }
        
        public List<AssignmentSuggestion> getAssignmentSuggestions() { return assignmentSuggestions; }
        public void setAssignmentSuggestions(List<AssignmentSuggestion> assignmentSuggestions) { this.assignmentSuggestions = assignmentSuggestions; }
        
        public double getOptimizationScore() { return optimizationScore; }
        public void setOptimizationScore(double optimizationScore) { this.optimizationScore = optimizationScore; }
        
        public LocalDateTime getAnalysisTimestamp() { return analysisTimestamp; }
        public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) { this.analysisTimestamp = analysisTimestamp; }
    }
    
    /**
     * Assignment suggestion
     */
    public static class AssignmentSuggestion {
        private String shipmentId;
        private String vehicleId;
        private String licensePlate;
        private VehicleType vehicleType;
        private double score;
        private String reasoning;
        
        // Getters and Setters
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        
        public VehicleType getVehicleType() { return vehicleType; }
        public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    }
    
    /**
     * Fleet health score
     */
    public static class FleetHealthScore {
        private double availabilityRate;
        private double maintenanceNeedRate;
        private double averageHealthScore;
        private double averageUtilization;
        private double overallHealthScore;
        private int totalVehicles;
        private LocalDateTime analysisTimestamp;
        
        // Getters and Setters
        public double getAvailabilityRate() { return availabilityRate; }
        public void setAvailabilityRate(double availabilityRate) { this.availabilityRate = availabilityRate; }
        
        public double getMaintenanceNeedRate() { return maintenanceNeedRate; }
        public void setMaintenanceNeedRate(double maintenanceNeedRate) { this.maintenanceNeedRate = maintenanceNeedRate; }
        
        public double getAverageHealthScore() { return averageHealthScore; }
        public void setAverageHealthScore(double averageHealthScore) { this.averageHealthScore = averageHealthScore; }
        
        public double getAverageUtilization() { return averageUtilization; }
        public void setAverageUtilization(double averageUtilization) { this.averageUtilization = averageUtilization; }
        
        public double getOverallHealthScore() { return overallHealthScore; }
        public void setOverallHealthScore(double overallHealthScore) { this.overallHealthScore = overallHealthScore; }
        
        public int getTotalVehicles() { return totalVehicles; }
        public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }
        
        public LocalDateTime getAnalysisTimestamp() { return analysisTimestamp; }
        public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) { this.analysisTimestamp = analysisTimestamp; }
    }
    
    /**
     * Optimization recommendation
     */
    public static class OptimizationRecommendation {
        private RecommendationType type;
        private Priority priority;
        private String title;
        private String description;
        
        // Getters and Setters
        public RecommendationType getType() { return type; }
        public void setType(RecommendationType type) { this.type = type; }
        
        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * Fleet dashboard summary
     */
    public static class FleetDashboardSummary {
        private int totalVehicles;
        private int activeVehicles;
        private int idleVehicles;
        private int maintenanceRequired;
        private double overallUtilization;
        private double fleetHealthScore;
        private int pendingAssignments;
        
        // Getters and Setters
        public int getTotalVehicles() { return totalVehicles; }
        public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }
        
        public int getActiveVehicles() { return activeVehicles; }
        public void setActiveVehicles(int activeVehicles) { this.activeVehicles = activeVehicles; }
        
        public int getIdleVehicles() { return idleVehicles; }
        public void setIdleVehicles(int idleVehicles) { this.idleVehicles = idleVehicles; }
        
        public int getMaintenanceRequired() { return maintenanceRequired; }
        public void setMaintenanceRequired(int maintenanceRequired) { this.maintenanceRequired = maintenanceRequired; }
        
        public double getOverallUtilization() { return overallUtilization; }
        public void setOverallUtilization(double overallUtilization) { this.overallUtilization = overallUtilization; }
        
        public double getFleetHealthScore() { return fleetHealthScore; }
        public void setFleetHealthScore(double fleetHealthScore) { this.fleetHealthScore = fleetHealthScore; }
        
        public int getPendingAssignments() { return pendingAssignments; }
        public void setPendingAssignments(int pendingAssignments) { this.pendingAssignments = pendingAssignments; }
    }
    
    /**
     * Assignment request
     */
    public static class AssignmentRequest {
        private String shipmentId;
        private String vehicleId;
        private String assignedBy;
        private LocalDateTime assignedAt;
        
        // Getters and Setters
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public String getAssignedBy() { return assignedBy; }
        public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
        
        public LocalDateTime getAssignedAt() { return assignedAt; }
        public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    }
    
    /**
     * Assignment result
     */
    public static class AssignmentResult {
        private boolean success;
        private String message;
        private String assignmentId;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getAssignmentId() { return assignmentId; }
        public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    }
    
    /**
     * Maintenance schedule request
     */
    public static class MaintenanceScheduleRequest {
        private String vehicleId;
        private MaintenanceType maintenanceType;
        private LocalDateTime scheduledDate;
        private String scheduledBy;
        private String notes;
        
        // Getters and Setters
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        
        public MaintenanceType getMaintenanceType() { return maintenanceType; }
        public void setMaintenanceType(MaintenanceType maintenanceType) { this.maintenanceType = maintenanceType; }
        
        public LocalDateTime getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
        
        public String getScheduledBy() { return scheduledBy; }
        public void setScheduledBy(String scheduledBy) { this.scheduledBy = scheduledBy; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    /**
     * Maintenance schedule result
     */
    public static class MaintenanceScheduleResult {
        private boolean success;
        private String message;
        private String maintenanceId;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getMaintenanceId() { return maintenanceId; }
        public void setMaintenanceId(String maintenanceId) { this.maintenanceId = maintenanceId; }
    }
    
    /**
     * Enums for fleet optimization
     */
    public enum MaintenanceSuggestionType {
        SCHEDULED_MAINTENANCE,
        TIME_BASED_MAINTENANCE,
        PERFORMANCE_ISSUE,
        EMERGENCY_MAINTENANCE
    }
    
    public enum RecommendationType {
        OPTIMIZE_UTILIZATION,
        REDUCE_IDLE_TIME,
        MAINTENANCE_SCHEDULING,
        IMPROVE_FLEET_HEALTH,
        FLEET_RIGHTSIZING
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
