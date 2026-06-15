package com.edham.logistics.analytics;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin dashboard analytics service
 * Provides comprehensive analytics for admin dashboard
 */
@Slf4j
@Service
public class AdminDashboardService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ColdChainMonitoringService coldChainService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AdminDashboardService(ShipmentRepository shipmentRepository,
                                UserRepository userRepository,
                                InvoiceRepository invoiceRepository,
                                PaymentRepository paymentRepository,
                                ColdChainMonitoringService coldChainService,
                                MongoTemplate mongoTemplate) {
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.coldChainService = coldChainService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Get comprehensive dashboard analytics
     */
    public DashboardAnalytics getDashboardAnalytics(DateFilter filter, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting dashboard analytics with filter: {}, period: {} to {}", filter, startDate, endDate);
        
        try {
            // Calculate date range based on filter
            LocalDateTime[] dateRange = calculateDateRange(filter, startDate, endDate);
            LocalDateTime actualStartDate = dateRange[0];
            LocalDateTime actualEndDate = dateRange[1];
            
            // Get all analytics data
            DashboardAnalytics analytics = DashboardAnalytics.builder()
                    .filter(filter)
                    .startDate(actualStartDate)
                    .endDate(actualEndDate)
                    .build();
            
            // Shipments analytics
            analytics.setShipmentsAnalytics(getShipmentsAnalytics(actualStartDate, actualEndDate));
            
            // Drivers analytics
            analytics.setDriversAnalytics(getDriversAnalytics(actualStartDate, actualEndDate));
            
            // Revenue analytics
            analytics.setRevenueAnalytics(getRevenueAnalytics(actualStartDate, actualEndDate));
            
            // Delayed shipments analytics
            analytics.setDelayedShipmentsAnalytics(getDelayedShipmentsAnalytics(actualStartDate, actualEndDate));
            
            // Temperature violations analytics
            analytics.setTemperatureViolationsAnalytics(getTemperatureViolationsAnalytics(actualStartDate, actualEndDate));
            
            // Performance metrics
            analytics.setPerformanceMetrics(getPerformanceMetrics(actualStartDate, actualEndDate));
            
            log.info("Dashboard analytics retrieved successfully for period: {} to {}", actualStartDate, actualEndDate);
            return analytics;
            
        } catch (Exception e) {
            log.error("Error getting dashboard analytics", e);
            throw new RuntimeException("Failed to get dashboard analytics", e);
        }
    }

    /**
     * Get shipments analytics
     */
    private ShipmentsAnalytics getShipmentsAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Total shipments
            long totalShipments = shipmentRepository.countByCreatedAtBetween(startDate, endDate);
            
            // Shipments by status
            Map<ShipmentStatus, Long> shipmentsByStatus = new HashMap<>();
            for (ShipmentStatus status : ShipmentStatus.values()) {
                long count = shipmentRepository.countByStatusAndCreatedAtBetween(status, startDate, endDate);
                shipmentsByStatus.put(status, count);
            }
            
            // Shipments by day/week/month
            Map<String, Long> shipmentsByTime = getShipmentsByTime(startDate, endDate);
            
            // Top routes
            List<RouteAnalytics> topRoutes = getTopRoutes(startDate, endDate, 10);
            
            // Delivery performance
            DeliveryPerformance deliveryPerformance = getDeliveryPerformance(startDate, endDate);
            
            return ShipmentsAnalytics.builder()
                    .totalShipments(totalShipments)
                    .shipmentsByStatus(shipmentsByStatus)
                    .shipmentsByTime(shipmentsByTime)
                    .topRoutes(topRoutes)
                    .deliveryPerformance(deliveryPerformance)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting shipments analytics", e);
            return ShipmentsAnalytics.builder().build();
        }
    }

    /**
     * Get drivers analytics
     */
    private DriversAnalytics getDriversAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Total drivers
            long totalDrivers = userRepository.countByRole(UserRole.DRIVER);
            
            // Active drivers (drivers with shipments in the period)
            Set<Long> activeDriverIds = shipmentRepository.findByCreatedAtBetween(startDate, endDate)
                    .stream()
                    .map(Shipment::getDriverId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            
            long activeDrivers = activeDriverIds.size();
            
            // Driver performance
            List<DriverPerformance> driverPerformance = getDriverPerformance(startDate, endDate, 20);
            
            // Drivers by status
            Map<String, Long> driversByStatus = new HashMap<>();
            driversByStatus.put("active", activeDrivers);
            driversByStatus.put("inactive", totalDrivers - activeDrivers);
            
            // Average deliveries per driver
            double averageDeliveriesPerDriver = totalDrivers > 0 ? 
                    (double) shipmentRepository.countByStatusAndCreatedAtBetween(ShipmentStatus.DELIVERED, startDate, endDate) / totalDrivers : 0;
            
            return DriversAnalytics.builder()
                    .totalDrivers(totalDrivers)
                    .activeDrivers(activeDrivers)
                    .driversByStatus(driversByStatus)
                    .driverPerformance(driverPerformance)
                    .averageDeliveriesPerDriver(averageDeliveriesPerDriver)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting drivers analytics", e);
            return DriversAnalytics.builder().build();
        }
    }

    /**
     * Get revenue analytics
     */
    private RevenueAnalytics getRevenueAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Total revenue from invoices
            List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
            BigDecimal totalRevenue = invoices.stream()
                    .map(Invoice::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Paid revenue
            BigDecimal paidRevenue = invoices.stream()
                    .map(Invoice::getPaidAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Outstanding revenue
            BigDecimal outstandingRevenue = invoices.stream()
                    .map(Invoice::getOutstandingBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Revenue by time
            Map<String, BigDecimal> revenueByTime = getRevenueByTime(startDate, endDate);
            
            // Revenue by service type
            Map<String, BigDecimal> revenueByService = getRevenueByService(startDate, endDate);
            
            // Collection rate
            double collectionRate = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                    paidRevenue.divide(totalRevenue, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
            
            // Revenue growth
            BigDecimal revenueGrowth = calculateRevenueGrowth(startDate, endDate);
            
            return RevenueAnalytics.builder()
                    .totalRevenue(totalRevenue)
                    .paidRevenue(paidRevenue)
                    .outstandingRevenue(outstandingRevenue)
                    .revenueByTime(revenueByTime)
                    .revenueByService(revenueByService)
                    .collectionRate(collectionRate)
                    .revenueGrowth(revenueGrowth)
                    .currency("SAR")
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting revenue analytics", e);
            return RevenueAnalytics.builder().build();
        }
    }

    /**
     * Get delayed shipments analytics
     */
    private DelayedShipmentsAnalytics getDelayedShipmentsAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Total delayed shipments
            List<Shipment> delayedShipments = shipmentRepository.findDelayedShipments(startDate, endDate);
            
            long totalDelayedShipments = delayedShipments.size();
            
            // Delayed shipments by reason
            Map<String, Long> delayedByReason = delayedShipments.stream()
                    .filter(shipment -> shipment.getDelayReason() != null)
                    .collect(Collectors.groupingBy(
                            Shipment::getDelayReason,
                            Collectors.counting()
                    ));
            
            // Average delay time
            double averageDelayHours = delayedShipments.stream()
                    .filter(shipment -> shipment.getActualDeliveryTime() != null && shipment.getExpectedDeliveryTime() != null)
                    .mapToLong(shipment -> java.time.Duration.between(
                            shipment.getExpectedDeliveryTime(),
                            shipment.getActualDeliveryTime()
                    ).toHours())
                    .average()
                    .orElse(0.0);
            
            // Delayed shipments by route
            Map<String, Long> delayedByRoute = delayedShipments.stream()
                    .collect(Collectors.groupingBy(
                            shipment -> shipment.getOrigin() + " → " + shipment.getDestination(),
                            Collectors.counting()
                    ));
            
            // Delay trend
            Map<String, Long> delayTrend = getDelayTrend(startDate, endDate);
            
            // On-time delivery rate
            long totalDeliveredShipments = shipmentRepository.countByStatusAndCreatedAtBetween(
                    ShipmentStatus.DELIVERED, startDate, endDate);
            double onTimeDeliveryRate = totalDeliveredShipments > 0 ? 
                    (double) (totalDeliveredShipments - totalDelayedShipments) / totalDeliveredShipments * 100 : 100;
            
            return DelayedShipmentsAnalytics.builder()
                    .totalDelayedShipments(totalDelayedShipments)
                    .delayedByReason(delayedByReason)
                    .averageDelayHours(averageDelayHours)
                    .delayedByRoute(delayedByRoute)
                    .delayTrend(delayTrend)
                    .onTimeDeliveryRate(onTimeDeliveryRate)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting delayed shipments analytics", e);
            return DelayedShipmentsAnalytics.builder().build();
        }
    }

    /**
     * Get temperature violations analytics
     */
    private TemperatureViolationsAnalytics getTemperatureViolationsAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Get temperature alerts from cold chain service
            List<ColdChainAlert> temperatureAlerts = coldChainService.getAlertsByDateRange(startDate, endDate);
            
            long totalViolations = temperatureAlerts.size();
            
            // Violations by severity
            Map<String, Long> violationsBySeverity = temperatureAlerts.stream()
                    .collect(Collectors.groupingBy(
                            alert -> alert.getSeverity().name(),
                            Collectors.counting()
                    ));
            
            // Violations by type
            Map<String, Long> violationsByType = temperatureAlerts.stream()
                    .collect(Collectors.groupingBy(
                            alert -> alert.getAlertType().name(),
                            Collectors.counting()
                    ));
            
            // Violations by product type
            Map<String, Long> violationsByProductType = temperatureAlerts.stream()
                    .filter(alert -> alert.getProductType() != null)
                    .collect(Collectors.groupingBy(
                            ColdChainAlert::getProductType,
                            Collectors.counting()
                    ));
            
            // Violations by time
            Map<String, Long> violationsByTime = getViolationsByTime(temperatureAlerts, startDate, endDate);
            
            // Top problematic shipments
            List<ShipmentViolation> topProblematicShipments = getTopProblematicShipments(temperatureAlerts, 10);
            
            // Resolution rate
            long resolvedViolations = temperatureAlerts.stream()
                    .filter(alert -> alert.getResolvedAt() != null)
                    .count();
            
            double resolutionRate = totalViolations > 0 ? (double) resolvedViolations / totalViolations * 100 : 100;
            
            return TemperatureViolationsAnalytics.builder()
                    .totalViolations(totalViolations)
                    .violationsBySeverity(violationsBySeverity)
                    .violationsByType(violationsByType)
                    .violationsByProductType(violationsByProductType)
                    .violationsByTime(violationsByTime)
                    .topProblematicShipments(topProblematicShipments)
                    .resolutionRate(resolutionRate)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting temperature violations analytics", e);
            return TemperatureViolationsAnalytics.builder().build();
        }
    }

    /**
     * Get performance metrics
     */
    private PerformanceMetrics getPerformanceMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // System uptime (placeholder - would be calculated from monitoring data)
            double systemUptime = 99.9;
            
            // Average response time (placeholder - would be calculated from API logs)
            double averageResponseTime = 150.0; // milliseconds
            
            // Error rate (placeholder - would be calculated from error logs)
            double errorRate = 0.5; // percentage
            
            // Database performance
            double databaseQueryTime = 25.0; // milliseconds average
            
            // Cache hit rate
            double cacheHitRate = 85.0; // percentage
            
            // Active users
            long activeUsers = userRepository.countByLastLoginAfter(startDate);
            
            // API calls
            long totalApiCalls = 1000000; // placeholder - would be calculated from API logs
            
            return PerformanceMetrics.builder()
                    .systemUptime(systemUptime)
                    .averageResponseTime(averageResponseTime)
                    .errorRate(errorRate)
                    .databaseQueryTime(databaseQueryTime)
                    .cacheHitRate(cacheHitRate)
                    .activeUsers(activeUsers)
                    .totalApiCalls(totalApiCalls)
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting performance metrics", e);
            return PerformanceMetrics.builder().build();
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

    private Map<String, Long> getShipmentsByTime(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> shipmentsByTime = new LinkedHashMap<>();
        List<Shipment> shipments = shipmentRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Group by day/week/month based on date range
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        
        if (daysBetween <= 31) {
            // Daily grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long count = shipments.stream()
                        .filter(s -> s.getCreatedAt().toLocalDate().equals(date.toLocalDate()))
                        .count();
                shipmentsByTime.put(day, count);
            }
        } else if (daysBetween <= 90) {
            // Weekly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusWeeks(1)) {
                String week = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                LocalDateTime weekEnd = date.plusWeeks(1).minusDays(1);
                long count = shipments.stream()
                        .filter(s -> !s.getCreatedAt().isBefore(date) && !s.getCreatedAt().isAfter(weekEnd))
                        .count();
                shipmentsByTime.put(week, count);
            }
        } else {
            // Monthly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
                String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDateTime monthEnd = date.plusMonths(1).minusDays(1);
                long count = shipments.stream()
                        .filter(s -> !s.getCreatedAt().isBefore(date) && !s.getCreatedAt().isAfter(monthEnd))
                        .count();
                shipmentsByTime.put(month, count);
            }
        }
        
        return shipmentsByTime;
    }

    private List<RouteAnalytics> getTopRoutes(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Shipment> shipments = shipmentRepository.findByCreatedAtBetween(startDate, endDate);
        
        Map<String, Long> routeCounts = shipments.stream()
                .collect(Collectors.groupingBy(
                        shipment -> shipment.getOrigin() + " → " + shipment.getDestination(),
                        Collectors.counting()
                ));
        
        return routeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> RouteAnalytics.builder()
                        .route(entry.getKey())
                        .shipmentCount(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private DeliveryPerformance getDeliveryPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        List<Shipment> deliveredShipments = shipmentRepository.findByStatusAndCreatedAtBetween(
                ShipmentStatus.DELIVERED, startDate, endDate);
        
        long totalDelivered = deliveredShipments.size();
        
        // On-time deliveries
        long onTimeDeliveries = deliveredShipments.stream()
                .filter(s -> s.getActualDeliveryTime() != null && 
                           !s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))
                .count();
        
        // Average delivery time
        double averageDeliveryHours = deliveredShipments.stream()
                .filter(s -> s.getActualDeliveryTime() != null && s.getCreatedAt() != null)
                .mapToLong(s -> java.time.Duration.between(s.getCreatedAt(), s.getActualDeliveryTime()).toHours())
                .average()
                .orElse(0.0);
        
        double onTimeDeliveryRate = totalDelivered > 0 ? (double) onTimeDeliveries / totalDelivered * 100 : 100;
        
        return DeliveryPerformance.builder()
                .totalDelivered(totalDelivered)
                .onTimeDeliveries(onTimeDeliveries)
                .averageDeliveryHours(averageDeliveryHours)
                .onTimeDeliveryRate(onTimeDeliveryRate)
                .build();
    }

    private List<DriverPerformance> getDriverPerformance(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Shipment> shipments = shipmentRepository.findByCreatedAtBetween(startDate, endDate);
        
        Map<Long, List<Shipment>> driverShipments = shipments.stream()
                .filter(s -> s.getDriverId() != null)
                .collect(Collectors.groupingBy(Shipment::getDriverId));
        
        return driverShipments.entrySet().stream()
                .map(entry -> {
                    Long driverId = entry.getKey();
                    List<Shipment> driverShipmentList = entry.getValue();
                    
                    long totalShipments = driverShipmentList.size();
                    long deliveredShipments = driverShipmentList.stream()
                            .mapToLong(s -> s.getStatus() == ShipmentStatus.DELIVERED ? 1L : 0L)
                            .sum();
                    
                    long onTimeDeliveries = driverShipmentList.stream()
                            .filter(s -> s.getStatus() == ShipmentStatus.DELIVERED &&
                                       s.getActualDeliveryTime() != null &&
                                       !s.getActualDeliveryTime().isAfter(s.getExpectedDeliveryTime()))
                            .count();
                    
                    double onTimeRate = deliveredShipments > 0 ? (double) onTimeDeliveries / deliveredShipments * 100 : 0;
                    
                    String driverName = userRepository.findById(driverId)
                            .map(User::getName)
                            .orElse("Unknown Driver");
                    
                    return DriverPerformance.builder()
                            .driverId(driverId)
                            .driverName(driverName)
                            .totalShipments(totalShipments)
                            .deliveredShipments(deliveredShipments)
                            .onTimeDeliveries(onTimeDeliveries)
                            .onTimeDeliveryRate(onTimeRate)
                            .build();
                })
                .sorted((d1, d2) -> Double.compare(d2.getOnTimeDeliveryRate(), d1.getOnTimeDeliveryRate()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> getRevenueByTime(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> revenueByTime = new LinkedHashMap<>();
        List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
        
        // Similar grouping logic as shipmentsByTime
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        
        if (daysBetween <= 31) {
            // Daily grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                BigDecimal dayRevenue = invoices.stream()
                        .filter(i -> i.getInvoiceDate().toLocalDate().equals(date.toLocalDate()))
                        .map(Invoice::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                revenueByTime.put(day, dayRevenue);
            }
        } else if (daysBetween <= 90) {
            // Weekly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusWeeks(1)) {
                String week = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                LocalDateTime weekEnd = date.plusWeeks(1).minusDays(1);
                BigDecimal weekRevenue = invoices.stream()
                        .filter(i -> !i.getInvoiceDate().isBefore(date) && !i.getInvoiceDate().isAfter(weekEnd))
                        .map(Invoice::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                revenueByTime.put(week, weekRevenue);
            }
        } else {
            // Monthly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
                String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDateTime monthEnd = date.plusMonths(1).minusDays(1);
                BigDecimal monthRevenue = invoices.stream()
                        .filter(i -> !i.getInvoiceDate().isBefore(date) && !i.getInvoiceDate().isAfter(monthEnd))
                        .map(Invoice::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                revenueByTime.put(month, monthRevenue);
            }
        }
        
        return revenueByTime;
    }

    private Map<String, BigDecimal> getRevenueByService(LocalDateTime startDate, LocalDateTime endDate) {
        List<Shipment> shipments = shipmentRepository.findByCreatedAtBetween(startDate, endDate);
        Map<String, BigDecimal> revenueByService = new HashMap<>();
        
        for (Shipment shipment : shipments) {
            BigDecimal shippingCost = shipment.getShippingCost() != null ? shipment.getShippingCost() : BigDecimal.ZERO;
            String serviceType = shipment.getProductType() != null ? shipment.getProductType() : "STANDARD";
            
            revenueByService.merge(serviceType, shippingCost, BigDecimal::add);
        }
        
        return revenueByService;
    }

    private BigDecimal calculateRevenueGrowth(LocalDateTime startDate, LocalDateTime endDate) {
        // Calculate revenue for current period
        List<Invoice> currentPeriodInvoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
        BigDecimal currentRevenue = currentPeriodInvoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate revenue for previous period of same duration
        long periodDuration = java.time.Duration.between(startDate, endDate).toDays();
        LocalDateTime previousStartDate = startDate.minusDays(periodDuration);
        LocalDateTime previousEndDate = startDate;
        
        List<Invoice> previousPeriodInvoices = invoiceRepository.findByInvoiceDateBetween(previousStartDate, previousEndDate);
        BigDecimal previousRevenue = previousPeriodInvoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate growth percentage
        if (previousRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return currentRevenue.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        
        return currentRevenue.subtract(previousRevenue)
                .divide(previousRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private Map<String, Long> getDelayTrend(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> delayTrend = new LinkedHashMap<>();
        List<Shipment> delayedShipments = shipmentRepository.findDelayedShipments(startDate, endDate);
        
        // Similar grouping logic as shipmentsByTime
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        
        if (daysBetween <= 31) {
            // Daily grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long count = delayedShipments.stream()
                        .filter(s -> s.getCreatedAt().toLocalDate().equals(date.toLocalDate()))
                        .count();
                delayTrend.put(day, count);
            }
        } else if (daysBetween <= 90) {
            // Weekly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusWeeks(1)) {
                String week = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                LocalDateTime weekEnd = date.plusWeeks(1).minusDays(1);
                long count = delayedShipments.stream()
                        .filter(s -> !s.getCreatedAt().isBefore(date) && !s.getCreatedAt().isAfter(weekEnd))
                        .count();
                delayTrend.put(week, count);
            }
        } else {
            // Monthly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
                String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDateTime monthEnd = date.plusMonths(1).minusDays(1);
                long count = delayedShipments.stream()
                        .filter(s -> !s.getCreatedAt().isBefore(date) && !s.getCreatedAt().isAfter(monthEnd))
                        .count();
                delayTrend.put(month, count);
            }
        }
        
        return delayTrend;
    }

    private Map<String, Long> getViolationsByTime(List<ColdChainAlert> alerts, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> violationsByTime = new LinkedHashMap<>();
        
        // Similar grouping logic as shipmentsByTime
        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        
        if (daysBetween <= 31) {
            // Daily grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long count = alerts.stream()
                        .filter(a -> a.getCreatedAt().toLocalDate().equals(date.toLocalDate()))
                        .count();
                violationsByTime.put(day, count);
            }
        } else if (daysBetween <= 90) {
            // Weekly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusWeeks(1)) {
                String week = date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
                LocalDateTime weekEnd = date.plusWeeks(1).minusDays(1);
                long count = alerts.stream()
                        .filter(a -> !a.getCreatedAt().isBefore(date) && !a.getCreatedAt().isAfter(weekEnd))
                        .count();
                violationsByTime.put(week, count);
            }
        } else {
            // Monthly grouping
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusMonths(1)) {
                String month = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                LocalDateTime monthEnd = date.plusMonths(1).minusDays(1);
                long count = alerts.stream()
                        .filter(a -> !a.getCreatedAt().isBefore(date) && !a.getCreatedAt().isAfter(monthEnd))
                        .count();
                violationsByTime.put(month, count);
            }
        }
        
        return violationsByTime;
    }

    private List<ShipmentViolation> getTopProblematicShipments(List<ColdChainAlert> alerts, int limit) {
        Map<Long, List<ColdChainAlert>> shipmentAlerts = alerts.stream()
                .filter(alert -> alert.getShipmentId() != null)
                .collect(Collectors.groupingBy(ColdChainAlert::getShipmentId));
        
        return shipmentAlerts.entrySet().stream()
                .map(entry -> {
                    Long shipmentId = entry.getKey();
                    List<ColdChainAlert> alertList = entry.getValue();
                    
                    String trackingNumber = shipmentRepository.findById(shipmentId)
                            .map(Shipment::getTrackingNumber)
                            .orElse("Unknown");
                    
                    return ShipmentViolation.builder()
                            .shipmentId(shipmentId)
                            .trackingNumber(trackingNumber)
                            .violationCount(alertList.size())
                            .criticalViolations(alertList.stream()
                                    .mapToLong(a -> a.getSeverity() == ColdChainMonitoringService.AlertSeverity.CRITICAL ? 1L : 0L)
                                    .sum())
                            .lastViolation(alertList.stream()
                                    .max(Comparator.comparing(ColdChainAlert::getCreatedAt))
                                    .map(ColdChainAlert::getCreatedAt)
                                    .orElse(null))
                            .build();
                })
                .sorted((s1, s2) -> Long.compare(s2.getViolationCount(), s1.getViolationCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Enums and data classes
    public enum DateFilter {
        DAILY, WEEKLY, MONTHLY, CUSTOM
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DashboardAnalytics {
        private DateFilter filter;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private ShipmentsAnalytics shipmentsAnalytics;
        private DriversAnalytics driversAnalytics;
        private RevenueAnalytics revenueAnalytics;
        private DelayedShipmentsAnalytics delayedShipmentsAnalytics;
        private TemperatureViolationsAnalytics temperatureViolationsAnalytics;
        private PerformanceMetrics performanceMetrics;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ShipmentsAnalytics {
        private Long totalShipments;
        private Map<ShipmentStatus, Long> shipmentsByStatus;
        private Map<String, Long> shipmentsByTime;
        private List<RouteAnalytics> topRoutes;
        private DeliveryPerformance deliveryPerformance;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DriversAnalytics {
        private Long totalDrivers;
        private Long activeDrivers;
        private Map<String, Long> driversByStatus;
        private List<DriverPerformance> driverPerformance;
        private Double averageDeliveriesPerDriver;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RevenueAnalytics {
        private BigDecimal totalRevenue;
        private BigDecimal paidRevenue;
        private BigDecimal outstandingRevenue;
        private Map<String, BigDecimal> revenueByTime;
        private Map<String, BigDecimal> revenueByService;
        private Double collectionRate;
        private BigDecimal revenueGrowth;
        private String currency;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DelayedShipmentsAnalytics {
        private Long totalDelayedShipments;
        private Map<String, Long> delayedByReason;
        private Double averageDelayHours;
        private Map<String, Long> delayedByRoute;
        private Map<String, Long> delayTrend;
        private Double onTimeDeliveryRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TemperatureViolationsAnalytics {
        private Long totalViolations;
        private Map<String, Long> violationsBySeverity;
        private Map<String, Long> violationsByType;
        private Map<String, Long> violationsByProductType;
        private Map<String, Long> violationsByTime;
        private List<ShipmentViolation> topProblematicShipments;
        private Double resolutionRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PerformanceMetrics {
        private Double systemUptime;
        private Double averageResponseTime;
        private Double errorRate;
        private Double databaseQueryTime;
        private Double cacheHitRate;
        private Long activeUsers;
        private Long totalApiCalls;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RouteAnalytics {
        private String route;
        private Long shipmentCount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DeliveryPerformance {
        private Long totalDelivered;
        private Long onTimeDeliveries;
        private Double averageDeliveryHours;
        private Double onTimeDeliveryRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DriverPerformance {
        private Long driverId;
        private String driverName;
        private Long totalShipments;
        private Long deliveredShipments;
        private Long onTimeDeliveries;
        private Double onTimeDeliveryRate;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ShipmentViolation {
        private Long shipmentId;
        private String trackingNumber;
        private Long violationCount;
        private Long criticalViolations;
        private LocalDateTime lastViolation;
    }
}
