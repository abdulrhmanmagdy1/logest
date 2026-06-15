package com.edham.logistics.service;



import com.edham.logistics.dto.*;

import com.edham.logistics.model.*;

import com.edham.logistics.repository.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;



import java.time.LocalDateTime;

import java.time.Duration;

import java.time.temporal.ChronoUnit;

import java.util.*;

import java.util.stream.Collectors;



/**

 * Advanced maintenance management service

 * Handles scheduling, tracking, and optimization of vehicle maintenance

 */

@Slf4j

@Service

public class MaintenanceService {



    private final MaintenanceRepository maintenanceRepository;

    private final VehicleRepository vehicleRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final ActivityLogService activityLogService;

    private final WebSocketSessionManager webSocketSessionManager;



    @Autowired

    public MaintenanceService(MaintenanceRepository maintenanceRepository,

                            VehicleRepository vehicleRepository,

                            UserRepository userRepository,

                            NotificationService notificationService,

                            ActivityLogService activityLogService,

                            WebSocketSessionManager webSocketSessionManager) {

        this.maintenanceRepository = maintenanceRepository;

        this.vehicleRepository = vehicleRepository;

        this.userRepository = userRepository;

        this.notificationService = notificationService;

        this.activityLogService = activityLogService;

        this.webSocketSessionManager = webSocketSessionManager;

    }



    /**

     * Schedule maintenance for vehicle

     */

    @Async

    public CompletableFuture<MaintenanceResponseDTO> scheduleMaintenance(MaintenanceRequestDTO request) {

        try {

            log.info("Scheduling maintenance for vehicle: {}", request.getVehicleId());



            // Validate vehicle exists

            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())

                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));



            // Create maintenance record

            Maintenance maintenance = Maintenance.builder()

                    .id(generateMaintenanceId())

                    .vehicleId(request.getVehicleId())

                    .licensePlate(vehicle.getLicensePlate())

                    .type(request.getType())

                    .status(MaintenanceStatus.SCHEDULED)

                    .priority(request.getPriority())

                    .title(request.getTitle())

                    .description(request.getDescription())

                    .scheduledDate(request.getScheduledDate())

                    .estimatedDuration(request.getEstimatedDuration())

                    .estimatedCost(request.getEstimatedCost())

                    .assignedMechanicId(request.getAssignedMechanicId())

                    .workshopName(request.getWorkshopName())

                    .workshopLocation(request.getWorkshopLocation())

                    .odometerReading(request.getOdometerReading())

                    .reportedIssues(request.getReportedIssues())

                    .toolsRequired(request.getToolsRequired())

                    .createdAt(LocalDateTime.now())

                    .build();



            // Set mechanic name if assigned

            if (request.getAssignedMechanicId() != null) {

                User mechanic = userRepository.findById(request.getAssignedMechanicId()).orElse(null);

                if (mechanic != null) {

                    maintenance.setMechanicName(mechanic.getFullName());

                }

            }



            // Save maintenance record

            maintenance = maintenanceRepository.save(maintenance);



            // Update vehicle maintenance status

            vehicle.setMaintenanceRequired(true);

            vehicleRepository.save(vehicle);



            // Send notifications

            sendMaintenanceScheduledNotifications(maintenance, vehicle);



            // Log activity

            logMaintenanceActivity(maintenance, "MAINTENANCE_SCHEDULED");



            return CompletableFuture.completedFuture(

                    MaintenanceResponseDTO.builder()

                            .success(true)

                            .maintenanceId(maintenance.getId())

                            .message("Maintenance scheduled successfully")

                            .timestamp(LocalDateTime.now())

                            .build()

            );



        } catch (Exception e) {

            log.error("Error scheduling maintenance: {}", e.getMessage(), e);

            return CompletableFuture.completedFuture(

                    MaintenanceResponseDTO.builder()

                            .success(false)

                            .error("Failed to schedule maintenance: " + e.getMessage())

                            .timestamp(LocalDateTime.now())

                            .build()

            );

        }

    }



    /**

     * Get scheduled maintenance for vehicle

     */

    public List<Maintenance> getScheduledMaintenance(Long vehicleId) {

        try {

            return maintenanceRepository.findByVehicleIdAndStatusIn(

                    vehicleId, 

                    Arrays.asList(MaintenanceStatus.SCHEDULED, MaintenanceStatus.CONFIRMED)

            );

        } catch (Exception e) {

            log.error("Error getting scheduled maintenance: {}", e.getMessage(), e);

            return Collections.emptyList();

        }

    }



    /**

     * Get overdue maintenance

     */

    public List<Maintenance> getOverdueMaintenance() {

        try {

            return maintenanceRepository.findOverdueMaintenance(LocalDateTime.now());

        } catch (Exception e) {

            log.error("Error getting overdue maintenance: {}", e.getMessage(), e);

            return Collections.emptyList();

        }

    }



    /**

     * Get upcoming maintenance

     */

    public List<Maintenance> getUpcomingMaintenance(int days) {

        try {

            LocalDateTime now = LocalDateTime.now();

            LocalDateTime future = now.plusDays(days);

            return maintenanceRepository.findUpcomingMaintenance(now, future);

        } catch (Exception e) {

            log.error("Error getting upcoming maintenance: {}", e.getMessage(), e);

            return Collections.emptyList();

        }

    }



    /**

     * Update maintenance status

     */

    @Async

    public CompletableFuture<MaintenanceResponseDTO> updateMaintenanceStatus(

            String maintenanceId, MaintenanceStatus status, String notes) {

        try {

            Maintenance maintenance = maintenanceRepository.findById(maintenanceId)

                    .orElseThrow(() -> new RuntimeException("Maintenance not found"));



            MaintenanceStatus oldStatus = maintenance.getStatus();

            maintenance.setStatus(status);

            maintenance.setUpdatedAt(LocalDateTime.now());



            // Handle status-specific updates

            switch (status) {

                case IN_PROGRESS:

                    maintenance.setActualStartDate(LocalDateTime.now());

                    break;

                case COMPLETED:

                    maintenance.setCompletionDate(LocalDateTime.now());

                    maintenance.setQualityCheckPassed(true);

                    updateVehicleAfterMaintenance(maintenance);

                    break;

                case CANCELLED:

                    updateVehicleAfterMaintenanceCancellation(maintenance);

                    break;

            }



            if (notes != null) {

                maintenance.setResolutionNotes(notes);

            }



            maintenance = maintenanceRepository.save(maintenance);



            // Send notifications

            sendMaintenanceStatusUpdateNotifications(maintenance, oldStatus);



            // Log activity

            logMaintenanceActivity(maintenance, "MAINTENANCE_STATUS_UPDATED");



            return CompletableFuture.completedFuture(

                    MaintenanceResponseDTO.builder()

                            .success(true)

                            .maintenanceId(maintenanceId)

                            .message("Maintenance status updated successfully")

                            .timestamp(LocalDateTime.now())

                            .build()

            );



        } catch (Exception e) {

            log.error("Error updating maintenance status: {}", e.getMessage(), e);

            return CompletableFuture.completedFuture(

                    MaintenanceResponseDTO.builder()

                            .success(false)

                            .error("Failed to update maintenance status: " + e.getMessage())

                            .timestamp(LocalDateTime.now())

                            .build()

            );

        }

    }



    /**

     * Get maintenance statistics

     */

    public MaintenanceStatisticsDTO getMaintenanceStatistics(LocalDateTime startDate, LocalDateTime endDate) {

        try {

            // Get completed maintenance in date range

            List<Maintenance> completedMaintenance = maintenanceRepository.findByCompletionDateBetween(startDate, endDate);

            

            // Get all maintenance in date range

            List<Maintenance> allMaintenance = maintenanceRepository.findByScheduledDateBetween(startDate, endDate);



            // Calculate statistics

            long totalMaintenance = allMaintenance.size();

            long completedCount = completedMaintenance.size();

            long overdueCount = maintenanceRepository.findOverdueMaintenance(startDate).size();

            

            double totalCost = completedMaintenance.stream()

                    .mapToDouble(Maintenance::getTotalCost)

                    .sum();

            

            double averageCost = completedCount > 0 ? totalCost / completedCount : 0.0;

            

            Map<MaintenanceType, Long> maintenanceByType = completedMaintenance.stream()

                    .collect(Collectors.groupingBy(Maintenance::getType, Collectors.counting()));

            

            Map<MaintenancePriority, Long> maintenanceByPriority = allMaintenance.stream()

                    .collect(Collectors.groupingBy(Maintenance::getPriority, Collectors.counting()));



            // Calculate downtime

            long totalDowntimeHours = completedMaintenance.stream()

                    .filter(m -> m.getDowntimeHours() != null)

                    .mapToLong(Maintenance::getDowntimeHours)

                    .sum();



            return MaintenanceStatisticsDTO.builder()

                    .totalMaintenance(totalMaintenance)

                    .completedMaintenance(completedCount)

                    .overdueMaintenance(overdueCount)

                    .totalCost(totalCost)

                    .averageCost(averageCost)

                    .totalDowntimeHours(totalDowntimeHours)

                    .maintenanceByType(maintenanceByType)

                    .maintenanceByPriority(maintenanceByPriority)

                    .completionRate(totalMaintenance > 0 ? (double) completedCount / totalMaintenance : 0.0)

                    .generatedAt(LocalDateTime.now())

                    .build();



        } catch (Exception e) {

            log.error("Error getting maintenance statistics: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to get maintenance statistics", e);

        }

    }



    /**

     * Get maintenance dashboard data

     */

    public MaintenanceDashboardDTO getMaintenanceDashboard() {

        try {

            LocalDateTime now = LocalDateTime.now();

            LocalDateTime today = now.toLocalDate().atStartOfDay();

            LocalDateTime weekFromNow = now.plusWeeks(1);



            // Get various maintenance lists

            List<Maintenance> overdue = getOverdueMaintenance();

            List<Maintenance> todayMaintenance = maintenanceRepository.findByScheduledDateBetween(today, now);

            List<Maintenance> upcoming = maintenanceRepository.findUpcomingMaintenance(now, weekFromNow);

            List<Maintenance> inProgress = maintenanceRepository.findByStatus(MaintenanceStatus.IN_PROGRESS);



            // Calculate statistics

            MaintenanceStatisticsDTO last30DaysStats = getMaintenanceStatistics(now.minusDays(30), now);

            MaintenanceStatisticsDTO last7DaysStats = getMaintenanceStatistics(now.minusDays(7), now);



            // Get vehicle health summary

            List<Vehicle> allVehicles = vehicleRepository.findAll();

            long vehiclesNeedingMaintenance = allVehicles.stream()

                    .filter(v -> v.getMaintenanceRequired() != null && v.getMaintenanceRequired())

                    .count();



            return MaintenanceDashboardDTO.builder()

                    .overdueMaintenance(overdue)

                    .todayMaintenance(todayMaintenance)

                    .upcomingMaintenance(upcoming)

                    .inProgressMaintenance(inProgress)

                    .last30DaysStats(last30DaysStats)

                    .last7DaysStats(last7DaysStats)

                    .totalVehicles(allVehicles.size())

                    .vehiclesNeedingMaintenance((int) vehiclesNeedingMaintenance)

                    .generatedAt(LocalDateTime.now())

                    .build();



        } catch (Exception e) {

            log.error("Error getting maintenance dashboard: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to get maintenance dashboard", e);

        }

    }



    /**

     * Generate automatic maintenance schedule

     */

    @Scheduled(cron = "0 0 8 * * ?") // Daily at 8 AM

    public void generateAutomaticMaintenanceSchedule() {

        try {

            log.info("Generating automatic maintenance schedule");



            List<Vehicle> vehicles = vehicleRepository.findAll();

            

            for (Vehicle vehicle : vehicles) {

                // Check if vehicle needs routine maintenance

                if (shouldScheduleRoutineMaintenance(vehicle)) {

                    scheduleRoutineMaintenance(vehicle);

                }

                

                // Check for predictive maintenance needs

                if (shouldSchedulePredictiveMaintenance(vehicle)) {

                    schedulePredictiveMaintenance(vehicle);

                }

            }



            log.info("Automatic maintenance schedule generation completed");



        } catch (Exception e) {

            log.error("Error generating automatic maintenance schedule: {}", e.getMessage(), e);

        }

    }



    /**

     * Send maintenance reminders

     */

    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM

    public void sendMaintenanceReminders() {

        try {

            log.info("Sending maintenance reminders");



            // Get upcoming maintenance in next 3 days

            List<Maintenance> upcoming = getUpcomingMaintenance(3);

            

            for (Maintenance maintenance : upcoming) {

                sendMaintenanceReminder(maintenance);

            }



            // Get overdue maintenance

            List<Maintenance> overdue = getOverdueMaintenance();

            

            for (Maintenance maintenance : overdue) {

                sendOverdueMaintenanceAlert(maintenance);

            }



            log.info("Maintenance reminders sent successfully");



        } catch (Exception e) {

            log.error("Error sending maintenance reminders: {}", e.getMessage(), e);

        }

    }



    // Helper methods

    private String generateMaintenanceId() {

        return "MNT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);

    }



    private void sendMaintenanceScheduledNotifications(Maintenance maintenance, Vehicle vehicle) {

        try {

            // Send to assigned mechanic

            if (maintenance.getAssignedMechanicId() != null) {

                String message = String.format("Maintenance scheduled for %s on %s", 

                        vehicle.getLicensePlate(), maintenance.getScheduledDate());

                

                notificationService.sendPushNotification(maintenance.getAssignedMechanicId(), 

                        NotificationMessageDTO.builder()

                                .title("Maintenance Scheduled")

                                .message(message)

                                .type(NotificationType.MAINTENANCE)

                                .build());

            }



            // Send to supervisors

            List<User> supervisors = userRepository.findByRole(UserRole.SUPERVISOR);

            for (User supervisor : supervisors) {

                notificationService.sendPushNotification(supervisor.getId(),

                        NotificationMessageDTO.builder()

                                .title("Maintenance Scheduled")

                                .message(String.format("Maintenance scheduled for vehicle %s", vehicle.getLicensePlate()))

                                .type(NotificationType.MAINTENANCE)

                                .build());

            }



        } catch (Exception e) {

            log.error("Error sending maintenance scheduled notifications: {}", e.getMessage(), e);

        }

    }



    private void sendMaintenanceStatusUpdateNotifications(Maintenance maintenance, MaintenanceStatus oldStatus) {

        try {

            String message = String.format("Maintenance status updated from %s to %s for vehicle %s", 

                    oldStatus, maintenance.getStatus(), maintenance.getLicensePlate());



            // Send to assigned mechanic

            if (maintenance.getAssignedMechanicId() != null) {

                notificationService.sendPushNotification(maintenance.getAssignedMechanicId(),

                        NotificationMessageDTO.builder()

                                .title("Maintenance Status Updated")

                                .message(message)

                                .type(NotificationType.MAINTENANCE)

                                .build());

            }



            // Send to supervisors

            List<User> supervisors = userRepository.findByRole(UserRole.SUPERVISOR);

            for (User supervisor : supervisors) {

                notificationService.sendPushNotification(supervisor.getId(),

                        NotificationMessageDTO.builder()

                                .title("Maintenance Status Updated")

                                .message(message)

                                .type(NotificationType.MAINTENANCE)

                                .build());

            }



        } catch (Exception e) {

            log.error("Error sending maintenance status update notifications: {}", e.getMessage(), e);

        }

    }



    private void updateVehicleAfterMaintenance(Maintenance maintenance) {

        try {

            Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicleId()).orElse(null);

            if (vehicle != null) {

                vehicle.setMaintenanceRequired(false);

                vehicle.setLastMaintenanceDate(LocalDateTime.now());

                vehicleRepository.save(vehicle);

            }

        } catch (Exception e) {

            log.error("Error updating vehicle after maintenance: {}", e.getMessage(), e);

        }

    }



    private void updateVehicleAfterMaintenanceCancellation(Maintenance maintenance) {

        try {

            Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicleId()).orElse(null);

            if (vehicle != null) {

                // Check if there are other scheduled maintenance for this vehicle

                List<Maintenance> otherMaintenance = maintenanceRepository.findByVehicleIdAndStatusIn(

                        vehicle.getId(), 

                        Arrays.asList(MaintenanceStatus.SCHEDULED, MaintenanceStatus.CONFIRMED, MaintenanceStatus.IN_PROGRESS)

                );

                

                if (otherMaintenance.isEmpty()) {

                    vehicle.setMaintenanceRequired(false);

                }

                

                vehicleRepository.save(vehicle);

            }

        } catch (Exception e) {

            log.error("Error updating vehicle after maintenance cancellation: {}", e.getMessage(), e);

        }

    }



    private void logMaintenanceActivity(Maintenance maintenance, String activityType) {

        try {

            Map<String, Object> metadata = Map.of(

                    "maintenanceId", maintenance.getId(),

                    "vehicleId", maintenance.getVehicleId(),

                    "maintenanceType", maintenance.getType(),

                    "status", maintenance.getStatus()

            );



            activityLogService.logActivity(

                    maintenance.getAssignedMechanicId() != null ? maintenance.getAssignedMechanicId() : 0L,

                    ActivityType.VEHICLE_MAINTENANCE,

                    "MAINTENANCE",

                    maintenance.getId(),

                    activityType,

                    metadata

            );



        } catch (Exception e) {

            log.error("Error logging maintenance activity: {}", e.getMessage(), e);

        }

    }



    private boolean shouldScheduleRoutineMaintenance(Vehicle vehicle) {

        // Simplified routine maintenance scheduling logic

        if (vehicle.getLastMaintenanceDate() == null) {

            return true; // New vehicle needs initial maintenance

        }



        long daysSinceLastMaintenance = ChronoUnit.DAYS.between(

                vehicle.getLastMaintenanceDate(), LocalDateTime.now());

        

        return daysSinceLastMaintenance > 90; // Schedule every 90 days

    }



    private boolean shouldSchedulePredictiveMaintenance(Vehicle vehicle) {

        // Simplified predictive maintenance logic

        // In real implementation, this would use sensor data, mileage, etc.

        return vehicle.getFuelLevel() != null && vehicle.getFuelLevel() < 10.0;

    }



    private void scheduleRoutineMaintenance(Vehicle vehicle) {

        try {

            MaintenanceRequestDTO request = MaintenanceRequestDTO.builder()

                    .vehicleId(vehicle.getId())

                    .type(MaintenanceType.ROUTINE_INSPECTION)

                    .priority(MaintenancePriority.MEDIUM)

                    .title("Routine Maintenance")

                    .description("Scheduled routine maintenance check")

                    .scheduledDate(LocalDateTime.now().plusDays(7))

                    .estimatedDuration(Duration.ofHours(2))

                    .estimatedCost(150.0)

                    .build();



            scheduleMaintenance(request);

            log.info("Scheduled routine maintenance for vehicle: {}", vehicle.getLicensePlate());



        } catch (Exception e) {

            log.error("Error scheduling routine maintenance: {}", e.getMessage(), e);

        }

    }



    private void schedulePredictiveMaintenance(Vehicle vehicle) {

        try {

            MaintenanceRequestDTO request = MaintenanceRequestDTO.builder()

                    .vehicleId(vehicle.getId())

                    .type(MaintenanceType.CONDITION_MONITORING)

                    .priority(MaintenancePriority.HIGH)

                    .title("Predictive Maintenance")

                    .description("Predictive maintenance based on vehicle condition")

                    .scheduledDate(LocalDateTime.now().plusDays(3))

                    .estimatedDuration(Duration.ofHours(1))

                    .estimatedCost(100.0)

                    .build();



            scheduleMaintenance(request);

            log.info("Scheduled predictive maintenance for vehicle: {}", vehicle.getLicensePlate());



        } catch (Exception e) {

            log.error("Error scheduling predictive maintenance: {}", e.getMessage(), e);

        }

    }



    private void sendMaintenanceReminder(Maintenance maintenance) {

        try {

            String message = String.format("Reminder: Maintenance scheduled for %s on %s", 

                    maintenance.getLicensePlate(), maintenance.getScheduledDate());



            if (maintenance.getAssignedMechanicId() != null) {

                notificationService.sendPushNotification(maintenance.getAssignedMechanicId(),

                        NotificationMessageDTO.builder()

                                .title("Maintenance Reminder")

                                .message(message)

                                .type(NotificationType.MAINTENANCE)

                                .build());

            }



        } catch (Exception e) {

            log.error("Error sending maintenance reminder: {}", e.getMessage(), e);

        }

    }



    private void sendOverdueMaintenanceAlert(Maintenance maintenance) {

        try {

            String message = String.format("URGENT: Maintenance overdue for %s since %s", 

                    maintenance.getLicensePlate(), maintenance.getScheduledDate());



            // Send to supervisors

            List<User> supervisors = userRepository.findByRole(UserRole.SUPERVISOR);

            for (User supervisor : supervisors) {

                notificationService.sendPushNotification(supervisor.getId(),

                        NotificationMessageDTO.builder()

                                .title("Overdue Maintenance Alert")

                                .message(message)

                                .type(NotificationType.EMERGENCY)

                                .build());

            }



        } catch (Exception e) {

            log.error("Error sending overdue maintenance alert: {}", e.getMessage(), e);

        }

    }

}

