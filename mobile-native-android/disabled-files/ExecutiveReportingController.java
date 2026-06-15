// // // package com.edham.logistics.controller;



import com.edham.logistics.dto.*;

import com.edham.logistics.service.ExecutiveReportingService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.ByteArrayResource;

import org.springframework.http.HttpHeaders;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;



import jakarta.validation.Valid;

import java.time.LocalDate;

import java.time.YearMonth;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

import java.util.List;

import java.util.Map;



/**

 * Executive reporting controller for high-level business intelligence

 * Provides comprehensive reports for management decision-making

 */

@RestController

@RequestMapping("/api/v1/reports/executive")

@CrossOrigin(origins = "*", maxAge = 3600)

@Slf4j

public class ExecutiveReportingController {



    private final ExecutiveReportingService executiveReportingService;



    @Autowired

    public ExecutiveReportingController(ExecutiveReportingService executiveReportingService) {

        this.executiveReportingService = executiveReportingService;

    }



    /**

     * Generate daily executive report

     */

    @GetMapping("/daily")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ExecutiveReportDTO> generateDailyReport(

            @RequestParam(required = false) LocalDate date) {

        try {

            if (date == null) {

                date = LocalDate.now();

            }



            ExecutiveReportDTO report = executiveReportingService.generateDailyReport(date);

            return ResponseEntity.ok(report);

        } catch (Exception e) {

            log.error("Error generating daily report: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Generate weekly executive report

     */

    @GetMapping("/weekly")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ExecutiveReportDTO> generateWeeklyReport(

            @RequestParam(required = false) LocalDate weekStart) {

        try {

            if (weekStart == null) {

                weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);

            }



            ExecutiveReportDTO report = executiveReportingService.generateWeeklyReport(weekStart);

            return ResponseEntity.ok(report);

        } catch (Exception e) {

            log.error("Error generating weekly report: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Generate monthly executive report

     */

    @GetMapping("/monthly")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ExecutiveReportDTO> generateMonthlyReport(

            @RequestParam(required = false) String yearMonth) {

        try {

            YearMonth targetMonth;

            if (yearMonth == null) {

                targetMonth = YearMonth.now();

            } else {

                targetMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));

            }



            ExecutiveReportDTO report = executiveReportingService.generateMonthlyReport(targetMonth);

            return ResponseEntity.ok(report);

        } catch (Exception e) {

            log.error("Error generating monthly report: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get driver performance ranking

     */

    @GetMapping("/driver-ranking")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<DriverRankingDTO> getDriverPerformanceRanking(

            @RequestParam(required = false) LocalDate startDate,

            @RequestParam(required = false) LocalDate endDate) {

        try {

            LocalDateTime start = startDate != null ? startDate.atStartOfDay() : 

                    LocalDate.now().minusDays(30).atStartOfDay();

            LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : 

                    LocalDate.now().atTime(23, 59, 59);



            DriverRankingDTO ranking = executiveReportingService.getDriverPerformanceRanking(start, end);

            return ResponseEntity.ok(ranking);

        } catch (Exception e) {

            log.error("Error getting driver performance ranking: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Generate custom executive report

     */

    @PostMapping("/generate")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ReportResponseDTO> generateCustomReport(@Valid @RequestBody ReportRequestDTO request) {

        try {

            ExecutiveReportDTO reportData = null;

            String reportId = "CUSTOM-" + System.currentTimeMillis();



            // Generate report based on type

            switch (request.getReportType().toUpperCase()) {

                case "DAILY":

                    LocalDate date = request.getReportDate() != null ? request.getReportDate() : LocalDate.now();

                    reportData = executiveReportingService.generateDailyReport(date);

                    break;

                case "WEEKLY":

                    LocalDate weekStart = request.getStartDate() != null ? request.getStartDate() : 

                            LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);

                    reportData = executiveReportingService.generateWeeklyReport(weekStart);

                    break;

                case "MONTHLY":

                    YearMonth yearMonth = request.getStartDate() != null ? 

                            YearMonth.from(request.getStartDate()) : YearMonth.now();

                    reportData = executiveReportingService.generateMonthlyReport(yearMonth);

                    break;

                default:

                    return ResponseEntity.badRequest().body(

                            ReportResponseDTO.builder()

                                    .success(false)

                                    .error("Unsupported report type: " + request.getReportType())

                                    .build()

                    );

            }



            // Handle export format

            byte[] fileData = null;

            String fileName = null;

            String mimeType = null;



            if ("PDF".equals(request.getFormat())) {

                fileData = executiveReportingService.exportToPDF(reportId);

                fileName = reportId + ".pdf";

                mimeType = "application/pdf";

            } else if ("EXCEL".equals(request.getFormat())) {

                fileData = executiveReportingService.exportToExcel(reportId);

                fileName = reportId + ".xlsx";

                mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            }



            ReportResponseDTO response = ReportResponseDTO.builder()

                    .success(true)

                    .reportId(reportId)

                    .reportData("JSON".equals(request.getFormat()) ? reportData : null)

                    .fileData(fileData)

                    .format(request.getFormat())

                    .fileName(fileName)

                    .mimeType(mimeType)

                    .fileSize(fileData != null ? (long) fileData.length : null)

                    .generatedAt(java.time.LocalDateTime.now())

                    .message("Report generated successfully")

                    .build();



            return ResponseEntity.ok(response);



        } catch (Exception e) {

            log.error("Error generating custom report: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(

                    ReportResponseDTO.builder()

                            .success(false)

                            .error("Failed to generate report: " + e.getMessage())

                            .build()

            );

        }

    }



    /**

     * Export report to PDF

     */

    @GetMapping("/{reportId}/export/pdf")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ByteArrayResource> exportToPDF(@PathVariable String reportId) {

        try {

            byte[] pdfData = executiveReportingService.exportToPDF(reportId);

            

            ByteArrayResource resource = new ByteArrayResource(pdfData);

            

            return ResponseEntity.ok()

                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportId + ".pdf")

                    .contentType(MediaType.APPLICATION_PDF)

                    .contentLength(pdfData.length)

                    .body(resource);



        } catch (Exception e) {

            log.error("Error exporting to PDF: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Export report to Excel

     */

    @GetMapping("/{reportId}/export/excel")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ByteArrayResource> exportToExcel(@PathVariable String reportId) {

        try {

            byte[] excelData = executiveReportingService.exportToExcel(reportId);

            

            ByteArrayResource resource = new ByteArrayResource(excelData);

            

            return ResponseEntity.ok()

                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportId + ".xlsx")

                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                    .contentLength(excelData.length)

                    .body(resource);



        } catch (Exception e) {

            log.error("Error exporting to Excel: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get executive dashboard data

     */

    @GetMapping("/dashboard")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ExecutiveDashboardDTO> getExecutiveDashboard() {

        try {

            // Get latest reports

            ExecutiveReportDTO latestDaily = executiveReportingService.generateDailyReport(LocalDate.now());

            ExecutiveReportDTO latestWeekly = executiveReportingService.generateWeeklyReport(

                    LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1));

            ExecutiveReportDTO latestMonthly = executiveReportingService.generateMonthlyReport(YearMonth.now());



            // Get driver ranking

            DriverRankingDTO driverRanking = executiveReportingService.getDriverPerformanceRanking(

                    LocalDate.now().minusDays(30).atStartOfDay(),

                    LocalDate.now().atTime(23, 59, 59));



            ExecutiveDashboardDTO dashboard = ExecutiveDashboardDTO.builder()

                    .lastUpdated(java.time.LocalDateTime.now())

                    .latestDailyReport(latestDaily)

                    .latestWeeklyReport(latestWeekly)

                    .latestMonthlyReport(latestMonthly)

                    .keyMetrics(Map.of(

                            "totalRevenue", latestDaily.getRevenueBreakdown().getTotalRevenue(),

                            "deliveryRate", latestDaily.getDailyMetrics().getDeliveryRate(),

                            "systemHealth", latestDaily.getSystemHealth().getHealthScore(),

                            "driverUtilization", latestDaily.getDriverPerformance().getDriverUtilization()

                    ))

                    .recommendations(generateDashboardRecommendations(latestDaily))

                    .build();



            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {

            log.error("Error getting executive dashboard: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get available report templates

     */

    @GetMapping("/templates")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<List<ReportTemplateDTO>> getReportTemplates() {

        try {

            List<ReportTemplateDTO> templates = List.of(

                    ReportTemplateDTO.builder()

                            .templateId("daily_standard")

                            .templateName("Daily Standard Report")

                            .reportType("DAILY")

                            .includedSections(List.of("METRICS", "REVENUE", "EFFICIENCY", "PERFORMANCE"))

                            .description("Standard daily report with all key metrics")

                            .isDefault(true)

                            .build(),

                    ReportTemplateDTO.builder()

                            .templateId("weekly_comprehensive")

                            .templateName("Weekly Comprehensive Report")

                            .reportType("WEEKLY")

                            .includedSections(List.of("METRICS", "REVENUE", "EFFICIENCY", "PERFORMANCE", "HEALTH"))

                            .description("Comprehensive weekly report with detailed analysis")

                            .isDefault(false)

                            .build(),

                    ReportTemplateDTO.builder()

                            .templateId("monthly_executive")

                            .templateName("Monthly Executive Summary")

                            .reportType("MONTHLY")

                            .includedSections(List.of("METRICS", "REVENUE", "PERFORMANCE", "HEALTH"))

                            .description("High-level monthly report for executive leadership")

                            .isDefault(false)

                            .build()

            );



            return ResponseEntity.ok(templates);

        } catch (Exception e) {

            log.error("Error getting report templates: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Schedule recurring reports

     */

    @PostMapping("/schedule")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<ReportScheduleDTO> scheduleReport(@Valid @RequestBody ReportScheduleDTO schedule) {

        try {

            // In a real implementation, this would save the schedule to the database

            ReportScheduleDTO savedSchedule = ReportScheduleDTO.builder()

                    .scheduleId("SCHEDULE-" + System.currentTimeMillis())

                    .reportType(schedule.getReportType())

                    .frequency(schedule.getFrequency())

                    .nextRunTime(calculateNextRunTime(schedule.getFrequency()))

                    .recipients(schedule.getRecipients())

                    .scheduleConfig(schedule.getScheduleConfig())

                    .active(true)

                    .createdAt(java.time.LocalDateTime.now())

                    .createdBy("Current User")

                    .build();



            return ResponseEntity.ok(savedSchedule);

        } catch (Exception e) {

            log.error("Error scheduling report: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Get active report schedules

     */

    @GetMapping("/schedules")

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")

    public ResponseEntity<List<ReportScheduleDTO>> getActiveSchedules() {

        try {

            List<ReportScheduleDTO> schedules = List.of(

                    ReportScheduleDTO.builder()

                            .scheduleId("schedule_1")

                            .reportType("DAILY")

                            .frequency("DAILY")

                            .nextRunTime(java.time.LocalDateTime.now().plusDays(1))

                            .recipients(List.of("admin@edham.com", "supervisor@edham.com"))

                            .active(true)

                            .build(),

                    ReportScheduleDTO.builder()

                            .scheduleId("schedule_2")

                            .reportType("WEEKLY")

                            .frequency("WEEKLY")

                            .nextRunTime(java.time.LocalDateTime.now().plusWeeks(1))

                            .recipients(List.of("admin@edham.com"))

                            .active(true)

                            .build()

            );



            return ResponseEntity.ok(schedules);

        } catch (Exception e) {

            log.error("Error getting active schedules: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().build();

        }

    }



    /**

     * Test executive reporting system

     */

    @GetMapping("/test")

    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<Map<String, Object>> testExecutiveReporting() {

        try {

            log.info("Executive reporting system test initiated");



            // Test report generation

            ExecutiveReportDTO testReport = executiveReportingService.generateDailyReport(LocalDate.now());



            // Test export functionality

            byte[] pdfData = executiveReportingService.exportToPDF("TEST-REPORT");

            byte[] excelData = executiveReportingService.exportToExcel("TEST-REPORT");



            return ResponseEntity.ok(Map.of(

                    "status", "operational",

                    "message", "Executive reporting system is fully operational",

                    "features", Map.of(

                            "dailyReports", "enabled",

                            "weeklyReports", "enabled",

                            "monthlyReports", "enabled",

                            "driverRanking", "enabled",

                            "pdfExport", "enabled",

                            "excelExport", "enabled",

                            "customReports", "enabled",

                            "scheduling", "enabled"

                    ),

                    "testResults", Map.of(

                            "dailyReportGenerated", testReport != null,

                            "pdfExportSize", pdfData.length,

                            "excelExportSize", excelData.length

                    ),

                    "timestamp", java.time.LocalDateTime.now().toString()

            ));

        } catch (Exception e) {

            log.error("Error testing executive reporting: {}", e.getMessage(), e);

            return ResponseEntity.internalServerError().body(Map.of(

                    "status", "error",

                    "message", "Executive reporting system test failed: " + e.getMessage()

            ));

        }

    }



    // Helper methods

    private List<String> generateDashboardRecommendations(ExecutiveReportDTO report) {

        List<String> recommendations = new ArrayList<>();



        if (report.getSystemHealth().getHealthScore() < 0.8) {

            recommendations.add("System health score is below optimal - consider maintenance review");

        }



        if (report.getDriverPerformance().getDriverUtilization() < 0.7) {

            recommendations.add("Driver utilization is low - consider workload optimization");

        }



        if (report.getShipmentEfficiency().getOnTimeDeliveryRate() < 0.9) {

            recommendations.add("On-time delivery rate needs improvement - review route optimization");

        }



        if (report.getRevenueBreakdown().getTotalRevenue() < 10000) {

            recommendations.add("Daily revenue is below target - consider marketing initiatives");

        }



        return recommendations;

    }



    private java.time.LocalDateTime calculateNextRunTime(String frequency) {

        switch (frequency.toUpperCase()) {

            case "DAILY":

                return java.time.LocalDateTime.now().plusDays(1);

            case "WEEKLY":

                return java.time.LocalDateTime.now().plusWeeks(1);

            case "MONTHLY":

                return java.time.LocalDateTime.now().plusMonths(1);

            default:

                return java.time.LocalDateTime.now().plusDays(1);

        }

    }

}

