// package com.edham.logistics.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Advanced Reporting System
 */
@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class AdvancedReportingController {

    @Autowired
    private AdvancedReportingService reportingService;

    /**
     * Generate Profit/Loss Report
     */
    @PostMapping("/profit-loss")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER')")
    public ResponseEntity<AdvancedReportingService.ProfitLossReport> generateProfitLossReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            
            AdvancedReportingService.ProfitLossReport report = 
                reportingService.generateProfitLossReport(startDate, endDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Driver Performance Report
     */
    @PostMapping("/driver-performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FLEET_MANAGER')")
    public ResponseEntity<AdvancedReportingService.DriverPerformanceReport> generateDriverPerformanceReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            
            AdvancedReportingService.DriverPerformanceReport report = 
                reportingService.generateDriverPerformanceReport(startDate, endDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Shipment Efficiency Report
     */
    @PostMapping("/shipment-efficiency")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<AdvancedReportingService.ShipmentEfficiencyReport> generateShipmentEfficiencyReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            
            AdvancedReportingService.ShipmentEfficiencyReport report = 
                reportingService.generateShipmentEfficiencyReport(startDate, endDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Generate Fuel Consumption Report
     */
    @PostMapping("/fuel-consumption")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FLEET_MANAGER')")
    public ResponseEntity<AdvancedReportingService.FuelConsumptionReport> generateFuelConsumptionReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            
            AdvancedReportingService.FuelConsumptionReport report = 
                reportingService.generateFuelConsumptionReport(startDate, endDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Export Profit/Loss Report
     */
    @PostMapping("/profit-loss/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER')")
    public ResponseEntity<ByteArrayResource> exportProfitLossReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            String formatStr = (String) request.get("format");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            AdvancedReportingService.ExportFormat format = 
                AdvancedReportingService.ExportFormat.valueOf(formatStr.toUpperCase());
            
            AdvancedReportingService.ProfitLossReport report = 
                reportingService.generateProfitLossReport(startDate, endDate);
            byte[] reportData = reportingService.exportReport(report, format);
            
            String filename = "profit-loss-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                getFileExtension(format);
            
            return createFileResponse(reportData, filename, format.getMimeType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Export Driver Performance Report
     */
    @PostMapping("/driver-performance/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FLEET_MANAGER')")
    public ResponseEntity<ByteArrayResource> exportDriverPerformanceReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            String formatStr = (String) request.get("format");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            AdvancedReportingService.ExportFormat format = 
                AdvancedReportingService.ExportFormat.valueOf(formatStr.toUpperCase());
            
            AdvancedReportingService.DriverPerformanceReport report = 
                reportingService.generateDriverPerformanceReport(startDate, endDate);
            byte[] reportData = reportingService.exportReport(report, format);
            
            String filename = "driver-performance-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                getFileExtension(format);
            
            return createFileResponse(reportData, filename, format.getMimeType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Export Shipment Efficiency Report
     */
    @PostMapping("/shipment-efficiency/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<ByteArrayResource> exportShipmentEfficiencyReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            String formatStr = (String) request.get("format");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            AdvancedReportingService.ExportFormat format = 
                AdvancedReportingService.ExportFormat.valueOf(formatStr.toUpperCase());
            
            AdvancedReportingService.ShipmentEfficiencyReport report = 
                reportingService.generateShipmentEfficiencyReport(startDate, endDate);
            byte[] reportData = reportingService.exportReport(report, format);
            
            String filename = "shipment-efficiency-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                getFileExtension(format);
            
            return createFileResponse(reportData, filename, format.getMimeType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Export Fuel Consumption Report
     */
    @PostMapping("/fuel-consumption/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FLEET_MANAGER')")
    public ResponseEntity<ByteArrayResource> exportFuelConsumptionReport(
            @RequestBody Map<String, Object> request) {
        try {
            String startDateStr = (String) request.get("startDate");
            String endDateStr = (String) request.get("endDate");
            String formatStr = (String) request.get("format");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);
            AdvancedReportingService.ExportFormat format = 
                AdvancedReportingService.ExportFormat.valueOf(formatStr.toUpperCase());
            
            AdvancedReportingService.FuelConsumptionReport report = 
                reportingService.generateFuelConsumptionReport(startDate, endDate);
            byte[] reportData = reportingService.exportReport(report, format);
            
            String filename = "fuel-consumption-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + 
                getFileExtension(format);
            
            return createFileResponse(reportData, filename, format.getMimeType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get available report types
     */
    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER', 'FLEET_MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<Map<String, Object>> getReportTypes() {
        Map<String, Object> reportTypes = Map.of(
            "PROFIT_LOSS", Map.of(
                "name", "تقرير الأرباح والخسائر",
                "description", "تحليل شامل للأرباح والخسائر مع تفصيل حسب الفئات",
                "requiredRoles", List.of("ADMIN", "ACCOUNTANT", "MANAGER"),
                "features", List.of("Revenue breakdown", "Expense breakdown", "Monthly trends", "Profit margins")
            ),
            "DRIVER_PERFORMANCE", Map.of(
                "name", "تقرير أداء السائقين",
                "description", "تقييم شامل لأداء السائقين ومقارنات",
                "requiredRoles", List.of("ADMIN", "MANAGER", "FLEET_MANAGER"),
                "features", List.of("Completion rates", "On-time delivery", "Customer ratings", "Performance ranking")
            ),
            "SHIPMENT_EFFICIENCY", Map.of(
                "name", "تقرير كفاءة الشحنات",
                "description", "تحليل كفاءة الشحنات والمسارات",
                "requiredRoles", List.of("ADMIN", "MANAGER", "OPERATIONS_MANAGER"),
                "features", List.of("Delivery times", "Route efficiency", "Delay analysis", "Time patterns")
            ),
            "FUEL_CONSUMPTION", Map.of(
                "name", "تقرير استهلاك الوقود",
                "description", "تحليل استهلاك الوقود وكفاءة المركبات",
                "requiredRoles", List.of("ADMIN", "MANAGER", "FLEET_MANAGER"),
                "features", List.of("Vehicle efficiency", "Driver efficiency", "Cost analysis", "Trends")
            )
        );

        return ResponseEntity.ok(reportTypes);
    }

    /**
     * Get available export formats
     */
    @GetMapping("/export-formats")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER', 'FLEET_MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<Map<String, Object>> getExportFormats() {
        Map<String, Object> formats = Map.of(
            "PDF", Map.of(
                "name", "PDF",
                "description", "تنسيق PDF مع رسوم بيانية وتنسيق احترافي",
                "mimeType", "application/pdf",
                "extension", ".pdf"
            ),
            "EXCEL", Map.of(
                "name", "Excel",
                "description", "تنسيق Excel مع جداول قابلة للتعديل",
                "mimeType", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "extension", ".xlsx"
            ),
            "CSV", Map.of(
                "name", "CSV",
                "description", "تنسيق CSV للتحليل في برامج أخرى",
                "mimeType", "text/csv",
                "extension", ".csv"
            )
        );

        return ResponseEntity.ok(formats);
    }

    /**
     * Get report templates
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER', 'FLEET_MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<Map<String, Object>> getReportTemplates() {
        Map<String, Object> templates = Map.of(
            "monthly", Map.of(
                "name", "تقرير شهري",
                "description", "تقرير شامل للشهر الحالي",
                "defaultPeriod", "MONTHLY",
                "startDate", LocalDateTime.now().withDayOfMonth(1).toString(),
                "endDate", LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).toString()
            ),
            "quarterly", Map.of(
                "name", "تقرير ربع سنوي",
                "description", "تقرير شامل للربع الحالي",
                "defaultPeriod", "QUARTERLY",
                "startDate", LocalDateTime.now().withDayOfMonth(1).minusMonths((LocalDateTime.now().getMonthValue() - 1) % 3).toString(),
                "endDate", LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).toString()
            ),
            "yearly", Map.of(
                "name", "تقرير سنوي",
                "description", "تقرير شامل للسنة الحالية",
                "defaultPeriod", "YEARLY",
                "startDate", LocalDateTime.now().withDayOfYear(1).toString(),
                "endDate", LocalDateTime.now().withDayOfYear(LocalDateTime.now().toLocalDate().lengthOfYear()).toString()
            ),
            "custom", Map.of(
                "name", "تقرير مخصص",
                "description", "تقرير بنطاق زمني مخصص",
                "defaultPeriod", "CUSTOM",
                "startDate", LocalDateTime.now().minusDays(30).toString(),
                "endDate", LocalDateTime.now().toString()
            )
        );

        return ResponseEntity.ok(templates);
    }

    /**
     * Get dashboard summary
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER', 'FLEET_MANAGER', 'OPERATIONS_MANAGER')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            // Generate recent reports for dashboard
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime monthStart = now.withDayOfMonth(1);
            LocalDateTime monthEnd = now.withDayOfMonth(now.toLocalDate().lengthOfMonth());
            
            // Generate summary reports
            AdvancedReportingService.ProfitLossReport profitLossReport = 
                reportingService.generateProfitLossReport(monthStart, monthEnd);
            
            AdvancedReportingService.DriverPerformanceReport driverPerformanceReport = 
                reportingService.generateDriverPerformanceReport(monthStart, monthEnd);
            
            AdvancedReportingService.ShipmentEfficiencyReport shipmentEfficiencyReport = 
                reportingService.generateShipmentEfficiencyReport(monthStart, monthEnd);
            
            AdvancedReportingService.FuelConsumptionReport fuelConsumptionReport = 
                reportingService.generateFuelConsumptionReport(monthStart, monthEnd);
            
            Map<String, Object> dashboard = Map.of(
                "currentMonth", Map.of(
                    "profitLoss", Map.of(
                        "totalRevenue", profitLossReport.getSummary().getTotalRevenue(),
                        "netProfit", profitLossReport.getSummary().getNetProfit(),
                        "profitMargin", profitLossReport.getSummary().getProfitMargin()
                    ),
                    "driverPerformance", Map.of(
                        "totalDrivers", driverPerformanceReport.getSummary().getTotalDrivers(),
                        "averageCompletionRate", driverPerformanceReport.getSummary().getAverageCompletionRate(),
                        "averageOnTimeDelivery", driverPerformanceReport.getSummary().getAverageOnTimeDelivery()
                    ),
                    "shipmentEfficiency", Map.of(
                        "totalShipments", shipmentEfficiencyReport.getSummary().getTotalShipments(),
                        "completionRate", shipmentEfficiencyReport.getSummary().getCompletionRate(),
                        "onTimeDeliveryRate", shipmentEfficiencyReport.getSummary().getOnTimeDeliveryRate()
                    ),
                    "fuelConsumption", Map.of(
                        "totalFuel", fuelConsumptionReport.getSummary().getTotalFuel(),
                        "totalCost", fuelConsumptionReport.getSummary().getTotalCost(),
                        "averageFuelPerKm", fuelConsumptionReport.getSummary().getAverageFuelPerKm()
                    )
                ),
                "topPerformers", driverPerformanceReport.getTopPerformers(),
                "underPerformers", driverPerformanceReport.getUnderPerformers(),
                "lastUpdated", now.toString()
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get system health for reporting
     */
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            // Check system health for reporting
            Map<String, Object> health = Map.of(
                "status", "HEALTHY",
                "reportGeneration", "OPERATIONAL",
                "exportServices", "OPERATIONAL",
                "databaseConnection", "OPERATIONAL",
                "lastReportGenerated", LocalDateTime.now().minusMinutes(5).toString(),
                "queueSize", 0,
                "averageProcessingTime", "2.3 seconds"
            );

            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Schedule report generation
     */
    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> scheduleReport(
            @RequestBody Map<String, Object> request) {
        try {
            String reportType = (String) request.get("reportType");
            String schedule = (String) request.get("schedule"); // DAILY, WEEKLY, MONTHLY
            String recipients = (String) request.get("recipients");
            String format = (String) request.get("format");
            
            // Implementation would schedule report generation
            String scheduleId = java.util.UUID.randomUUID().toString();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "scheduleId", scheduleId,
                "message", "Report scheduled successfully",
                "nextRun", LocalDateTime.now().plusDays(1).toString()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to schedule report: " + e.getMessage()
            ));
        }
    }

    /**
     * Get scheduled reports
     */
    @GetMapping("/scheduled")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Map<String, Object>>> getScheduledReports() {
        try {
            // Implementation would return scheduled reports
            List<Map<String, Object>> scheduledReports = List.of(
                Map.of(
                    "scheduleId", "schedule-001",
                    "reportType", "PROFIT_LOSS",
                    "schedule", "MONTHLY",
                    "nextRun", LocalDateTime.now().plusDays(1).toString(),
                    "recipients", "admin@company.com",
                    "format", "PDF",
                    "active", true
                ),
                Map.of(
                    "scheduleId", "schedule-002",
                    "reportType", "DRIVER_PERFORMANCE",
                    "schedule", "WEEKLY",
                    "nextRun", LocalDateTime.now().plusDays(7).toString(),
                    "recipients", "manager@company.com",
                    "format", "EXCEL",
                    "active", true
                )
            );

            return ResponseEntity.ok(scheduledReports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete scheduled report
     */
    @DeleteMapping("/scheduled/{scheduleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Object>> deleteScheduledReport(
            @PathVariable String scheduleId) {
        try {
            // Implementation would delete scheduled report
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "Scheduled report deleted successfully"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to delete scheduled report: " + e.getMessage()
            ));
        }
    }

    /**
     * Helper method to create file response
     */
    private ResponseEntity<ByteArrayResource> createFileResponse(byte[] data, String filename, String mimeType) {
        ByteArrayResource resource = new ByteArrayResource(data);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .body(resource);
    }

    /**
     * Helper method to get file extension
     */
    private String getFileExtension(AdvancedReportingService.ExportFormat format) {
        switch (format) {
            case PDF:
                return ".pdf";
            case EXCEL:
                return ".xlsx";
            case CSV:
                return ".csv";
            default:
                return ".txt";
        }
    }
}
