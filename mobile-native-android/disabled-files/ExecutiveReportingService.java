package com.edham.logistics.service;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executive reporting service for high-level business intelligence
 * Provides comprehensive reports for management decision-making
 */
@Slf4j
@Service
public class ExecutiveReportingService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final NotificationService notificationService;

    @Autowired
    public ExecutiveReportingService(ShipmentRepository shipmentRepository,
                                   UserRepository userRepository,
                                   VehicleRepository vehicleRepository,
                                   MaintenanceRepository maintenanceRepository,
                                   NotificationService notificationService) {
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.notificationService = notificationService;
    }

    /**
     * Generate daily executive report
     */
    public ExecutiveReportDTO generateDailyReport(LocalDate date) {
        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            // Get daily metrics
            DailyMetricsDTO dailyMetrics = calculateDailyMetrics(startOfDay, endOfDay);
            
            // Get revenue breakdown
            RevenueBreakdownDTO revenueBreakdown = calculateRevenueBreakdown(startOfDay, endOfDay);
            
            // Get shipment efficiency
            ShipmentEfficiencyDTO shipmentEfficiency = calculateShipmentEfficiency(startOfDay, endOfDay);
            
            // Get driver performance
            DriverPerformanceDTO driverPerformance = calculateDriverPerformance(startOfDay, endOfDay);
            
            // Get system health
            SystemHealthDTO systemHealth = calculateSystemHealth(startOfDay, endOfDay);

            return ExecutiveReportDTO.builder()
                    .reportId("DAILY-" + date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .reportType("DAILY")
                    .reportDate(date)
                    .periodStart(startOfDay)
                    .periodEnd(endOfDay)
                    .dailyMetrics(dailyMetrics)
                    .revenueBreakdown(revenueBreakdown)
                    .shipmentEfficiency(shipmentEfficiency)
                    .driverPerformance(driverPerformance)
                    .systemHealth(systemHealth)
                    .generatedAt(LocalDateTime.now())
                    .generatedBy("Executive Reporting System")
                    .build();

        } catch (Exception e) {
            log.error("Error generating daily report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate daily report", e);
        }
    }

    /**
     * Generate weekly executive report
     */
    public ExecutiveReportDTO generateWeeklyReport(LocalDate weekStart) {
        try {
            LocalDate weekEnd = weekStart.plusDays(6);
            LocalDateTime startOfWeek = weekStart.atStartOfDay();
            LocalDateTime endOfWeek = weekEnd.atTime(23, 59, 59);

            // Get weekly metrics
            WeeklyMetricsDTO weeklyMetrics = calculateWeeklyMetrics(startOfWeek, endOfWeek);
            
            // Get revenue breakdown
            RevenueBreakdownDTO revenueBreakdown = calculateRevenueBreakdown(startOfWeek, endOfWeek);
            
            // Get shipment efficiency
            ShipmentEfficiencyDTO shipmentEfficiency = calculateShipmentEfficiency(startOfWeek, endOfWeek);
            
            // Get driver performance
            DriverPerformanceDTO driverPerformance = calculateDriverPerformance(startOfWeek, endOfWeek);
            
            // Get system health
            SystemHealthDTO systemHealth = calculateSystemHealth(startOfWeek, endOfWeek);

            return ExecutiveReportDTO.builder()
                    .reportId("WEEKLY-" + weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .reportType("WEEKLY")
                    .reportDate(weekStart)
                    .periodStart(startOfWeek)
                    .periodEnd(endOfWeek)
                    .weeklyMetrics(weeklyMetrics)
                    .revenueBreakdown(revenueBreakdown)
                    .shipmentEfficiency(shipmentEfficiency)
                    .driverPerformance(driverPerformance)
                    .systemHealth(systemHealth)
                    .generatedAt(LocalDateTime.now())
                    .generatedBy("Executive Reporting System")
                    .build();

        } catch (Exception e) {
            log.error("Error generating weekly report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate weekly report", e);
        }
    }

    /**
     * Generate monthly executive report
     */
    public ExecutiveReportDTO generateMonthlyReport(YearMonth yearMonth) {
        try {
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            LocalDateTime startOfMonth = monthStart.atStartOfDay();
            LocalDateTime endOfMonth = monthEnd.atTime(23, 59, 59);

            // Get monthly metrics
            MonthlyMetricsDTO monthlyMetrics = calculateMonthlyMetrics(startOfMonth, endOfMonth);
            
            // Get revenue breakdown
            RevenueBreakdownDTO revenueBreakdown = calculateRevenueBreakdown(startOfMonth, endOfMonth);
            
            // Get shipment efficiency
            ShipmentEfficiencyDTO shipmentEfficiency = calculateShipmentEfficiency(startOfMonth, endOfMonth);
            
            // Get driver performance
            DriverPerformanceDTO driverPerformance = calculateDriverPerformance(startOfMonth, endOfMonth);
            
            // Get system health
            SystemHealthDTO systemHealth = calculateSystemHealth(startOfMonth, endOfMonth);

            return ExecutiveReportDTO.builder()
                    .reportId("MONTHLY-" + yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .reportType("MONTHLY")
                    .reportDate(monthStart)
                    .periodStart(startOfMonth)
                    .periodEnd(endOfMonth)
                    .monthlyMetrics(monthlyMetrics)
                    .revenueBreakdown(revenueBreakdown)
                    .shipmentEfficiency(shipmentEfficiency)
                    .driverPerformance(driverPerformance)
                    .systemHealth(systemHealth)
                    .generatedAt(LocalDateTime.now())
                    .generatedBy("Executive Reporting System")
                    .build();

        } catch (Exception e) {
            log.error("Error generating monthly report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate monthly report", e);
        }
    }

    /**
     * Get driver performance ranking
     */
    public DriverRankingDTO getDriverPerformanceRanking(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Get all drivers
            List<User> drivers = userRepository.findByRole(UserRole.DRIVER);
            
            // Calculate performance metrics for each driver
            List<DriverPerformanceSummaryDTO> driverPerformances = new ArrayList<>();
            
            for (User driver : drivers) {
                DriverPerformanceSummaryDTO performance = calculateDriverPerformanceSummary(
                        driver.getId(), startDate, endDate);
                driverPerformances.add(performance);
            }
            
            // Sort by overall performance score
            driverPerformances.sort((a, b) -> Double.compare(b.getOverallScore(), a.getOverallScore()));
            
            // Assign rankings
            for (int i = 0; i < driverPerformances.size(); i++) {
                driverPerformances.get(i).setRank(i + 1);
            }
            
            // Calculate ranking statistics
            RankingStatisticsDTO statistics = calculateRankingStatistics(driverPerformances);

            return DriverRankingDTO.builder()
                    .periodStart(startDate)
                    .periodEnd(endDate)
                    .driverPerformances(driverPerformances)
                    .statistics(statistics)
                    .totalDrivers(drivers.size())
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error getting driver performance ranking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get driver performance ranking", e);
        }
    }

    /**
     * Export report to PDF
     */
    public byte[] exportToPDF(String reportId) {
        try {
            // In a real implementation, this would use a PDF library like iText or PDFBox
            log.info("Exporting report {} to PDF", reportId);
            
            // Generate PDF content
            String pdfContent = generatePDFContent(reportId);
            
            // Convert to bytes (simplified)
            return pdfContent.getBytes();
            
        } catch (Exception e) {
            log.error("Error exporting to PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export to PDF", e);
        }
    }

    /**
     * Export report to Excel
     */
    public byte[] exportToExcel(String reportId) {
        try {
            // In a real implementation, this would use Apache POI for Excel generation
            log.info("Exporting report {} to Excel", reportId);
            
            // Generate Excel content
            String excelContent = generateExcelContent(reportId);
            
            // Convert to bytes (simplified)
            return excelContent.getBytes();
            
        } catch (Exception e) {
            log.error("Error exporting to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }

    // Helper methods for calculating metrics
    private DailyMetricsDTO calculateDailyMetrics(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        // Get shipments for the day
        List<Shipment> dailyShipments = getShipmentsInPeriod(startOfDay, endOfDay);
        
        int totalShipments = dailyShipments.size();
        int deliveredShipments = (int) dailyShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.DELIVERED)
                .count();
        int pendingShipments = (int) dailyShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.PENDING)
                .count();
        int inTransitShipments = (int) dailyShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.IN_TRANSIT)
                .count();
        
        double totalRevenue = dailyShipments.stream()
                .mapToDouble(this::getShipmentRevenue)
                .sum();
        
        double averageDeliveryTime = calculateAverageDeliveryTime(dailyShipments);
        
        return DailyMetricsDTO.builder()
                .totalShipments(totalShipments)
                .deliveredShipments(deliveredShipments)
                .pendingShipments(pendingShipments)
                .inTransitShipments(inTransitShipments)
                .totalRevenue(totalRevenue)
                .averageDeliveryTime(averageDeliveryTime)
                .deliveryRate(totalShipments > 0 ? (double) deliveredShipments / totalShipments : 0.0)
                .build();
    }

    private WeeklyMetricsDTO calculateWeeklyMetrics(LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        List<Shipment> weeklyShipments = getShipmentsInPeriod(startOfWeek, endOfWeek);
        
        int totalShipments = weeklyShipments.size();
        int deliveredShipments = (int) weeklyShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.DELIVERED)
                .count();
        
        double totalRevenue = weeklyShipments.stream()
                .mapToDouble(this::getShipmentRevenue)
                .sum();
        
        // Calculate day-by-day breakdown
        Map<String, Integer> dailyBreakdown = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime dayStart = startOfWeek.plusDays(i);
            LocalDateTime dayEnd = dayStart.plusDays(1).minusSeconds(1);
            List<Shipment> dayShipments = getShipmentsInPeriod(dayStart, dayEnd);
            dailyBreakdown.put(dayStart.format(DateTimeFormatter.ofPattern("EEEE")), dayShipments.size());
        }
        
        return WeeklyMetricsDTO.builder()
                .totalShipments(totalShipments)
                .deliveredShipments(deliveredShipments)
                .totalRevenue(totalRevenue)
                .dailyBreakdown(dailyBreakdown)
                .averageShipmentsPerDay(totalShipments / 7.0)
                .build();
    }

    private MonthlyMetricsDTO calculateMonthlyMetrics(LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        List<Shipment> monthlyShipments = getShipmentsInPeriod(startOfMonth, endOfMonth);
        
        int totalShipments = monthlyShipments.size();
        int deliveredShipments = (int) monthlyShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.DELIVERED)
                .count();
        
        double totalRevenue = monthlyShipments.stream()
                .mapToDouble(this::getShipmentRevenue)
                .sum();
        
        // Calculate week-by-week breakdown
        Map<String, Integer> weeklyBreakdown = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            LocalDateTime weekStart = startOfMonth.plusWeeks(i);
            LocalDateTime weekEnd = weekStart.plusWeeks(1).minusSeconds(1);
            if (weekEnd.isAfter(endOfMonth)) weekEnd = endOfMonth;
            List<Shipment> weekShipments = getShipmentsInPeriod(weekStart, weekEnd);
            weeklyBreakdown.put("Week " + (i + 1), weekShipments.size());
        }
        
        return MonthlyMetricsDTO.builder()
                .totalShipments(totalShipments)
                .deliveredShipments(deliveredShipments)
                .totalRevenue(totalRevenue)
                .weeklyBreakdown(weeklyBreakdown)
                .averageShipmentsPerWeek(totalShipments / 4.0)
                .monthlyGrowth(calculateMonthlyGrowth(startOfMonth))
                .build();
    }

    private RevenueBreakdownDTO calculateRevenueBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        List<Shipment> shipments = getShipmentsInPeriod(startDate, endDate);
        
        double totalRevenue = shipments.stream()
                .mapToDouble(this::getShipmentRevenue)
                .sum();
        
        // Revenue by service type
        Map<String, Double> revenueByServiceType = new HashMap<>();
        revenueByServiceType.put("STANDARD", shipments.stream()
                .filter(s -> "STANDARD".equals(getServiceType(s)))
                .mapToDouble(this::getShipmentRevenue)
                .sum());
        revenueByServiceType.put("EXPRESS", shipments.stream()
                .filter(s -> "EXPRESS".equals(getServiceType(s)))
                .mapToDouble(this::getShipmentRevenue)
                .sum());
        revenueByServiceType.put("PREMIUM", shipments.stream()
                .filter(s -> "PREMIUM".equals(getServiceType(s)))
                .mapToDouble(this::getShipmentRevenue)
                .sum());
        
        // Revenue by region
        Map<String, Double> revenueByRegion = new HashMap<>();
        revenueByRegion.put("RIYADH", totalRevenue * 0.4);
        revenueByRegion.put("JEDDAH", totalRevenue * 0.3);
        revenueByRegion.put("DAMMAM", totalRevenue * 0.2);
        revenueByRegion.put("OTHERS", totalRevenue * 0.1);
        
        return RevenueBreakdownDTO.builder()
                .totalRevenue(totalRevenue)
                .revenueByServiceType(revenueByServiceType)
                .revenueByRegion(revenueByRegion)
                .averageRevenuePerShipment(shipments.size() > 0 ? totalRevenue / shipments.size() : 0.0)
                .build();
    }

    private ShipmentEfficiencyDTO calculateShipmentEfficiency(LocalDateTime startDate, LocalDateTime endDate) {
        List<Shipment> shipments = getShipmentsInPeriod(startDate, endDate);
        
        double onTimeDeliveryRate = calculateOnTimeDeliveryRate(shipments);
        double averageDeliveryTime = calculateAverageDeliveryTime(shipments);
        double fuelEfficiency = calculateFuelEfficiency(shipments);
        double vehicleUtilization = calculateVehicleUtilization(startDate, endDate);
        
        return ShipmentEfficiencyDTO.builder()
                .onTimeDeliveryRate(onTimeDeliveryRate)
                .averageDeliveryTime(averageDeliveryTime)
                .fuelEfficiency(fuelEfficiency)
                .vehicleUtilization(vehicleUtilization)
                .efficiencyScore((onTimeDeliveryRate + (1 - averageDeliveryTime / 24) + fuelEfficiency + vehicleUtilization) / 4)
                .build();
    }

    private DriverPerformanceDTO calculateDriverPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        List<User> drivers = userRepository.findByRole(UserRole.DRIVER);
        
        int totalDrivers = drivers.size();
        int activeDrivers = (int) drivers.stream()
                .filter(driver -> hasActiveShipments(driver.getId(), startDate, endDate))
                .count();
        
        double averageRating = drivers.stream()
                .filter(driver -> driver.getFullName() != null)
                .mapToDouble(driver -> 4.0 + Math.random()) // Simulated rating
                .average()
                .orElse(0.0);
        
        double averageDeliveriesPerDriver = activeDrivers > 0 ? 
                (double) getCompletedShipments(startDate, endDate) / activeDrivers : 0.0;
        
        return DriverPerformanceDTO.builder()
                .totalDrivers(totalDrivers)
                .activeDrivers(activeDrivers)
                .driverUtilization(totalDrivers > 0 ? (double) activeDrivers / totalDrivers : 0.0)
                .averageRating(averageRating)
                .averageDeliveriesPerDriver(averageDeliveriesPerDriver)
                .build();
    }

    private SystemHealthDTO calculateSystemHealth(LocalDateTime startDate, LocalDateTime endDate) {
        int activeVehicles = (int) vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == com.edham.logistics.dto.VehicleStatus.ACTIVE)
                .count();
        
        int totalVehicles = (int) vehicleRepository.count();
        
        List<Maintenance> overdueMaintenance = maintenanceRepository.findOverdueMaintenance(LocalDateTime.now());
        
        double systemUptime = 0.99; // Simulated uptime
        double responseTime = 150.0; // milliseconds
        double errorRate = 0.01; // 1% error rate
        
        return SystemHealthDTO.builder()
                .systemUptime(systemUptime)
                .averageResponseTime(responseTime)
                .errorRate(errorRate)
                .activeVehicles(activeVehicles)
                .totalVehicles(totalVehicles)
                .vehicleAvailability(totalVehicles > 0 ? (double) activeVehicles / totalVehicles : 0.0)
                .overdueMaintenance(overdueMaintenance.size())
                .healthScore((systemUptime + (1 - errorRate) + vehicleRepository.count() > 0 ? 
                        (double) activeVehicles / totalVehicles : 0.0) / 3)
                .build();
    }

    private DriverPerformanceSummaryDTO calculateDriverPerformanceSummary(Long driverId, 
                                                                         LocalDateTime startDate, 
                                                                         LocalDateTime endDate) {
        List<Shipment> driverShipments = getShipmentsByDriver(driverId, startDate, endDate);
        
        int totalShipments = driverShipments.size();
        int deliveredShipments = (int) driverShipments.stream()
                .filter(s -> s.getStatus() == com.edham.logistics.dto.ShipmentStatus.DELIVERED)
                .count();
        
        double totalRevenue = driverShipments.stream()
                .mapToDouble(this::getShipmentRevenue)
                .sum();
        
        double averageRating = 4.0 + Math.random(); // Simulated rating
        double onTimeRate = calculateOnTimeDeliveryRate(driverShipments);
        
        double overallScore = (averageRating / 5.0 * 0.3) + 
                             (onTimeRate * 0.4) + 
                             (totalShipments > 0 ? (double) deliveredShipments / totalShipments * 0.3 : 0.0);
        
        return DriverPerformanceSummaryDTO.builder()
                .driverId(driverId)
                .driverName(getDriverName(driverId))
                .totalShipments(totalShipments)
                .deliveredShipments(deliveredShipments)
                .totalRevenue(totalRevenue)
                .averageRating(averageRating)
                .onTimeDeliveryRate(onTimeRate)
                .overallScore(overallScore)
                .build();
    }

    // Additional helper methods
    private List<Shipment> getShipmentsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // In a real implementation, this would query the database
        return Collections.emptyList();
    }

    private List<Shipment> getShipmentsByDriver(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        // In a real implementation, this would query the database
        return Collections.emptyList();
    }

    private double getShipmentRevenue(Shipment shipment) {
        // Simulated revenue calculation
        return 100.0 + Math.random() * 500.0;
    }

    private String getServiceType(Shipment shipment) {
        // Simulated service type
        return Math.random() > 0.7 ? "STANDARD" : Math.random() > 0.4 ? "EXPRESS" : "PREMIUM";
    }

    private double calculateAverageDeliveryTime(List<Shipment> shipments) {
        // Simulated average delivery time in hours
        return 2.5 + Math.random() * 2.0;
    }

    private double calculateOnTimeDeliveryRate(List<Shipment> shipments) {
        // Simulated on-time delivery rate
        return 0.85 + Math.random() * 0.1;
    }

    private double calculateFuelEfficiency(List<Shipment> shipments) {
        // Simulated fuel efficiency
        return 0.8 + Math.random() * 0.15;
    }

    private double calculateVehicleUtilization(LocalDateTime startDate, LocalDateTime endDate) {
        // Simulated vehicle utilization
        return 0.7 + Math.random() * 0.2;
    }

    private boolean hasActiveShipments(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        // In a real implementation, this would check if driver has active shipments
        return true;
    }

    private int getCompletedShipments(LocalDateTime startDate, LocalDateTime endDate) {
        // Simulated completed shipments count
        return 50 + (int)(Math.random() * 50);
    }

    private double calculateMonthlyGrowth(LocalDateTime startOfMonth) {
        // Simulated monthly growth rate
        return 0.05 + Math.random() * 0.1;
    }

    private String getDriverName(Long driverId) {
        // In a real implementation, this would get the driver's name
        return "Driver " + driverId;
    }

    private RankingStatisticsDTO calculateRankingStatistics(List<DriverPerformanceSummaryDTO> driverPerformances) {
        if (driverPerformances.isEmpty()) {
            return RankingStatisticsDTO.builder().build();
        }
        
        double averageScore = driverPerformances.stream()
                .mapToDouble(DriverPerformanceSummaryDTO::getOverallScore)
                .average()
                .orElse(0.0);
        
        double medianScore = calculateMedian(driverPerformances.stream()
                .map(DriverPerformanceSummaryDTO::getOverallScore)
                .collect(Collectors.toList()));
        
        int topPerformers = (int) driverPerformances.stream()
                .filter(dp -> dp.getOverallScore() >= 0.8)
                .count();
        
        return RankingStatisticsDTO.builder()
                .averageScore(averageScore)
                .medianScore(medianScore)
                .topPerformers(topPerformers)
                .bottomPerformers((int) driverPerformances.stream()
                        .filter(dp -> dp.getOverallScore() < 0.6)
                        .count())
                .build();
    }

    private double calculateMedian(List<Double> scores) {
        List<Double> sorted = scores.stream().sorted().collect(Collectors.toList());
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }

    private String generatePDFContent(String reportId) {
        // Simulated PDF content generation
        return "PDF Content for Report: " + reportId + "\n" +
               "Generated at: " + LocalDateTime.now() + "\n" +
               "Executive Report Content...";
    }

    private String generateExcelContent(String reportId) {
        // Simulated Excel content generation
        return "Excel Content for Report: " + reportId + "\n" +
               "Generated at: " + LocalDateTime.now() + "\n" +
               "Executive Report Data in Excel Format...";
    }
}
