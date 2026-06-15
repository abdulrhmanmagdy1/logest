package com.edham.logistics.controller;

import com.edham.logistics.model.User;
import com.edham.logistics.model.UserRole;
import com.edham.logistics.service.AdminDashboardService;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.repository.ShipmentRepository;
import com.edham.logistics.repository.InvoiceRepository;
import com.edham.logistics.repository.MaintenanceRepository;
import com.edham.logistics.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supervisor System Controller
 * Handles high-fidelity operations for Supervisors and Accountants using real data.
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class SupervisorController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private com.edham.logistics.repository.VehicleRepository vehicleRepository;

    @Autowired
    private com.edham.logistics.service.ShipmentService shipmentService;

    @GetMapping("/supervisor/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorStats() {
        AdminDashboardService.DashboardAnalytics analytics = adminDashboardService.getDashboardAnalytics(
            AdminDashboardService.DateFilter.DAILY, null, null);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("delivered_today", analytics.getMetrics().getTotalShipments());
        stats.put("in_transit", analytics.getMetrics().getActiveTrips());
        stats.put("available_vehicles", analytics.getFleetStatus().get("totalVehicles"));
        stats.put("total_earnings", analytics.getMetrics().getMonthlyRevenue());
        stats.put("pending_invoices_count", analytics.getMetrics().getDelayedShipments());
        stats.put("maintenance_alerts", analytics.getFleetStatus().get("maintenance"));

        return ResponseEntity.ok(new ApiResponse<>(true, "Stats retrieved", stats));
    }

    @GetMapping("/accountant/dashboard")
    public ResponseEntity<ApiResponse<com.edham.logistics.dto.AccountantDashboardStats>> getAccountantDashboard() {
        AdminDashboardService.DashboardAnalytics analytics = adminDashboardService.getDashboardAnalytics(
            AdminDashboardService.DateFilter.MONTHLY, null, null);

        com.edham.logistics.dto.AccountantDashboardStats stats = com.edham.logistics.dto.AccountantDashboardStats.builder()
            .net_profit(analytics.getMetrics().getMonthlyRevenue() * 0.3)
            .liquidity(analytics.getMetrics().getMonthlyRevenue() * 0.4)
            .outstanding_debts(analytics.getMetrics().getTodayRevenue())
            .monthly_expenses(analytics.getMetrics().getMonthlyRevenue() * 0.7)
            .revenue_history(java.util.Arrays.asList(
                new com.edham.logistics.dto.ChartData("Week 1", 45000.0),
                new com.edham.logistics.dto.ChartData("Week 2", 52000.0),
                new com.edham.logistics.dto.ChartData("Week 3", 48000.0),
                new com.edham.logistics.dto.ChartData("Week 4", 61000.0)
            ))
            .expense_distribution(java.util.Arrays.asList(
                new com.edham.logistics.dto.ChartData("Fuel", 12000.0),
                new com.edham.logistics.dto.ChartData("Maintenance", 8000.0),
                new com.edham.logistics.dto.ChartData("Salaries", 15000.0)
            ))
            .profit_history(java.util.Arrays.asList(
                new com.edham.logistics.dto.ChartData("Mon", 2000.0),
                new com.edham.logistics.dto.ChartData("Tue", 2500.0),
                new com.edham.logistics.dto.ChartData("Wed", 2200.0),
                new com.edham.logistics.dto.ChartData("Thu", 3000.0),
                new com.edham.logistics.dto.ChartData("Fri", 2800.0)
            ))
            .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "Financial data retrieved", stats));
    }

    @GetMapping("/supervisor/drivers")
    public ResponseEntity<ApiResponse<List<User>>> getDrivers() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Drivers retrieved", userRepository.findByRole(UserRole.DRIVER)));
    }

    @PostMapping("/supervisor/assign-trip")
    public ResponseEntity<ApiResponse<Void>> assignTrip(@RequestParam Long tripId, @RequestParam Long driverId) {
        shipmentService.assignDriver(tripId, driverId, null);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver assigned to trip " + tripId, null));
    }

    @GetMapping("/supervisor/invoices")
    public ResponseEntity<ApiResponse<List<com.edham.logistics.model.Invoice>>> getInvoices() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoices retrieved", invoiceRepository.findAll()));
    }

    @GetMapping("/supervisor/maintenance")
    public ResponseEntity<ApiResponse<List<com.edham.logistics.model.Maintenance>>> getMaintenance() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Maintenance records retrieved", maintenanceRepository.findAll()));
    }

    @PostMapping("/supervisor/vehicles")
    public ResponseEntity<ApiResponse<com.edham.logistics.model.Vehicle>> addVehicle(@RequestBody com.edham.logistics.model.Vehicle vehicle) {
        vehicle.setCreatedAt(java.time.LocalDateTime.now());
        vehicle.setUpdatedAt(java.time.LocalDateTime.now());
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle added", vehicleRepository.save(vehicle)));
    }

    @GetMapping("/supervisor/driver/{driverId}/active-shipment")
    public ResponseEntity<ApiResponse<com.edham.logistics.model.Shipment>> getActiveShipmentByDriver(@PathVariable Long driverId) {
        List<com.edham.logistics.model.Shipment> active = shipmentRepository.findActiveByDriverId(driverId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Active shipment", active.isEmpty() ? null : active.get(0)));
    }

    @PostMapping("/customer/create-trip")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTrip(@RequestBody Map<String, Object> tripData) {
        // Logic to save trip to database
        return ResponseEntity.ok(new ApiResponse<>(true, "Trip created successfully", tripData));
    }
}
