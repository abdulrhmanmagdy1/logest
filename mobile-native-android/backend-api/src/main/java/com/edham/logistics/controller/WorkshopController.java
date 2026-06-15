package com.edham.logistics.controller;

import com.edham.logistics.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workshop")
@CrossOrigin(origins = "*")
public class WorkshopController {

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWorkshopStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("groundedTrucks", 3);
        stats.put("readyTrucks", 21);
        stats.put("pendingRepairs", 5);
        stats.put("lowStockItems", 12);
        stats.put("fleetHealthScore", 92.5);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Workshop stats retrieved", stats));
    }

    @PostMapping("/vehicle/{id}/ground")
    public ResponseEntity<ApiResponse<Void>> groundVehicle(@PathVariable String id, @RequestParam String reason) {
        // Logic to ground vehicle in DB
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle " + id + " grounded: " + reason, null));
    }

    @PostMapping("/vehicle/{id}/release")
    public ResponseEntity<ApiResponse<Void>> releaseVehicle(@PathVariable String id) {
        // Logic to release vehicle in DB
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle " + id + " released to service", null));
    }

    @PostMapping("/request-part")
    public ResponseEntity<ApiResponse<Void>> requestPart(
            @RequestParam String partId,
            @RequestParam Integer quantity,
            @RequestParam String priority) {
        // Logic to create part request for Accountant
        return ResponseEntity.ok(new ApiResponse<>(true, "Part request created for " + partId, null));
    }
}
