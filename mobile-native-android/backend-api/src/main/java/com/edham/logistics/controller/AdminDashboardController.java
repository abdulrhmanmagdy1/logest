package com.edham.logistics.controller;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.service.AdminDashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @Autowired
    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<AdminDashboardService.DashboardAnalytics>> getDashboardAnalytics(
            @RequestParam(defaultValue = "WEEKLY") AdminDashboardService.DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            AdminDashboardService.DashboardAnalytics analytics = dashboardService.getDashboardAnalytics(filter, startDate, endDate);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<AdminDashboardService.DashboardAnalytics>builder()
                            .success(true).data(analytics).message("Analytics retrieved").timestamp(LocalDateTime.now()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<AdminDashboardService.DashboardAnalytics>builder()
                            .success(false).error(e.getMessage()).timestamp(LocalDateTime.now()).build()
            );
        }
    }
}
