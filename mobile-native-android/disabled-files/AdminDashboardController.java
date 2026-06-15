// // // package com.edham.logistics.analytics;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.analytics.AdminDashboardService.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Admin dashboard controller
 * Provides comprehensive analytics for admin dashboard
 */
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

    /**
     * Get comprehensive dashboard analytics
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<DashboardAnalytics>> getDashboardAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            log.debug("Getting dashboard analytics with filter: {}, period: {} to {}", filter, startDate, endDate);
            
            DashboardAnalytics analytics = dashboardService.getDashboardAnalytics(filter, startDate, endDate);
            
            log.info("Dashboard analytics retrieved successfully for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<DashboardAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Dashboard analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting dashboard analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<DashboardAnalytics>builder()
                            .success(false)
                            .error("Failed to get dashboard analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get shipments analytics only
     */
    @GetMapping("/analytics/shipments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<ShipmentsAnalytics>> getShipmentsAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            ShipmentsAnalytics analytics = dashboardService.getShipmentsAnalytics(dateRange[0], dateRange[1]);
            
            log.debug("Shipments analytics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<ShipmentsAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Shipments analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting shipments analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<ShipmentsAnalytics>builder()
                            .success(false)
                            .error("Failed to get shipments analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get drivers analytics only
     */
    @GetMapping("/analytics/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<DriversAnalytics>> getDriversAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            DriversAnalytics analytics = dashboardService.getDriversAnalytics(dateRange[0], dateRange[1]);
            
            log.debug("Drivers analytics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<DriversAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Drivers analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting drivers analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<DriversAnalytics>builder()
                            .success(false)
                            .error("Failed to get drivers analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get revenue analytics only
     */
    @GetMapping("/analytics/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<RevenueAnalytics>> getRevenueAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            RevenueAnalytics analytics = dashboardService.getRevenueAnalytics(dateRange[0], dateRange[1]);
            
            log.debug("Revenue analytics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<RevenueAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Revenue analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting revenue analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<RevenueAnalytics>builder()
                            .success(false)
                            .error("Failed to get revenue analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get delayed shipments analytics only
     */
    @GetMapping("/analytics/delayed-shipments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<DelayedShipmentsAnalytics>> getDelayedShipmentsAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            DelayedShipmentsAnalytics analytics = dashboardService.getDelayedShipmentsAnalytics(dateRange[0], dateRange[1]);
            
            log.debug("Delayed shipments analytics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<DelayedShipmentsAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Delayed shipments analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting delayed shipments analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<DelayedShipmentsAnalytics>builder()
                            .success(false)
                            .error("Failed to get delayed shipments analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get temperature violations analytics only
     */
    @GetMapping("/analytics/temperature-violations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<TemperatureViolationsAnalytics>> getTemperatureViolationsAnalytics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            TemperatureViolationsAnalytics analytics = dashboardService.getTemperatureViolationsAnalytics(dateRange[0], dateRange[1]);
            
            log.debug("Temperature violations analytics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<TemperatureViolationsAnalytics>builder()
                            .success(true)
                            .data(analytics)
                            .message("Temperature violations analytics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting temperature violations analytics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<TemperatureViolationsAnalytics>builder()
                            .success(false)
                            .error("Failed to get temperature violations analytics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get performance metrics only
     */
    @GetMapping("/analytics/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<PerformanceMetrics>> getPerformanceMetrics(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            PerformanceMetrics metrics = dashboardService.getPerformanceMetrics(dateRange[0], dateRange[1]);
            
            log.debug("Performance metrics retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<PerformanceMetrics>builder()
                            .success(true)
                            .data(metrics)
                            .message("Performance metrics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting performance metrics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<PerformanceMetrics>builder()
                            .success(false)
                            .error("Failed to get performance metrics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get dashboard summary (key metrics only)
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getDashboardSummary(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, null, null);
            DashboardAnalytics analytics = dashboardService.getDashboardAnalytics(filter, dateRange[0], dateRange[1]);
            
            Map<String, Object> summary = Map.of(
                    "totalShipments", analytics.getShipmentsAnalytics().getTotalShipments(),
                    "activeDrivers", analytics.getDriversAnalytics().getActiveDrivers(),
                    "totalRevenue", analytics.getRevenueAnalytics().getTotalRevenue(),
                    "delayedShipments", analytics.getDelayedShipmentsAnalytics().getTotalDelayedShipments(),
                    "temperatureViolations", analytics.getTemperatureViolationsAnalytics().getTotalViolations(),
                    "onTimeDeliveryRate", analytics.getDelayedShipmentsAnalytics().getOnTimeDeliveryRate(),
                    "collectionRate", analytics.getRevenueAnalytics().getCollectionRate(),
                    "systemUptime", analytics.getPerformanceMetrics().getSystemUptime(),
                    "filter", filter.name(),
                    "period", Map.of(
                            "startDate", dateRange[0],
                            "endDate", dateRange[1]
                    )
            );
            
            log.info("Dashboard summary retrieved for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(summary)
                            .message("Dashboard summary retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting dashboard summary", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to get dashboard summary: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get real-time metrics (for live dashboard updates)
     */
    @GetMapping("/real-time")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getRealTimeMetrics() {
        
        try {
            // Get current moment analytics (last hour)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourAgo = now.minusHours(1);
            
            DashboardAnalytics analytics = dashboardService.getDashboardAnalytics(DateFilter.DAILY, oneHourAgo, now);
            
            Map<String, Object> realTimeMetrics = Map.of(
                    "shipmentsLastHour", analytics.getShipmentsAnalytics().getTotalShipments(),
                    "activeDriversNow", analytics.getDriversAnalytics().getActiveDrivers(),
                    "revenueLastHour", analytics.getRevenueAnalytics().getTotalRevenue(),
                    "alertsLastHour", analytics.getTemperatureViolationsAnalytics().getTotalViolations(),
                    "systemResponseTime", analytics.getPerformanceMetrics().getAverageResponseTime(),
                    "errorRate", analytics.getPerformanceMetrics().getErrorRate(),
                    "activeUsers", analytics.getPerformanceMetrics().getActiveUsers(),
                    "timestamp", now
            );
            
            log.debug("Real-time metrics retrieved");
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(realTimeMetrics)
                            .message("Real-time metrics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting real-time metrics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to get real-time metrics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Export dashboard data as JSON
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> exportDashboardData(
            @RequestParam(defaultValue = "WEEKLY") DateFilter filter,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        try {
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            DashboardAnalytics analytics = dashboardService.getDashboardAnalytics(filter, dateRange[0], dateRange[1]);
            
            Map<String, Object> exportData = Map.of(
                    "exportInfo", Map.of(
                            "filter", filter.name(),
                            "startDate", dateRange[0],
                            "endDate", dateRange[1],
                            "exportedAt", LocalDateTime.now(),
                            "exportedBy", getCurrentUserId()
                    ),
                    "analytics", analytics
            );
            
            log.info("Dashboard data exported for filter: {}", filter);
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(exportData)
                            .message("Dashboard data exported successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error exporting dashboard data", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to export dashboard data: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get available filters and their descriptions
     */
    @GetMapping("/filters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getAvailableFilters() {
        
        try {
            Map<String, Object> filters = Map.of(
                    "dateFilters", Map.of(
                            "DAILY", "Last 24 hours",
                            "WEEKLY", "Last 7 days",
                            "MONTHLY", "Last 30 days",
                            "CUSTOM", "Custom date range"
                    ),
                    "defaultFilter", "WEEKLY",
                    "supportedFormats", List.of("json", "csv", "pdf"),
                    "refreshInterval", "30000", // 30 seconds
                    "cacheDuration", "300" // 5 minutes
            );
            
            log.debug("Available filters retrieved");
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(filters)
                            .message("Available filters retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting available filters", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to get available filters: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    // Helper methods
    private LocalDateTime[] calculateDateRange(DateFilter filter, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (filter) {
            case DAILY:
                return new LocalDateTime[]{now.minusDays(1), now};
            case WEEKLY:
                return new LocalDateTime[]{now.minusWeeks(1), now};
            case MONTHLY:
                return new LocalDateTime[]{now.minusMonths(1), now};
            case CUSTOM:
                return new LocalDateTime[]{startDate != null ? startDate : now.minusMonths(1), 
                                         endDate != null ? endDate : now};
            default:
                return new LocalDateTime[]{now.minusDays(7), now};
        }
    }

    private Long getCurrentUserId() {
        // Implementation to get current user ID from security context
        return 1L; // Placeholder
    }
}
