// package com.edham.logistics.fleet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Fleet Optimization Controller - REST API for fleet management
 */
@RestController
@RequestMapping("/api/fleet")
@CrossOrigin(origins = "*")
public class FleetOptimizationController {
    
    @Autowired
    private FleetOptimizationService fleetOptimizationService;
    
    /**
     * Get comprehensive fleet optimization analysis
     */
    @GetMapping("/optimization")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<FleetOptimizationAnalysis> getFleetOptimizationAnalysis() {
        FleetOptimizationAnalysis analysis = fleetOptimizationService.getFleetOptimizationAnalysis();
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Get vehicle utilization analysis
     */
    @GetMapping("/utilization")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<VehicleUtilizationAnalysis> getVehicleUtilization() {
        VehicleUtilizationAnalysis analysis = fleetOptimizationService.analyzeVehicleUtilization(
            fleetOptimizationService.getAllVehicles()
        );
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Get idle vehicles
     */
    @GetMapping("/idle-vehicles")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<List<IdleVehicleDetection>> getIdleVehicles() {
        List<IdleVehicleDetection> idleVehicles = fleetOptimizationService.detectIdleVehicles(
            fleetOptimizationService.getAllVehicles()
        );
        return ResponseEntity.ok(idleVehicles);
    }
    
    /**
     * Get maintenance suggestions
     */
    @GetMapping("/maintenance-suggestions")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<List<MaintenanceSuggestion>> getMaintenanceSuggestions() {
        List<MaintenanceSuggestion> suggestions = fleetOptimizationService.generateMaintenanceSuggestions(
            fleetOptimizationService.getAllVehicles()
        );
        return ResponseEntity.ok(suggestions);
    }
    
    /**
     * Get vehicle assignment optimization
     */
    @GetMapping("/assignment-optimization")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    public ResponseEntity<VehicleAssignmentOptimization> getAssignmentOptimization() {
        VehicleAssignmentOptimization optimization = fleetOptimizationService.optimizeVehicleAssignments();
        return ResponseEntity.ok(optimization);
    }
    
    /**
     * Get fleet health score
     */
    @GetMapping("/health-score")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<FleetHealthScore> getFleetHealthScore() {
        FleetHealthScore score = fleetOptimizationService.calculateFleetHealthScore(
            fleetOptimizationService.getAllVehicles()
        );
        return ResponseEntity.ok(score);
    }
    
    /**
     * Get optimization recommendations
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    public ResponseEntity<List<OptimizationRecommendation>> getOptimizationRecommendations() {
        List<OptimizationRecommendation> recommendations = fleetOptimizationService.generateOptimizationRecommendations(
            fleetOptimizationService.getAllVehicles()
        );
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * Get specific vehicle utilization
     */
    @GetMapping("/vehicles/{vehicleId}/utilization")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP') or hasRole('DRIVER')")
    public ResponseEntity<VehicleUtilization> getVehicleUtilization(
            @PathVariable String vehicleId,
            @RequestParam(defaultValue = "7") int days
    ) {
        // This would need to be implemented in the service
        // For now, return a placeholder
        return ResponseEntity.ok(new VehicleUtilization());
    }
    
    /**
     * Assign vehicle to shipment
     */
    @PostMapping("/assignments")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentResult> assignVehicle(@RequestBody AssignmentRequest request) {
        // This would implement the actual assignment logic
        // For now, return a placeholder response
        AssignmentResult result = new AssignmentResult();
        result.setSuccess(true);
        result.setMessage("Vehicle assigned successfully");
        return ResponseEntity.ok(result);
    }
    
    /**
     * Schedule maintenance
     */
    @PostMapping("/maintenance/schedule")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<MaintenanceScheduleResult> scheduleMaintenance(@RequestBody MaintenanceScheduleRequest request) {
        // This would implement the actual maintenance scheduling
        // For now, return a placeholder response
        MaintenanceScheduleResult result = new MaintenanceScheduleResult();
        result.setSuccess(true);
        result.setMessage("Maintenance scheduled successfully");
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get fleet dashboard summary
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN') or hasRole('WORKSHOP')")
    public ResponseEntity<FleetDashboardSummary> getFleetDashboard() {
        FleetDashboardSummary summary = new FleetDashboardSummary();
        
        // Get comprehensive analysis
        FleetOptimizationAnalysis analysis = fleetOptimizationService.getFleetOptimizationAnalysis();
        
        summary.setTotalVehicles(fleetOptimizationService.getAllVehicles().size());
        summary.setActiveVehicles(analysis.getVehicleUtilization().getTotalActiveVehicles());
        summary.setIdleVehicles(analysis.getIdleVehicles().size());
        summary.setMaintenanceRequired(analysis.getMaintenanceSuggestions().stream()
            .filter(s -> s.getPriority().toString().equals("HIGH"))
            .count());
        summary.setOverallUtilization(analysis.getVehicleUtilization().getOverallUtilization());
        summary.setFleetHealthScore(analysis.getFleetHealthScore().getOverallHealthScore());
        summary.setPendingAssignments(analysis.getAssignmentOptimization().getPendingShipments());
        
        return ResponseEntity.ok(summary);
    }
}
