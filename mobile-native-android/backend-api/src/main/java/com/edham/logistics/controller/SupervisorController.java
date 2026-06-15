package com.edham.logistics.controller;

import com.edham.logistics.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supervisor System Controller
 * Handles high-fidelity operations for Supervisors and Accountants
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class SupervisorController {

    @GetMapping("/supervisor/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("delivered_today", 128);
        stats.put("in_transit", 36);
        stats.put("available_vehicles", 42);
        stats.put("total_earnings", 2400000.0);
        stats.put("pending_invoices_count", 18);
        stats.put("maintenance_alerts", 7);

        return ResponseEntity.ok(new ApiResponse<>(true, "Stats retrieved", stats));
    }

    @GetMapping("/accountant/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAccountantDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("net_profit", 146200.0);
        stats.put("total_revenue", 238500.0);
        stats.put("total_expenses", 92300.0);
        stats.put("outstanding_invoices", 37200.0);
        stats.put("urgent_overdue", 8950.0);

        return ResponseEntity.ok(new ApiResponse<>(true, "Financial data retrieved", stats));
    }

    @GetMapping("/supervisor/drivers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDrivers() {
        List<Map<String, Object>> drivers = new ArrayList<>();
        
        Map<String, Object> d1 = new HashMap<>();
        d1.put("id", "DRV-001");
        d1.put("first_name", "خالد");
        d1.put("last_name", "العتيبي");
        d1.put("email", "khaled@edham.co");
        d1.put("status", "ACTIVE");
        d1.put("plate_number", "أ ب ج 1234");
        drivers.add(d1);

        Map<String, Object> d2 = new HashMap<>();
        d2.put("id", "DRV-002");
        d2.put("first_name", "أحمد");
        d2.put("last_name", "سعد");
        d2.put("email", "ahmed@edham.co");
        d2.put("status", "IN_TRANSIT");
        d2.put("plate_number", "ب ج د 5678");
        drivers.add(d2);

        return ResponseEntity.ok(new ApiResponse<>(true, "Drivers retrieved", drivers));
    }

    @PostMapping("/supervisor/assign-trip")
    public ResponseEntity<ApiResponse<Void>> assignTrip(@RequestParam String tripId, @RequestParam String driverId) {
        // Business logic for assignment
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver assigned to trip " + tripId, null));
    }

    @GetMapping("/supervisor/invoices")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInvoices() {
        List<Map<String, Object>> invoices = new ArrayList<>();
        Map<String, Object> i1 = new HashMap<>();
        i1.put("id", "INV-1045");
        i1.put("clientName", "شركة الأمل للأغذية");
        i1.put("amount", 85000.0);
        i1.put("date", "15 مايو 2026");
        i1.put("status", "PAID");
        invoices.add(i1);

        return ResponseEntity.ok(new ApiResponse<>(true, "Invoices retrieved", invoices));
    }

    @GetMapping("/supervisor/maintenance")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMaintenance() {
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> r1 = new HashMap<>();
        r1.put("vehicleId", "T-01");
        r1.put("serviceType", "تغيير زيت + فلتر");
        r1.put("date", "15 مايو 2026");
        r1.put("cost", 2500.0);
        r1.put("status", "COMPLETED");
        records.add(r1);

        return ResponseEntity.ok(new ApiResponse<>(true, "Maintenance records retrieved", records));
    }

    @PostMapping("/customer/create-trip")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTrip(@RequestBody Map<String, Object> tripData) {
        // Logic to save trip to database
        return ResponseEntity.ok(new ApiResponse<>(true, "Trip created successfully", tripData));
    }
}
