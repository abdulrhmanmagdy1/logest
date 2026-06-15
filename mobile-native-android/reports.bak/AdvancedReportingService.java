// // package com.edham.logistics.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced Reporting Service for Logistics Management
 * Provides comprehensive analytics and reporting capabilities
 */
@Service
@Transactional
public class AdvancedReportingService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private FuelRepository fuelRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Report Type Enum
     */
    public enum ReportType {
        PROFIT_LOSS("تقرير الأرباح والخسائر"),
        DRIVER_PERFORMANCE("تقرير أداء السائقين"),
        SHIPMENT_EFFICIENCY("تقرير كفاءة الشحنات"),
        FUEL_CONSUMPTION("تقرير استهلاك الوقود");

        private final String arabicName;

        ReportType(String arabicName) {
            this.arabicName = arabicName;
        }

        public String getArabicName() { return arabicName; }
    }

    /**
     * Export Format Enum
     */
    public enum ExportFormat {
        PDF("PDF", "application/pdf"),
        EXCEL("Excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        CSV("CSV", "text/csv");

        private final String name;
        private final String mimeType;

        ExportFormat(String name, String mimeType) {
            this.name = name;
            this.mimeType = mimeType;
        }

        public String getName() { return name; }
        public String getMimeType() { return mimeType; }
    }

    /**
     * Profit/Loss Report Data
     */
    public static class ProfitLossReport {
        private String reportId;
        private LocalDateTime generatedAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private ReportPeriod period;
        private FinancialSummary summary;
        private List<RevenueBreakdown> revenueBreakdown;
        private List<ExpenseBreakdown> expenseBreakdown;
        private List<MonthlyTrend> monthlyTrends;
        private List<ProfitMarginAnalysis> profitMargins;

        // Constructors, getters, and setters
        public ProfitLossReport() {
            this.reportId = UUID.randomUUID().toString();
            this.generatedAt = LocalDateTime.now();
            this.revenueBreakdown = new ArrayList<>();
            this.expenseBreakdown = new ArrayList<>();
            this.monthlyTrends = new ArrayList<>();
            this.profitMargins = new ArrayList<>();
        }

        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public ReportPeriod getPeriod() { return period; }
        public void setPeriod(ReportPeriod period) { this.period = period; }
        
        public FinancialSummary getSummary() { return summary; }
        public void setSummary(FinancialSummary summary) { this.summary = summary; }
        
        public List<RevenueBreakdown> getRevenueBreakdown() { return revenueBreakdown; }
        public void setRevenueBreakdown(List<RevenueBreakdown> revenueBreakdown) { this.revenueBreakdown = revenueBreakdown; }
        
        public List<ExpenseBreakdown> getExpenseBreakdown() { return expenseBreakdown; }
        public void setExpenseBreakdown(List<ExpenseBreakdown> expenseBreakdown) { this.expenseBreakdown = expenseBreakdown; }
        
        public List<MonthlyTrend> getMonthlyTrends() { return monthlyTrends; }
        public void setMonthlyTrends(List<MonthlyTrend> monthlyTrends) { this.monthlyTrends = monthlyTrends; }
        
        public List<ProfitMarginAnalysis> getProfitMargins() { return profitMargins; }
        public void setProfitMargins(List<ProfitMarginAnalysis> profitMargins) { this.profitMargins = profitMargins; }
    }

    /**
     * Driver Performance Report Data
     */
    public static class DriverPerformanceReport {
        private String reportId;
        private LocalDateTime generatedAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<DriverPerformance> driverPerformances;
        private PerformanceSummary summary;
        private List<PerformanceMetrics> topPerformers;
        private List<PerformanceMetrics> underPerformers;

        public DriverPerformanceReport() {
            this.reportId = UUID.randomUUID().toString();
            this.generatedAt = LocalDateTime.now();
            this.driverPerformances = new ArrayList<>();
            this.topPerformers = new ArrayList<>();
            this.underPerformers = new ArrayList<>();
        }

        // Getters and setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public List<DriverPerformance> getDriverPerformances() { return driverPerformances; }
        public void setDriverPerformances(List<DriverPerformance> driverPerformances) { this.driverPerformances = driverPerformances; }
        
        public PerformanceSummary getSummary() { return summary; }
        public void setSummary(PerformanceSummary summary) { this.summary = summary; }
        
        public List<PerformanceMetrics> getTopPerformers() { return topPerformers; }
        public void setTopPerformers(List<PerformanceMetrics> topPerformers) { this.topPerformers = topPerformers; }
        
        public List<PerformanceMetrics> getUnderPerformers() { return underPerformers; }
        public void setUnderPerformers(List<PerformanceMetrics> underPerformers) { this.underPerformers = underPerformers; }
    }

    /**
     * Shipment Efficiency Report Data
     */
    public static class ShipmentEfficiencyReport {
        private String reportId;
        private LocalDateTime generatedAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EfficiencySummary summary;
        private List<ShipmentEfficiency> shipmentEfficiencies;
        private List<RouteEfficiency> routeEfficiencies;
        private List<TimeAnalysis> timeAnalysis;
        private List<DelayAnalysis> delayAnalysis;

        public ShipmentEfficiencyReport() {
            this.reportId = UUID.randomUUID().toString();
            this.generatedAt = LocalDateTime.now();
            this.shipmentEfficiencies = new ArrayList<>();
            this.routeEfficiencies = new ArrayList<>();
            this.timeAnalysis = new ArrayList<>();
            this.delayAnalysis = new ArrayList<>();
        }

        // Getters and setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public EfficiencySummary getSummary() { return summary; }
        public void setSummary(EfficiencySummary summary) { this.summary = summary; }
        
        public List<ShipmentEfficiency> getShipmentEfficiencies() { return shipmentEfficiencies; }
        public void setShipmentEfficiencies(List<ShipmentEfficiency> shipmentEfficiencies) { this.shipmentEfficiencies = shipmentEfficiencies; }
        
        public List<RouteEfficiency> getRouteEfficiencies() { return routeEfficiencies; }
        public void setRouteEfficiencies(List<RouteEfficiency> routeEfficiencies) { this.routeEfficiencies = routeEfficiencies; }
        
        public List<TimeAnalysis> getTimeAnalysis() { return timeAnalysis; }
        public void setTimeAnalysis(List<TimeAnalysis> timeAnalysis) { this.timeAnalysis = timeAnalysis; }
        
        public List<DelayAnalysis> getDelayAnalysis() { return delayAnalysis; }
        public void setDelayAnalysis(List<DelayAnalysis> delayAnalysis) { this.delayAnalysis = delayAnalysis; }
    }

    /**
     * Fuel Consumption Report Data
     */
    public static class FuelConsumptionReport {
        private String reportId;
        private LocalDateTime generatedAt;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private FuelSummary summary;
        private List<VehicleFuelConsumption> vehicleConsumptions;
        private List<DriverFuelEfficiency> driverEfficiencies;
        private List<RouteFuelAnalysis> routeAnalyses;
        private List<FuelTrend> fuelTrends;
        private List<FuelCostAnalysis> costAnalyses;

        public FuelConsumptionReport() {
            this.reportId = UUID.randomUUID().toString();
            this.generatedAt = LocalDateTime.now();
            this.vehicleConsumptions = new ArrayList<>();
            this.driverEfficiencies = new ArrayList<>();
            this.routeAnalyses = new ArrayList<>();
            this.fuelTrends = new ArrayList<>();
            this.costAnalyses = new ArrayList<>();
        }

        // Getters and setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public FuelSummary getSummary() { return summary; }
        public void setSummary(FuelSummary summary) { this.summary = summary; }
        
        public List<VehicleFuelConsumption> getVehicleConsumptions() { return vehicleConsumptions; }
        public void setVehicleConsumptions(List<VehicleFuelConsumption> vehicleConsumptions) { this.vehicleConsumptions = vehicleConsumptions; }
        
        public List<DriverFuelEfficiency> getDriverEfficiencies() { return driverEfficiencies; }
        public void setDriverEfficiencies(List<DriverFuelEfficiency> driverEfficiencies) { this.driverEfficiencies = driverEfficiencies; }
        
        public List<RouteFuelAnalysis> getRouteAnalyses() { return routeAnalyses; }
        public void setRouteAnalyses(List<RouteFuelAnalysis> routeAnalyses) { this.routeAnalyses = routeAnalyses; }
        
        public List<FuelTrend> getFuelTrends() { return fuelTrends; }
        public void setFuelTrends(List<FuelTrend> fuelTrends) { this.fuelTrends = fuelTrends; }
        
        public List<FuelCostAnalysis> getCostAnalyses() { return costAnalyses; }
        public void setCostAnalyses(List<FuelCostAnalysis> costAnalyses) { this.costAnalyses = costAnalyses; }
    }

    // Supporting data classes
    public static class ReportPeriod {
        private String periodType; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
        private String periodLabel;

        // Getters and setters
        public String getPeriodType() { return periodType; }
        public void setPeriodType(String periodType) { this.periodType = periodType; }
        public String getPeriodLabel() { return periodLabel; }
        public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }
    }

    public static class FinancialSummary {
        private Double totalRevenue;
        private Double totalExpenses;
        private Double grossProfit;
        private Double netProfit;
        private Double profitMargin;
        private Double revenueGrowth;
        private Double expenseGrowth;

        // Getters and setters
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Double getTotalExpenses() { return totalExpenses; }
        public void setTotalExpenses(Double totalExpenses) { this.totalExpenses = totalExpenses; }
        public Double getGrossProfit() { return grossProfit; }
        public void setGrossProfit(Double grossProfit) { this.grossProfit = grossProfit; }
        public Double getNetProfit() { return netProfit; }
        public void setNetProfit(Double netProfit) { this.netProfit = netProfit; }
        public Double getProfitMargin() { return profitMargin; }
        public void setProfitMargin(Double profitMargin) { this.profitMargin = profitMargin; }
        public Double getRevenueGrowth() { return revenueGrowth; }
        public void setRevenueGrowth(Double revenueGrowth) { this.revenueGrowth = revenueGrowth; }
        public Double getExpenseGrowth() { return expenseGrowth; }
        public void setExpenseGrowth(Double expenseGrowth) { this.expenseGrowth = expenseGrowth; }
    }

    public static class RevenueBreakdown {
        private String category;
        private Double amount;
        private Double percentage;
        private Double growth;

        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        public Double getGrowth() { return growth; }
        public void setGrowth(Double growth) { this.growth = growth; }
    }

    public static class ExpenseBreakdown {
        private String category;
        private Double amount;
        private Double percentage;
        private Double growth;

        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        public Double getGrowth() { return growth; }
        public void setGrowth(Double growth) { this.growth = growth; }
    }

    public static class MonthlyTrend {
        private String month;
        private Double revenue;
        private Double expenses;
        private Double profit;
        private Double profitMargin;

        // Getters and setters
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public Double getRevenue() { return revenue; }
        public void setRevenue(Double revenue) { this.revenue = revenue; }
        public Double getExpenses() { return expenses; }
        public void setExpenses(Double expenses) { this.expenses = expenses; }
        public Double getProfit() { return profit; }
        public void setProfit(Double profit) { this.profit = profit; }
        public Double getProfitMargin() { return profitMargin; }
        public void setProfitMargin(Double profitMargin) { this.profitMargin = profitMargin; }
    }

    public static class ProfitMarginAnalysis {
        private String category;
        private Double margin;
        private Double trend;
        private String status; // HIGH, MEDIUM, LOW

        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getMargin() { return margin; }
        public void setMargin(Double margin) { this.margin = margin; }
        public Double getTrend() { return trend; }
        public void setTrend(Double trend) { this.trend = trend; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class DriverPerformance {
        private String driverId;
        private String driverName;
        private Integer totalShipments;
        private Integer completedShipments;
        private Double completionRate;
        private Double averageDeliveryTime;
        private Double onTimeDeliveryRate;
        private Double customerRating;
        private Double totalDistance;
        private Double fuelEfficiency;
        private Double revenueGenerated;
        private Integer violations;

        // Getters and setters
        public String getDriverId() { return driverId; }
        public void setDriverId(String driverId) { this.driverId = driverId; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public Integer getTotalShipments() { return totalShipments; }
        public void setTotalShipments(Integer totalShipments) { this.totalShipments = totalShipments; }
        public Integer getCompletedShipments() { return completedShipments; }
        public void setCompletedShipments(Integer completedShipments) { this.completedShipments = completedShipments; }
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
        public Double getAverageDeliveryTime() { return averageDeliveryTime; }
        public void setAverageDeliveryTime(Double averageDeliveryTime) { this.averageDeliveryTime = averageDeliveryTime; }
        public Double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
        public void setOnTimeDeliveryRate(Double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
        public Double getCustomerRating() { return customerRating; }
        public void setCustomerRating(Double customerRating) { this.customerRating = customerRating; }
        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
        public Double getFuelEfficiency() { return fuelEfficiency; }
        public void setFuelEfficiency(Double fuelEfficiency) { this.fuelEfficiency = fuelEfficiency; }
        public Double getRevenueGenerated() { return revenueGenerated; }
        public void setRevenueGenerated(Double revenueGenerated) { this.revenueGenerated = revenueGenerated; }
        public Integer getViolations() { return violations; }
        public void setViolations(Integer violations) { this.violations = violations; }
    }

    public static class PerformanceSummary {
        private Integer totalDrivers;
        private Double averageCompletionRate;
        private Double averageOnTimeDelivery;
        private Double averageCustomerRating;
        private Double totalRevenue;
        private Integer totalViolations;

        // Getters and setters
        public Integer getTotalDrivers() { return totalDrivers; }
        public void setTotalDrivers(Integer totalDrivers) { this.totalDrivers = totalDrivers; }
        public Double getAverageCompletionRate() { return averageCompletionRate; }
        public void setAverageCompletionRate(Double averageCompletionRate) { this.averageCompletionRate = averageCompletionRate; }
        public Double getAverageOnTimeDelivery() { return averageOnTimeDelivery; }
        public void setAverageOnTimeDelivery(Double averageOnTimeDelivery) { this.averageOnTimeDelivery = averageOnTimeDelivery; }
        public Double getAverageCustomerRating() { return averageCustomerRating; }
        public void setAverageCustomerRating(Double averageCustomerRating) { this.averageCustomerRating = averageCustomerRating; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Integer getTotalViolations() { return totalViolations; }
        public void setTotalViolations(Integer totalViolations) { this.totalViolations = totalViolations; }
    }

    public static class PerformanceMetrics {
        private String driverId;
        private String driverName;
        private Double score;
        private String rank;

        // Getters and setters
        public String getDriverId() { return driverId; }
        public void setDriverId(String driverId) { this.driverId = driverId; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        public String getRank() { return rank; }
        public void setRank(String rank) { this.rank = rank; }
    }

    // Additional supporting classes for other reports...
    public static class EfficiencySummary {
        private Integer totalShipments;
        private Integer completedShipments;
        private Double completionRate;
        private Double averageDeliveryTime;
        private Double onTimeDeliveryRate;
        private Double averageDistance;
        private Double totalRevenue;

        // Getters and setters
        public Integer getTotalShipments() { return totalShipments; }
        public void setTotalShipments(Integer totalShipments) { this.totalShipments = totalShipments; }
        public Integer getCompletedShipments() { return completedShipments; }
        public void setCompletedShipments(Integer completedShipments) { this.completedShipments = completedShipments; }
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
        public Double getAverageDeliveryTime() { return averageDeliveryTime; }
        public void setAverageDeliveryTime(Double averageDeliveryTime) { this.averageDeliveryTime = averageDeliveryTime; }
        public Double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
        public void setOnTimeDeliveryRate(Double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
        public Double getAverageDistance() { return averageDistance; }
        public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    }

    public static class ShipmentEfficiency {
        private String shipmentId;
        private String origin;
        private String destination;
        private Double distance;
        private Double actualTime;
        private Double estimatedTime;
        private Double efficiency;
        private Double cost;
        private Double revenue;

        // Getters and setters
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
        public Double getActualTime() { return actualTime; }
        public void setActualTime(Double actualTime) { this.actualTime = actualTime; }
        public Double getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(Double estimatedTime) { this.estimatedTime = estimatedTime; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
        public Double getCost() { return cost; }
        public void setCost(Double cost) { this.cost = cost; }
        public Double getRevenue() { return revenue; }
        public void setRevenue(Double revenue) { this.revenue = revenue; }
    }

    public static class RouteEfficiency {
        private String routeId;
        private String routeName;
        private Integer totalShipments;
        private Double averageTime;
        private Double averageDistance;
        private Double efficiency;
        private Double totalRevenue;

        // Getters and setters
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public Integer getTotalShipments() { return totalShipments; }
        public void setTotalShipments(Integer totalShipments) { this.totalShipments = totalShipments; }
        public Double getAverageTime() { return averageTime; }
        public void setAverageTime(Double averageTime) { this.averageTime = averageTime; }
        public Double getAverageDistance() { return averageDistance; }
        public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
        public Double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    }

    public static class TimeAnalysis {
        private String period;
        private Double averageTime;
        private Double minTime;
        private Double maxTime;
        private Integer shipments;

        // Getters and setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public Double getAverageTime() { return averageTime; }
        public void setAverageTime(Double averageTime) { this.averageTime = averageTime; }
        public Double getMinTime() { return minTime; }
        public void setMinTime(Double minTime) { this.minTime = minTime; }
        public Double getMaxTime() { return maxTime; }
        public void setMaxTime(Double maxTime) { this.maxTime = maxTime; }
        public Integer getShipments() { return shipments; }
        public void setShipments(Integer shipments) { this.shipments = shipments; }
    }

    public static class DelayAnalysis {
        private String reason;
        private Integer count;
        private Double averageDelay;
        private Double totalDelay;
        private Double percentage;

        // Getters and setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public Double getAverageDelay() { return averageDelay; }
        public void setAverageDelay(Double averageDelay) { this.averageDelay = averageDelay; }
        public Double getTotalDelay() { return totalDelay; }
        public void setTotalDelay(Double totalDelay) { this.totalDelay = totalDelay; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
    }

    public static class FuelSummary {
        private Double totalFuel;
        private Double totalCost;
        private Double averageFuelPerKm;
        private Double totalDistance;
        private Double costPerKm;
        private Double efficiencyImprovement;

        // Getters and setters
        public Double getTotalFuel() { return totalFuel; }
        public void setTotalFuel(Double totalFuel) { this.totalFuel = totalFuel; }
        public Double getTotalCost() { return totalCost; }
        public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
        public Double getAverageFuelPerKm() { return averageFuelPerKm; }
        public void setAverageFuelPerKm(Double averageFuelPerKm) { this.averageFuelPerKm = averageFuelPerKm; }
        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
        public Double getCostPerKm() { return costPerKm; }
        public void setCostPerKm(Double costPerKm) { this.costPerKm = costPerKm; }
        public Double getEfficiencyImprovement() { return efficiencyImprovement; }
        public void setEfficiencyImprovement(Double efficiencyImprovement) { this.efficiencyImprovement = efficiencyImprovement; }
    }

    public static class VehicleFuelConsumption {
        private String vehicleId;
        private String vehicleName;
        private Double totalFuel;
        private Double totalDistance;
        private Double fuelPerKm;
        private Double totalCost;
        private Double efficiency;

        // Getters and setters
        public String getVehicleId() { return vehicleId; }
        public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
        public Double getTotalFuel() { return totalFuel; }
        public void setTotalFuel(Double totalFuel) { this.totalFuel = totalFuel; }
        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
        public Double getFuelPerKm() { return fuelPerKm; }
        public void setFuelPerKm(Double fuelPerKm) { this.fuelPerKm = fuelPerKm; }
        public Double getTotalCost() { return totalCost; }
        public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
    }

    public static class DriverFuelEfficiency {
        private String driverId;
        private String driverName;
        private Double totalFuel;
        private Double totalDistance;
        private Double fuelPerKm;
        private Double efficiency;
        private Double costSavings;

        // Getters and setters
        public String getDriverId() { return driverId; }
        public void setDriverId(String driverId) { this.driverId = driverId; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public Double getTotalFuel() { return totalFuel; }
        public void setTotalFuel(Double totalFuel) { this.totalFuel = totalFuel; }
        public Double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
        public Double getFuelPerKm() { return fuelPerKm; }
        public void setFuelPerKm(Double fuelPerKm) { this.fuelPerKm = fuelPerKm; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
        public Double getCostSavings() { return costSavings; }
        public void setCostSavings(Double costSavings) { this.costSavings = costSavings; }
    }

    public static class RouteFuelAnalysis {
        private String routeId;
        private String routeName;
        private Double averageFuel;
        private Double averageDistance;
        private Double fuelPerKm;
        private Double efficiency;

        // Getters and setters
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public Double getAverageFuel() { return averageFuel; }
        public void setAverageFuel(Double averageFuel) { this.averageFuel = averageFuel; }
        public Double getAverageDistance() { return averageDistance; }
        public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
        public Double getFuelPerKm() { return fuelPerKm; }
        public void setFuelPerKm(Double fuelPerKm) { this.fuelPerKm = fuelPerKm; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
    }

    public static class FuelTrend {
        private String period;
        private Double fuelConsumption;
        private Double cost;
        private Double efficiency;

        // Getters and setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public Double getFuelConsumption() { return fuelConsumption; }
        public void setFuelConsumption(Double fuelConsumption) { this.fuelConsumption = fuelConsumption; }
        public Double getCost() { return cost; }
        public void setCost(Double cost) { this.cost = cost; }
        public Double getEfficiency() { return efficiency; }
        public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
    }

    public static class FuelCostAnalysis {
        private String category;
        private Double amount;
        private Double percentage;
        private Double trend;

        // Getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        public Double getTrend() { return trend; }
        public void setTrend(Double trend) { this.trend = trend; }
    }

    /**
     * Generate Profit/Loss Report
     */
    public ProfitLossReport generateProfitLossReport(LocalDateTime startDate, LocalDateTime endDate) {
        ProfitLossReport report = new ProfitLossReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        // Set period
        ReportPeriod period = new ReportPeriod();
        period.setPeriodType("CUSTOM");
        period.setPeriodLabel(startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " - " + 
                           endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        report.setPeriod(period);
        
        // Calculate financial summary
        FinancialSummary summary = calculateFinancialSummary(startDate, endDate);
        report.setSummary(summary);
        
        // Generate revenue breakdown
        List<RevenueBreakdown> revenueBreakdown = generateRevenueBreakdown(startDate, endDate);
        report.setRevenueBreakdown(revenueBreakdown);
        
        // Generate expense breakdown
        List<ExpenseBreakdown> expenseBreakdown = generateExpenseBreakdown(startDate, endDate);
        report.setExpenseBreakdown(expenseBreakdown);
        
        // Generate monthly trends
        List<MonthlyTrend> monthlyTrends = generateMonthlyTrends(startDate, endDate);
        report.setMonthlyTrends(monthlyTrends);
        
        // Generate profit margin analysis
        List<ProfitMarginAnalysis> profitMargins = generateProfitMarginAnalysis(startDate, endDate);
        report.setProfitMargins(profitMargins);
        
        return report;
    }

    /**
     * Generate Driver Performance Report
     */
    public DriverPerformanceReport generateDriverPerformanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        DriverPerformanceReport report = new DriverPerformanceReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        // Get all drivers
        List<Driver> drivers = driverRepository.findAll();
        
        // Generate performance for each driver
        List<DriverPerformance> performances = new ArrayList<>();
        for (Driver driver : drivers) {
            DriverPerformance performance = calculateDriverPerformance(driver, startDate, endDate);
            performances.add(performance);
        }
        report.setDriverPerformances(performances);
        
        // Generate summary
        PerformanceSummary summary = calculatePerformanceSummary(performances);
        report.setSummary(summary);
        
        // Identify top and under performers
        List<PerformanceMetrics> topPerformers = identifyTopPerformers(performances);
        report.setTopPerformers(topPerformers);
        
        List<PerformanceMetrics> underPerformers = identifyUnderPerformers(performances);
        report.setUnderPerformers(underPerformers);
        
        return report;
    }

    /**
     * Generate Shipment Efficiency Report
     */
    public ShipmentEfficiencyReport generateShipmentEfficiencyReport(LocalDateTime startDate, LocalDateTime endDate) {
        ShipmentEfficiencyReport report = new ShipmentEfficiencyReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        // Get shipments in period
        List<Shipment> shipments = shipmentRepository.findByCreatedAtBetween(startDate, endDate);
        
        // Generate shipment efficiencies
        List<ShipmentEfficiency> efficiencies = generateShipmentEfficiencies(shipments);
        report.setShipmentEfficiencies(efficiencies);
        
        // Generate route efficiencies
        List<RouteEfficiency> routeEfficiencies = generateRouteEfficiencies(shipments);
        report.setRouteEfficiencies(routeEfficiencies);
        
        // Generate time analysis
        List<TimeAnalysis> timeAnalysis = generateTimeAnalysis(shipments);
        report.setTimeAnalysis(timeAnalysis);
        
        // Generate delay analysis
        List<DelayAnalysis> delayAnalysis = generateDelayAnalysis(shipments);
        report.setDelayAnalysis(delayAnalysis);
        
        // Generate summary
        EfficiencySummary summary = calculateEfficiencySummary(shipments);
        report.setSummary(summary);
        
        return report;
    }

    /**
     * Generate Fuel Consumption Report
     */
    public FuelConsumptionReport generateFuelConsumptionReport(LocalDateTime startDate, LocalDateTime endDate) {
        FuelConsumptionReport report = new FuelConsumptionReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        // Get fuel records in period
        List<FuelRecord> fuelRecords = fuelRepository.findByDateBetween(startDate, endDate);
        
        // Generate vehicle consumptions
        List<VehicleFuelConsumption> vehicleConsumptions = generateVehicleFuelConsumptions(fuelRecords);
        report.setVehicleConsumptions(vehicleConsumptions);
        
        // Generate driver efficiencies
        List<DriverFuelEfficiency> driverEfficiencies = generateDriverFuelEfficiencies(fuelRecords);
        report.setDriverEfficiencies(driverEfficiencies);
        
        // Generate route analyses
        List<RouteFuelAnalysis> routeAnalyses = generateRouteFuelAnalyses(fuelRecords);
        report.setRouteAnalyses(routeAnalyses);
        
        // Generate fuel trends
        List<FuelTrend> fuelTrends = generateFuelTrends(fuelRecords);
        report.setFuelTrends(fuelTrends);
        
        // Generate cost analyses
        List<FuelCostAnalysis> costAnalyses = generateFuelCostAnalyses(fuelRecords);
        report.setCostAnalyses(costAnalyses);
        
        // Generate summary
        FuelSummary summary = calculateFuelSummary(fuelRecords);
        report.setSummary(summary);
        
        return report;
    }

    /**
     * Export report to specified format
     */
    public byte[] exportReport(Object report, ExportFormat format) {
        switch (format) {
            case PDF:
                return exportToPDF(report);
            case EXCEL:
                return exportToExcel(report);
            case CSV:
                return exportToCSV(report);
            default:
                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
    }

    // Helper methods for calculations
    private FinancialSummary calculateFinancialSummary(LocalDateTime startDate, LocalDateTime endDate) {
        FinancialSummary summary = new FinancialSummary();
        
        // Calculate total revenue from payments
        Double totalRevenue = paymentRepository.sumAmountByStatusBetweenDates(
            PaymentLifecycleService.PaymentStatus.COMPLETED, startDate, endDate);
        summary.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
        
        // Calculate total expenses
        Double totalExpenses = expenseRepository.sumAmountByDateBetween(startDate, endDate);
        summary.setTotalExpenses(totalExpenses != null ? totalExpenses : 0.0);
        
        // Calculate profits
        summary.setGrossProfit(summary.getTotalRevenue());
        summary.setNetProfit(summary.getTotalRevenue() - summary.getTotalExpenses());
        summary.setProfitMargin(summary.getTotalRevenue() > 0 ? 
            (summary.getNetProfit() / summary.getTotalRevenue()) * 100 : 0.0);
        
        // Calculate growth (compare with previous period)
        LocalDateTime previousStart = startDate.minusMonths(1);
        LocalDateTime previousEnd = endDate.minusMonths(1);
        
        Double previousRevenue = paymentRepository.sumAmountByStatusBetweenDates(
            PaymentLifecycleService.PaymentStatus.COMPLETED, previousStart, previousEnd);
        Double previousExpenses = expenseRepository.sumAmountByDateBetween(previousStart, previousEnd);
        
        summary.setRevenueGrowth(previousRevenue != null && previousRevenue > 0 ? 
            ((summary.getTotalRevenue() - previousRevenue) / previousRevenue) * 100 : 0.0);
        summary.setExpenseGrowth(previousExpenses != null && previousExpenses > 0 ? 
            ((summary.getTotalExpenses() - previousExpenses) / previousExpenses) * 100 : 0.0);
        
        return summary;
    }

    private List<RevenueBreakdown> generateRevenueBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        List<RevenueBreakdown> breakdown = new ArrayList<>();
        
        // Revenue by shipment type
        Map<String, Double> revenueByType = paymentRepository.getRevenueByShipmentType(startDate, endDate);
        Double totalRevenue = revenueByType.values().stream().mapToDouble(Double::doubleValue).sum();
        
        for (Map.Entry<String, Double> entry : revenueByType.entrySet()) {
            RevenueBreakdown item = new RevenueBreakdown();
            item.setCategory(entry.getKey());
            item.setAmount(entry.getValue());
            item.setPercentage(totalRevenue > 0 ? (entry.getValue() / totalRevenue) * 100 : 0.0);
            // Calculate growth (simplified)
            item.setGrowth(0.0);
            breakdown.add(item);
        }
        
        return breakdown;
    }

    private List<ExpenseBreakdown> generateExpenseBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        List<ExpenseBreakdown> breakdown = new ArrayList<>();
        
        // Expenses by category
        Map<String, Double> expensesByCategory = expenseRepository.getExpensesByCategory(startDate, endDate);
        Double totalExpenses = expensesByCategory.values().stream().mapToDouble(Double::doubleValue).sum();
        
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            ExpenseBreakdown item = new ExpenseBreakdown();
            item.setCategory(entry.getKey());
            item.setAmount(entry.getValue());
            item.setPercentage(totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0.0);
            // Calculate growth (simplified)
            item.setGrowth(0.0);
            breakdown.add(item);
        }
        
        return breakdown;
    }

    private List<MonthlyTrend> generateMonthlyTrends(LocalDateTime startDate, LocalDateTime endDate) {
        List<MonthlyTrend> trends = new ArrayList<>();
        
        // Generate monthly data for the period
        LocalDateTime current = startDate.withDayOfMonth(1);
        while (current.isBefore(endDate)) {
            LocalDateTime monthEnd = current.plusMonths(1).minusDays(1);
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            
            MonthlyTrend trend = new MonthlyTrend();
            trend.setMonth(current.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            
            Double revenue = paymentRepository.sumAmountByStatusBetweenDates(
                PaymentLifecycleService.PaymentStatus.COMPLETED, current, monthEnd);
            trend.setRevenue(revenue != null ? revenue : 0.0);
            
            Double expenses = expenseRepository.sumAmountByDateBetween(current, monthEnd);
            trend.setExpenses(expenses != null ? expenses : 0.0);
            
            trend.setProfit(trend.getRevenue() - trend.getExpenses());
            trend.setProfitMargin(trend.getRevenue() > 0 ? 
                (trend.getProfit() / trend.getRevenue()) * 100 : 0.0);
            
            trends.add(trend);
            current = current.plusMonths(1);
        }
        
        return trends;
    }

    private List<ProfitMarginAnalysis> generateProfitMarginAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        List<ProfitMarginAnalysis> analysis = new ArrayList<>();
        
        // Analyze profit margins by different categories
        Map<String, Double> revenueByCategory = paymentRepository.getRevenueByShipmentType(startDate, endDate);
        Map<String, Double> expensesByCategory = expenseRepository.getExpensesByCategory(startDate, endDate);
        
        for (String category : revenueByCategory.keySet()) {
            ProfitMarginAnalysis item = new ProfitMarginAnalysis();
            item.setCategory(category);
            
            Double revenue = revenueByCategory.get(category);
            Double expenses = expensesByCategory.getOrDefault(category, 0.0);
            Double profit = revenue - expenses;
            Double margin = revenue > 0 ? (profit / revenue) * 100 : 0.0;
            
            item.setMargin(margin);
            item.setTrend(0.0); // Simplified trend calculation
            
            // Determine status
            if (margin >= 20.0) {
                item.setStatus("HIGH");
            } else if (margin >= 10.0) {
                item.setStatus("MEDIUM");
            } else {
                item.setStatus("LOW");
            }
            
            analysis.add(item);
        }
        
        return analysis;
    }

    private DriverPerformance calculateDriverPerformance(Driver driver, LocalDateTime startDate, LocalDateTime endDate) {
        DriverPerformance performance = new DriverPerformance();
        performance.setDriverId(driver.getDriverId());
        performance.setDriverName(driver.getName());
        
        // Get shipments for this driver in the period
        List<Shipment> shipments = shipmentRepository.findByDriverIdAndCreatedAtBetween(
            driver.getDriverId(), startDate, endDate);
        
        performance.setTotalShipments(shipments.size());
        
        // Calculate completion rate
        long completedShipments = shipments.stream()
            .filter(s -> s.getStatus() == Shipment.Status.DELIVERED)
            .count();
        performance.setCompletedShipments((int) completedShipments);
        performance.setCompletionRate(shipments.size() > 0 ? 
            ((double) completedShipments / shipments.size()) * 100 : 0.0);
        
        // Calculate average delivery time
        Double avgDeliveryTime = shipments.stream()
            .filter(s -> s.getActualDeliveryTime() != null && s.getEstimatedDeliveryTime() != null)
            .mapToDouble(s -> s.getActualDeliveryTime() - s.getEstimatedDeliveryTime())
            .average()
            .orElse(0.0);
        performance.setAverageDeliveryTime(avgDeliveryTime);
        
        // Calculate on-time delivery rate
        long onTimeDeliveries = shipments.stream()
            .filter(s -> s.getActualDeliveryTime() != null && 
                       s.getEstimatedDeliveryTime() != null &&
                       s.getActualDeliveryTime() <= s.getEstimatedDeliveryTime())
            .count();
        performance.setOnTimeDeliveryRate(shipments.size() > 0 ? 
            ((double) onTimeDeliveries / shipments.size()) * 100 : 0.0);
        
        // Calculate other metrics
        performance.setTotalDistance(shipments.stream()
            .mapToDouble(s -> s.getDistance() != null ? s.getDistance() : 0.0)
            .sum());
        
        performance.setRevenueGenerated(shipments.stream()
            .mapToDouble(s -> s.getCost() != null ? s.getCost() : 0.0)
            .sum());
        
        // Set default values for now
        performance.setCustomerRating(4.5);
        performance.setFuelEfficiency(8.5);
        performance.setViolations(0);
        
        return performance;
    }

    private PerformanceSummary calculatePerformanceSummary(List<DriverPerformance> performances) {
        PerformanceSummary summary = new PerformanceSummary();
        summary.setTotalDrivers(performances.size());
        
        if (performances.isEmpty()) {
            return summary;
        }
        
        summary.setAverageCompletionRate(performances.stream()
            .mapToDouble(DriverPerformance::getCompletionRate)
            .average()
            .orElse(0.0));
        
        summary.setAverageOnTimeDelivery(performances.stream()
            .mapToDouble(DriverPerformance::getOnTimeDeliveryRate)
            .average()
            .orElse(0.0));
        
        summary.setAverageCustomerRating(performances.stream()
            .mapToDouble(DriverPerformance::getCustomerRating)
            .average()
            .orElse(0.0));
        
        summary.setTotalRevenue(performances.stream()
            .mapToDouble(DriverPerformance::getRevenueGenerated)
            .sum());
        
        summary.setTotalViolations(performances.stream()
            .mapToInt(DriverPerformance::getViolations)
            .sum());
        
        return summary;
    }

    private List<PerformanceMetrics> identifyTopPerformers(List<DriverPerformance> performances) {
        return performances.stream()
            .sorted((a, b) -> Double.compare(
                calculatePerformanceScore(b), calculatePerformanceScore(a)))
            .limit(5)
            .map(p -> {
                PerformanceMetrics metrics = new PerformanceMetrics();
                metrics.setDriverId(p.getDriverId());
                metrics.setDriverName(p.getDriverName());
                metrics.setScore(calculatePerformanceScore(p));
                metrics.setRank("TOP");
                return metrics;
            })
            .collect(Collectors.toList());
    }

    private List<PerformanceMetrics> identifyUnderPerformers(List<DriverPerformance> performances) {
        return performances.stream()
            .sorted((a, b) -> Double.compare(
                calculatePerformanceScore(a), calculatePerformanceScore(b)))
            .limit(5)
            .map(p -> {
                PerformanceMetrics metrics = new PerformanceMetrics();
                metrics.setDriverId(p.getDriverId());
                metrics.setDriverName(p.getDriverName());
                metrics.setScore(calculatePerformanceScore(p));
                metrics.setRank("LOW");
                return metrics;
            })
            .collect(Collectors.toList());
    }

    private Double calculatePerformanceScore(DriverPerformance performance) {
        // Simple scoring algorithm
        Double score = 0.0;
        score += performance.getCompletionRate() * 0.3;
        score += performance.getOnTimeDeliveryRate() * 0.3;
        score += performance.getCustomerRating() * 20.0 * 0.2; // Scale rating to 0-100
        score += Math.min(100, performance.getFuelEfficiency() * 10) * 0.2; // Scale efficiency
        return score;
    }

    private List<ShipmentEfficiency> generateShipmentEfficiencies(List<Shipment> shipments) {
        return shipments.stream()
            .map(shipment -> {
                ShipmentEfficiency efficiency = new ShipmentEfficiency();
                efficiency.setShipmentId(shipment.getShipmentId());
                efficiency.setOrigin(shipment.getOrigin());
                efficiency.setDestination(shipment.getDestination());
                efficiency.setDistance(shipment.getDistance());
                efficiency.setActualTime(shipment.getActualDeliveryTime());
                efficiency.setEstimatedTime(shipment.getEstimatedDeliveryTime());
                efficiency.setCost(shipment.getCost());
                efficiency.setRevenue(shipment.getRevenue());
                
                // Calculate efficiency
                if (shipment.getEstimatedDeliveryTime() != null && shipment.getActualDeliveryTime() != null) {
                    efficiency.setEfficiency((shipment.getEstimatedDeliveryTime() / shipment.getActualDeliveryTime()) * 100);
                } else {
                    efficiency.setEfficiency(100.0);
                }
                
                return efficiency;
            })
            .collect(Collectors.toList());
    }

    private List<RouteEfficiency> generateRouteEfficiencies(List<Shipment> shipments) {
        Map<String, List<Shipment>> shipmentsByRoute = shipments.stream()
            .collect(Collectors.groupingBy(s -> s.getOrigin() + "-" + s.getDestination()));
        
        return shipmentsByRoute.entrySet().stream()
            .map(entry -> {
                RouteEfficiency routeEfficiency = new RouteEfficiency();
                routeEfficiency.setRouteId(entry.getKey());
                routeEfficiency.setRouteName(entry.getKey());
                
                List<Shipment> routeShipments = entry.getValue();
                routeEfficiency.setTotalShipments(routeShipments.size());
                
                routeEfficiency.setAverageTime(routeShipments.stream()
                    .filter(s -> s.getActualDeliveryTime() != null)
                    .mapToDouble(Shipment::getActualDeliveryTime)
                    .average()
                    .orElse(0.0));
                
                routeEfficiency.setAverageDistance(routeShipments.stream()
                    .mapToDouble(s -> s.getDistance() != null ? s.getDistance() : 0.0)
                    .average()
                    .orElse(0.0));
                
                routeEfficiency.setTotalRevenue(routeShipments.stream()
                    .mapToDouble(s -> s.getRevenue() != null ? s.getRevenue() : 0.0)
                    .sum());
                
                // Calculate efficiency
                Double estimatedTotal = routeShipments.stream()
                    .filter(s -> s.getEstimatedDeliveryTime() != null)
                    .mapToDouble(Shipment::getEstimatedDeliveryTime)
                    .sum();
                Double actualTotal = routeShipments.stream()
                    .filter(s -> s.getActualDeliveryTime() != null)
                    .mapToDouble(Shipment::getActualDeliveryTime)
                    .sum();
                
                routeEfficiency.setEfficiency(estimatedTotal > 0 ? (estimatedTotal / actualTotal) * 100 : 100.0);
                
                return routeEfficiency;
            })
            .collect(Collectors.toList());
    }

    private List<TimeAnalysis> generateTimeAnalysis(List<Shipment> shipments) {
        // Group by day of week
        Map<String, List<Shipment>> shipmentsByDay = shipments.stream()
            .collect(Collectors.groupingBy(s -> 
                s.getCreatedAt().getDayOfWeek().toString()));
        
        return shipmentsByDay.entrySet().stream()
            .map(entry -> {
                TimeAnalysis analysis = new TimeAnalysis();
                analysis.setPeriod(entry.getKey());
                
                List<Shipment> dayShipments = entry.getValue();
                analysis.setShipments(dayShipments.size());
                
                List<Double> deliveryTimes = dayShipments.stream()
                    .filter(s -> s.getActualDeliveryTime() != null)
                    .mapToDouble(Shipment::getActualDeliveryTime)
                    .boxed()
                    .collect(Collectors.toList());
                
                if (!deliveryTimes.isEmpty()) {
                    analysis.setAverageTime(deliveryTimes.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0));
                    analysis.setMinTime(deliveryTimes.stream()
                        .mapToDouble(Double::doubleValue)
                        .min()
                        .orElse(0.0));
                    analysis.setMaxTime(deliveryTimes.stream()
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .orElse(0.0));
                }
                
                return analysis;
            })
            .collect(Collectors.toList());
    }

    private List<DelayAnalysis> generateDelayAnalysis(List<Shipment> shipments) {
        Map<String, List<Shipment>> delayedShipments = shipments.stream()
            .filter(s -> s.getActualDeliveryTime() != null && s.getEstimatedDeliveryTime() != null &&
                       s.getActualDeliveryTime() > s.getEstimatedDeliveryTime())
            .collect(Collectors.groupingBy(s -> s.getDelayReason() != null ? s.getDelayReason() : "Unknown"));
        
        int totalDelayed = delayedShipments.values().stream()
            .mapToInt(List::size)
            .sum();
        
        return delayedShipments.entrySet().stream()
            .map(entry -> {
                DelayAnalysis analysis = new DelayAnalysis();
                analysis.setReason(entry.getKey());
                
                List<Shipment> reasonShipments = entry.getValue();
                analysis.setCount(reasonShipments.size());
                
                List<Double> delays = reasonShipments.stream()
                    .map(s -> s.getActualDeliveryTime() - s.getEstimatedDeliveryTime())
                    .collect(Collectors.toList());
                
                analysis.setAverageDelay(delays.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0));
                
                analysis.setTotalDelay(delays.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum());
                
                analysis.setPercentage(totalDelayed > 0 ? 
                    ((double) reasonShipments.size() / totalDelayed) * 100 : 0.0);
                
                return analysis;
            })
            .collect(Collectors.toList());
    }

    private EfficiencySummary calculateEfficiencySummary(List<Shipment> shipments) {
        EfficiencySummary summary = new EfficiencySummary();
        summary.setTotalShipments(shipments.size());
        
        long completedShipments = shipments.stream()
            .filter(s -> s.getStatus() == Shipment.Status.DELIVERED)
            .count();
        summary.setCompletedShipments((int) completedShipments);
        summary.setCompletionRate(shipments.size() > 0 ? 
            ((double) completedShipments / shipments.size()) * 100 : 0.0);
        
        summary.setAverageDeliveryTime(shipments.stream()
            .filter(s -> s.getActualDeliveryTime() != null)
            .mapToDouble(Shipment::getActualDeliveryTime)
            .average()
            .orElse(0.0));
        
        long onTimeDeliveries = shipments.stream()
            .filter(s -> s.getActualDeliveryTime() != null && s.getEstimatedDeliveryTime() != null &&
                       s.getActualDeliveryTime() <= s.getEstimatedDeliveryTime())
            .count();
        summary.setOnTimeDeliveryRate(shipments.size() > 0 ? 
            ((double) onTimeDeliveries / shipments.size()) * 100 : 0.0);
        
        summary.setAverageDistance(shipments.stream()
            .mapToDouble(s -> s.getDistance() != null ? s.getDistance() : 0.0)
            .average()
            .orElse(0.0));
        
        summary.setTotalRevenue(shipments.stream()
            .mapToDouble(s -> s.getRevenue() != null ? s.getRevenue() : 0.0)
            .sum());
        
        return summary;
    }

    private List<VehicleFuelConsumption> generateVehicleFuelConsumptions(List<FuelRecord> fuelRecords) {
        Map<String, List<FuelRecord>> recordsByVehicle = fuelRecords.stream()
            .collect(Collectors.groupingBy(FuelRecord::getVehicleId));
        
        return recordsByVehicle.entrySet().stream()
            .map(entry -> {
                VehicleFuelConsumption consumption = new VehicleFuelConsumption();
                consumption.setVehicleId(entry.getKey());
                consumption.setVehicleName("Vehicle " + entry.getKey()); // Simplified
                
                List<FuelRecord> vehicleRecords = entry.getValue();
                consumption.setTotalFuel(vehicleRecords.stream()
                    .mapToDouble(FuelRecord::getAmount)
                    .sum());
                consumption.setTotalDistance(vehicleRecords.stream()
                    .mapToDouble(r -> r.getOdometer() != null ? r.getOdometer() : 0.0)
                    .max()
                    .orElse(0.0));
                consumption.setTotalCost(vehicleRecords.stream()
                    .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
                    .sum());
                
                consumption.setFuelPerKm(consumption.getTotalDistance() > 0 ? 
                    consumption.getTotalFuel() / consumption.getTotalDistance() : 0.0);
                
                // Calculate efficiency (simplified)
                consumption.setEfficiency(consumption.getFuelPerKm() > 0 ? 
                    100.0 / consumption.getFuelPerKm() : 100.0);
                
                return consumption;
            })
            .collect(Collectors.toList());
    }

    private List<DriverFuelEfficiency> generateDriverFuelEfficiencies(List<FuelRecord> fuelRecords) {
        Map<String, List<FuelRecord>> recordsByDriver = fuelRecords.stream()
            .collect(Collectors.groupingBy(FuelRecord::getDriverId));
        
        return recordsByDriver.entrySet().stream()
            .map(entry -> {
                DriverFuelEfficiency efficiency = new DriverFuelEfficiency();
                efficiency.setDriverId(entry.getKey());
                efficiency.setDriverName("Driver " + entry.getKey()); // Simplified
                
                List<FuelRecord> driverRecords = entry.getValue();
                efficiency.setTotalFuel(driverRecords.stream()
                    .mapToDouble(FuelRecord::getAmount)
                    .sum());
                efficiency.setTotalDistance(driverRecords.stream()
                    .mapToDouble(r -> r.getOdometer() != null ? r.getOdometer() : 0.0)
                    .max()
                    .orElse(0.0));
                
                efficiency.setFuelPerKm(efficiency.getTotalDistance() > 0 ? 
                    efficiency.getTotalFuel() / efficiency.getTotalDistance() : 0.0);
                
                // Calculate efficiency and cost savings (simplified)
                efficiency.setEfficiency(efficiency.getFuelPerKm() > 0 ? 
                    100.0 / efficiency.getFuelPerKm() : 100.0);
                efficiency.setCostSavings(0.0); // Simplified
                
                return efficiency;
            })
            .collect(Collectors.toList());
    }

    private List<RouteFuelAnalysis> generateRouteFuelAnalyses(List<FuelRecord> fuelRecords) {
        // Simplified route analysis
        List<RouteFuelAnalysis> analyses = new ArrayList<>();
        
        // Group by route (simplified - would need actual route data)
        Map<String, List<FuelRecord>> recordsByRoute = fuelRecords.stream()
            .collect(Collectors.groupingBy(r -> r.getRoute() != null ? r.getRoute() : "Unknown"));
        
        for (Map.Entry<String, List<FuelRecord>> entry : recordsByRoute.entrySet()) {
            RouteFuelAnalysis analysis = new RouteFuelAnalysis();
            analysis.setRouteId(entry.getKey());
            analysis.setRouteName(entry.getKey());
            
            List<FuelRecord> routeRecords = entry.getValue();
            analysis.setAverageFuel(routeRecords.stream()
                .mapToDouble(FuelRecord::getAmount)
                .average()
                .orElse(0.0));
            analysis.setAverageDistance(routeRecords.stream()
                .mapToDouble(r -> r.getOdometer() != null ? r.getOdometer() : 0.0)
                .average()
                .orElse(0.0));
            
            analysis.setFuelPerKm(analysis.getAverageDistance() > 0 ? 
                analysis.getAverageFuel() / analysis.getAverageDistance() : 0.0);
            
            analysis.setEfficiency(analysis.getFuelPerKm() > 0 ? 
                100.0 / analysis.getFuelPerKm() : 100.0);
            
            analyses.add(analysis);
        }
        
        return analyses;
    }

    private List<FuelTrend> generateFuelTrends(List<FuelRecord> fuelRecords) {
        Map<String, List<FuelRecord>> recordsByMonth = fuelRecords.stream()
            .collect(Collectors.groupingBy(r -> 
                r.getDate().format(DateTimeFormatter.ofPattern("MMM yyyy"))));
        
        return recordsByMonth.entrySet().stream()
            .map(entry -> {
                FuelTrend trend = new FuelTrend();
                trend.setPeriod(entry.getKey());
                
                List<FuelRecord> monthRecords = entry.getValue();
                trend.setFuelConsumption(monthRecords.stream()
                    .mapToDouble(FuelRecord::getAmount)
                    .sum());
                trend.setCost(monthRecords.stream()
                    .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
                    .sum());
                
                // Calculate efficiency (simplified)
                Double totalDistance = monthRecords.stream()
                    .mapToDouble(r -> r.getOdometer() != null ? r.getOdometer() : 0.0)
                    .max()
                    .orElse(0.0);
                trend.setEfficiency(totalDistance > 0 ? 
                    trend.getFuelConsumption() / totalDistance : 0.0);
                
                return trend;
            })
            .collect(Collectors.toList());
    }

    private List<FuelCostAnalysis> generateFuelCostAnalyses(List<FuelRecord> fuelRecords) {
        List<FuelCostAnalysis> analyses = new ArrayList<>();
        
        // Analyze by fuel type
        Map<String, List<FuelRecord>> recordsByType = fuelRecords.stream()
            .collect(Collectors.groupingBy(r -> r.getFuelType() != null ? r.getFuelType() : "Unknown"));
        
        Double totalCost = fuelRecords.stream()
            .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
            .sum();
        
        for (Map.Entry<String, List<FuelRecord>> entry : recordsByType.entrySet()) {
            FuelCostAnalysis analysis = new FuelCostAnalysis();
            analysis.setCategory(entry.getKey());
            
            Double typeCost = entry.getValue().stream()
                .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
                .sum();
            analysis.setAmount(typeCost);
            analysis.setPercentage(totalCost > 0 ? (typeCost / totalCost) * 100 : 0.0);
            analysis.setTrend(0.0); // Simplified trend
            
            analyses.add(analysis);
        }
        
        return analyses;
    }

    private FuelSummary calculateFuelSummary(List<FuelRecord> fuelRecords) {
        FuelSummary summary = new FuelSummary();
        
        summary.setTotalFuel(fuelRecords.stream()
            .mapToDouble(FuelRecord::getAmount)
            .sum());
        
        summary.setTotalCost(fuelRecords.stream()
            .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
            .sum());
        
        Double totalDistance = fuelRecords.stream()
            .mapToDouble(r -> r.getOdometer() != null ? r.getOdometer() : 0.0)
            .max()
            .orElse(0.0);
        summary.setTotalDistance(totalDistance);
        
        summary.setAverageFuelPerKm(totalDistance > 0 ? 
            summary.getTotalFuel() / totalDistance : 0.0);
        
        summary.setCostPerKm(totalDistance > 0 ? 
            summary.getTotalCost() / totalDistance : 0.0);
        
        summary.setEfficiencyImprovement(0.0); // Simplified
        
        return summary;
    }

    // Export methods (simplified - would need actual implementation)
    private byte[] exportToPDF(Object report) {
        // Implementation would use a PDF library like iText or PDFBox
        // For now, return placeholder
        return ("PDF Report: " + report.toString()).getBytes();
    }

    private byte[] exportToExcel(Object report) {
        // Implementation would use Apache POI or similar
        // For now, return placeholder
        return ("Excel Report: " + report.toString()).getBytes();
    }

    private byte[] exportToCSV(Object report) {
        // Implementation would generate CSV format
        // For now, return placeholder
        return ("CSV Report: " + report.toString()).getBytes();
    }
}
