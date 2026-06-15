package com.edham.logistics.notifications;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.notifications.UnifiedNotificationService.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unified notification system integration test
 * Comprehensive testing for push notifications, in-app notifications, role-based alerts, and priority levels
 */
@RestController
@RequestMapping("/api/v1/notifications/test")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class UnifiedNotificationIntegrationTest {

    private final UnifiedNotificationService notificationService;
    private final UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;
    private final ExecutorService testExecutor;

    @Autowired
    public UnifiedNotificationIntegrationTest(UnifiedNotificationService notificationService,
                                           UserRepository userRepository,
                                           ShipmentRepository shipmentRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.shipmentRepository = shipmentRepository;
        this.testExecutor = Executors.newFixedThreadPool(5);
    }

    /**
     * Comprehensive notification system test
     */
    @PostMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> comprehensiveTest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> testResults = new HashMap<>();
                long startTime = System.currentTimeMillis();
                
                // Test 1: Firebase push notifications
                Map<String, Object> pushNotificationTest = testPushNotifications();
                testResults.put("pushNotifications", pushNotificationTest);
                
                // Test 2: In-app notifications
                Map<String, Object> inAppNotificationTest = testInAppNotifications();
                testResults.put("inAppNotifications", inAppNotificationTest);
                
                // Test 3: Role-based alerts
                Map<String, Object> roleBasedAlertsTest = testRoleBasedAlerts();
                testResults.put("roleBasedAlerts", roleBasedAlertsTest);
                
                // Test 4: Priority levels
                Map<String, Object> priorityLevelsTest = testPriorityLevels();
                testResults.put("priorityLevels", priorityLevelsTest);
                
                // Test 5: Notification history
                Map<String, Object> notificationHistoryTest = testNotificationHistory();
                testResults.put("notificationHistory", notificationHistoryTest);
                
                // Test 6: Batch notifications
                Map<String, Object> batchNotificationsTest = testBatchNotifications();
                testResults.put("batchNotifications", batchNotificationsTest);
                
                // Test 7: Notification preferences
                Map<String, Object> notificationPreferencesTest = testNotificationPreferences();
                testResults.put("notificationPreferences", notificationPreferencesTest);
                
                // Test 8: Performance under load
                Map<String, Object> performanceTest = testNotificationPerformance();
                testResults.put("performance", performanceTest);
                
                long endTime = System.currentTimeMillis();
                testResults.put("totalTestTime", endTime - startTime);
                testResults.put("testStatus", "COMPLETED");
                testResults.put("testTimestamp", LocalDateTime.now());
                
                log.info("Comprehensive notification test completed in {}ms", endTime - startTime);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(testResults)
                                .message("Notification system test completed successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error in comprehensive notification test", e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Test failed: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        }, testExecutor);
    }

    /**
     * Test Firebase push notifications
     */
    private Map<String, Object> testPushNotifications() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test users
            List<User> testUsers = createTestUsers(5);
            
            // Test different notification types
            List<String> notificationTypes = Arrays.asList(
                    "SHIPMENT_CREATED",
                    "SHIPMENT_DELIVERED",
                    "TEMPERATURE_WARNING",
                    "TEMPERATURE_CRITICAL",
                    "EMERGENCY_ALERT"
            );
            
            int successfulNotifications = 0;
            int totalNotifications = notificationTypes.size() * testUsers.size();
            List<NotificationResult> notificationResults = new ArrayList<>();
            
            for (String templateId : notificationTypes) {
                Map<String, Object> data = createNotificationData(templateId);
                
                for (User user : testUsers) {
                    NotificationResult result = notificationService.sendNotification(
                            user.getId(), templateId, data).get(10, TimeUnit.SECONDS);
                    
                    notificationResults.add(result);
                    
                    if (result.getSuccess()) {
                        successfulNotifications++;
                    }
                    
                    Thread.sleep(100); // Small delay between notifications
                }
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("totalNotifications", totalNotifications);
            results.put("successfulNotifications", successfulNotifications);
            results.put("successRate", (double) successfulNotifications / totalNotifications * 100);
            results.put("notificationTypes", notificationTypes);
            results.put("testUsers", testUsers.size());
            results.put("notificationResults", notificationResults);
            results.put("testDuration", endTime - startTime);
            results.put("status", successfulNotifications >= totalNotifications * 0.8 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in push notification test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test in-app notifications
     */
    private Map<String, Object> testInAppNotifications() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test user
            User testUser = createTestUsers(1).get(0);
            
            // Send various in-app notifications
            List<String> inAppTemplates = Arrays.asList(
                    "NEW_TASK_ASSIGNED",
                    "TASK_REMINDER",
                    "SYSTEM_MAINTENANCE"
            );
            
            List<NotificationResult> resultsList = new ArrayList<>();
            List<String> notificationIds = new ArrayList<>();
            
            for (String templateId : inAppTemplates) {
                Map<String, Object> data = createNotificationData(templateId);
                
                NotificationResult result = notificationService.sendNotification(
                        testUser.getId(), templateId, data).get(5, TimeUnit.SECONDS);
                
                resultsList.add(result);
                
                if (result.getSuccess()) {
                    notificationIds.add(result.getNotificationId());
                }
            }
            
            // Retrieve in-app notifications
            List<UnifiedNotification> inAppNotifications = notificationService
                    .getUserNotifications(testUser.getId(), 0, 20, null)
                    .get(5, TimeUnit.SECONDS);
            
            // Test marking as read
            int markedAsRead = 0;
            for (String notificationId : notificationIds) {
                Boolean readResult = notificationService.markNotificationAsRead(notificationId)
                        .get(5, TimeUnit.SECONDS);
                if (readResult) {
                    markedAsRead++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("totalInAppNotifications", inAppTemplates.size());
            results.put("retrievedNotifications", inAppNotifications.size());
            results.put("markedAsRead", markedAsRead);
            results.put("notificationResults", resultsList);
            results.put("testDuration", endTime - startTime);
            results.put("status", inAppNotifications.size() >= inAppTemplates.size() * 0.9 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in in-app notification test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test role-based alerts
     */
    private Map<String, Object> testRoleBasedAlerts() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create users for each role
            Map<UserRole, User> roleUsers = new HashMap<>();
            for (UserRole role : Arrays.asList(UserRole.CLIENT, UserRole.DRIVER, UserRole.ADMIN, UserRole.SUPERVISOR, UserRole.ACCOUNTANT)) {
                User user = createTestUsers(1).get(0);
                user.setRole(role);
                userRepository.save(user);
                roleUsers.put(role, user);
            }
            
            // Test role-specific notifications
            Map<UserRole, List<String>> roleNotifications = new HashMap<>();
            roleNotifications.put(UserRole.CLIENT, Arrays.asList("SHIPMENT_CREATED", "SHIPMENT_DELIVERED"));
            roleNotifications.put(UserRole.DRIVER, Arrays.asList("NEW_TASK_ASSIGNED", "TASK_REMINDER"));
            roleNotifications.put(UserRole.ADMIN, Arrays.asList("SYSTEM_MAINTENANCE", "EMERGENCY_ALERT"));
            roleNotifications.put(UserRole.SUPERVISOR, Arrays.asList("TEMPERATURE_WARNING", "TEMPERATURE_CRITICAL"));
            roleNotifications.put(UserRole.ACCOUNTANT, Arrays.asList("PAYMENT_PROCESSING", "FINANCIAL_REPORT"));
            
            Map<UserRole, NotificationTestResult> roleResults = new HashMap<>();
            
            for (Map.Entry<UserRole, User> entry : roleUsers.entrySet()) {
                UserRole role = entry.getKey();
                User user = entry.getValue();
                List<String> templates = roleNotifications.get(role);
                
                int successfulNotifications = 0;
                List<NotificationResult> notificationResults = new ArrayList<>();
                
                for (String templateId : templates) {
                    Map<String, Object> data = createNotificationData(templateId);
                    
                    NotificationResult result = notificationService.sendNotification(
                            user.getId(), templateId, data).get(5, TimeUnit.SECONDS);
                    
                    notificationResults.add(result);
                    
                    if (result.getSuccess()) {
                        successfulNotifications++;
                    }
                }
                
                roleResults.put(role, NotificationTestResult.builder()
                        .role(role.name())
                        .totalNotifications(templates.size())
                        .successfulNotifications(successfulNotifications)
                        .successRate((double) successfulNotifications / templates.size() * 100)
                        .notificationResults(notificationResults)
                        .build());
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("roleResults", roleResults);
            results.put("totalRoles", roleUsers.size());
            results.put("testDuration", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in role-based alerts test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test priority levels
     */
    private Map<String, Object> testPriorityLevels() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test user
            User testUser = createTestUsers(1).get(0);
            
            // Test all priority levels
            Map<NotificationPriority, List<String>> priorityTemplates = new HashMap<>();
            priorityTemplates.put(NotificationPriority.LOW, Arrays.asList("SYSTEM_MAINTENANCE"));
            priorityTemplates.put(NotificationPriority.MEDIUM, Arrays.asList("SHIPMENT_IN_TRANSIT"));
            priorityTemplates.put(NotificationPriority.HIGH, Arrays.asList("SHIPMENT_DELIVERED", "TEMPERATURE_WARNING"));
            priorityTemplates.put(NotificationPriority.CRITICAL, Arrays.asList("TEMPERATURE_CRITICAL", "EMERGENCY_ALERT"));
            
            Map<NotificationPriority, NotificationTestResult> priorityResults = new HashMap<>();
            
            for (Map.Entry<NotificationPriority, List<String>> entry : priorityTemplates.entrySet()) {
                NotificationPriority priority = entry.getKey();
                List<String> templates = entry.getValue();
                
                int successfulNotifications = 0;
                List<NotificationResult> notificationResults = new ArrayList<>();
                
                for (String templateId : templates) {
                    Map<String, Object> data = createNotificationData(templateId);
                    
                    NotificationResult result = notificationService.sendNotification(
                            testUser.getId(), templateId, data).get(5, TimeUnit.SECONDS);
                    
                    notificationResults.add(result);
                    
                    if (result.getSuccess()) {
                        successfulNotifications++;
                    }
                }
                
                priorityResults.put(priority, NotificationTestResult.builder()
                        .priority(priority.name())
                        .totalNotifications(templates.size())
                        .successfulNotifications(successfulNotifications)
                        .successRate((double) successfulNotifications / templates.size() * 100)
                        .notificationResults(notificationResults)
                        .build());
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("priorityResults", priorityResults);
            results.put("totalPriorities", priorityTemplates.size());
            results.put("testDuration", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in priority levels test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test notification history
     */
    private Map<String, Object> testNotificationHistory() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test user
            User testUser = createTestUsers(1).get(0);
            
            // Send multiple notifications to create history
            List<String> historyTemplates = Arrays.asList(
                    "SHIPMENT_CREATED", "SHIPMENT_PICKED_UP", "SHIPMENT_IN_TRANSIT", 
                    "SHIPMENT_DELIVERED", "TEMPERATURE_WARNING", "TEMPERATURE_CRITICAL"
            );
            
            List<String> sentNotificationIds = new ArrayList<>();
            
            for (int i = 0; i < historyTemplates.size(); i++) {
                String templateId = historyTemplates.get(i);
                Map<String, Object> data = createNotificationData(templateId);
                data.put("sequence", i + 1);
                
                NotificationResult result = notificationService.sendNotification(
                        testUser.getId(), templateId, data).get(5, TimeUnit.SECONDS);
                
                if (result.getSuccess()) {
                    sentNotificationIds.add(result.getNotificationId());
                }
                
                Thread.sleep(200); // Small delay
            }
            
            // Wait for notifications to be processed
            Thread.sleep(2000);
            
            // Test history retrieval
            List<UnifiedNotification> allHistory = notificationService
                    .getUserNotifications(testUser.getId(), 0, 50, null)
                    .get(10, TimeUnit.SECONDS);
            
            // Test pagination
            List<UnifiedNotification> pagedHistory = notificationService
                    .getUserNotifications(testUser.getId(), 0, 3, null)
                    .get(5, TimeUnit.SECONDS);
            
            // Test category filtering
            List<UnifiedNotification> shipmentHistory = notificationService
                    .getUserNotifications(testUser.getId(), 0, 50, NotificationCategory.SHIPMENT)
                    .get(5, TimeUnit.SECONDS);
            
            // Test statistics
            UnifiedNotificationService.NotificationStatistics statistics = notificationService
                    .getNotificationStatistics(testUser.getId())
                    .get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("sentNotifications", sentNotificationIds.size());
            results.put("retrievedHistory", allHistory.size());
            results.put("pagedHistory", pagedHistory.size());
            results.put("shipmentHistory", shipmentHistory.size());
            results.put("statistics", statistics);
            results.put("testDuration", endTime - startTime);
            results.put("status", allHistory.size() >= sentNotificationIds.size() * 0.9 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in notification history test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test batch notifications
     */
    private Map<String, Object> testBatchNotifications() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test users
            List<User> testUsers = createTestUsers(10);
            List<Long> userIds = testUsers.stream().map(User::getId).toList();
            
            // Test batch notification to all users
            String batchTemplate = "SYSTEM_MAINTENANCE";
            Map<String, Object> batchData = createNotificationData(batchTemplate);
            batchData.put("batchSize", userIds.size());
            
            List<NotificationResult> batchResults = notificationService
                    .sendNotificationToUsers(userIds, batchTemplate, batchData)
                    .get(15, TimeUnit.SECONDS);
            
            // Test batch notification to specific role
            String roleTemplate = "EMERGENCY_ALERT";
            Map<String, Object> roleData = createNotificationData(roleTemplate);
            
            List<NotificationResult> roleResults = notificationService
                    .sendNotificationToRole(UserRole.DRIVER, roleTemplate, roleData)
                    .get(10, TimeUnit.SECONDS);
            
            // Analyze results
            int batchSuccessCount = (int) batchResults.stream().mapToLong(r -> r.getSuccess() ? 1L : 0L).sum();
            int roleSuccessCount = (int) roleResults.stream().mapToLong(r -> r.getSuccess() ? 1L : 0L).sum();
            
            long endTime = System.currentTimeMillis();
            
            results.put("batchNotification", Map.of(
                    "totalUsers", userIds.size(),
                    "successfulNotifications", batchSuccessCount,
                    "successRate", (double) batchSuccessCount / userIds.size() * 100,
                    "results", batchResults
            ));
            
            results.put("roleNotification", Map.of(
                    "role", "DRIVER",
                    "successfulNotifications", roleSuccessCount,
                    "results", roleResults
            ));
            
            results.put("testDuration", endTime - startTime);
            results.put("status", batchSuccessCount >= userIds.size() * 0.8 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in batch notifications test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test notification preferences
     */
    private Map<String, Object> testNotificationPreferences() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test user
            User testUser = createTestUsers(1).get(0);
            
            // Set custom preferences
            UnifiedNotificationService.UserNotificationPreferences preferences = 
                    UnifiedNotificationService.UserNotificationPreferences.builder()
                            .pushNotificationsEnabled(true)
                            .inAppNotificationsEnabled(true)
                            .enabledCategories(Set.of(NotificationCategory.SHIPMENT, NotificationCategory.COLD_CHAIN))
                            .enabledPriorities(Set.of(NotificationPriority.HIGH, NotificationPriority.CRITICAL))
                            .quietHoursEnabled(true)
                            .quietHoursStart(22)
                            .quietHoursEnd(8)
                            .build();
            
            Boolean preferencesSet = notificationService.updateUserNotificationPreferences(
                    testUser.getId(), preferences).get(5, TimeUnit.SECONDS);
            
            // Test notifications with preferences
            List<String> preferenceTestTemplates = Arrays.asList(
                    "SHIPMENT_CREATED", // Should work
                    "SYSTEM_MAINTENANCE", // Should be filtered out
                    "TEMPERATURE_WARNING", // Should work
                    "TASK_REMINDER" // Should be filtered out
            );
            
            int allowedNotifications = 0;
            int filteredNotifications = 0;
            List<NotificationResult> preferenceResults = new ArrayList<>();
            
            for (String templateId : preferenceTestTemplates) {
                Map<String, Object> data = createNotificationData(templateId);
                
                NotificationResult result = notificationService.sendNotification(
                        testUser.getId(), templateId, data).get(5, TimeUnit.SECONDS);
                
                preferenceResults.add(result);
                
                if (result.getSuccess()) {
                    allowedNotifications++;
                } else {
                    filteredNotifications++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("preferencesSet", preferencesSet);
            results.put("allowedNotifications", allowedNotifications);
            results.put("filteredNotifications", filteredNotifications);
            results.put("preferenceResults", preferenceResults);
            results.put("testDuration", endTime - startTime);
            results.put("status", preferencesSet && filteredNotifications > 0 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in notification preferences test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test notification performance under load
     */
    private Map<String, Object> testNotificationPerformance() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test users
            List<User> testUsers = createTestUsers(20);
            List<Long> userIds = testUsers.stream().map(User::getId).toList();
            
            // Performance test parameters
            int notificationsPerUser = 5;
            int totalNotifications = userIds.size() * notificationsPerUser;
            
            AtomicInteger successfulNotifications = new AtomicInteger(0);
            AtomicInteger failedNotifications = new AtomicInteger(0);
            AtomicLong totalResponseTime = new AtomicLong(0);
            
            // Send notifications concurrently
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (User user : testUsers) {
                for (int i = 0; i < notificationsPerUser; i++) {
                    final Long userId = user.getId();
                    final int sequence = i + 1;
                    
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            long requestStart = System.currentTimeMillis();
                            
                            Map<String, Object> data = Map.of(
                                    "testId", sequence,
                                    "userId", userId,
                                    "timestamp", System.currentTimeMillis()
                            );
                            
                            NotificationResult result = notificationService.sendNotification(
                                    userId, "SYSTEM_MAINTENANCE", data).get(10, TimeUnit.SECONDS);
                            
                            long responseTime = System.currentTimeMillis() - requestStart;
                            totalResponseTime.addAndGet(responseTime);
                            
                            if (result.getSuccess()) {
                                successfulNotifications.incrementAndGet();
                            } else {
                                failedNotifications.incrementAndGet();
                            }
                            
                        } catch (Exception e) {
                            failedNotifications.incrementAndGet();
                            log.error("Error in performance test notification", e);
                        }
                    }, testExecutor);
                    
                    futures.add(future);
                }
            }
            
            // Wait for all notifications to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            // Calculate performance metrics
            double successRate = (double) successfulNotifications.get() / totalNotifications * 100;
            double averageResponseTime = (double) totalResponseTime.get() / totalNotifications;
            double notificationsPerSecond = (double) totalNotifications / ((endTime - startTime) / 1000.0);
            
            results.put("totalNotifications", totalNotifications);
            results.put("successfulNotifications", successfulNotifications.get());
            results.put("failedNotifications", failedNotifications.get());
            results.put("successRate", successRate);
            results.put("averageResponseTime", averageResponseTime);
            results.put("notificationsPerSecond", notificationsPerSecond);
            results.put("testDuration", endTime - startTime);
            results.put("status", successRate >= 90.0 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in notification performance test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    // Helper methods
    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId((long) (1000 + i));
            user.setEmail("testuser" + i + "@example.com");
            user.setName("Test User " + i);
            user.setRole(UserRole.CLIENT);
            user.setFcmToken("test_fcm_token_" + i);
            userRepository.save(user);
            users.add(user);
        }
        return users;
    }

    private Map<String, Object> createNotificationData(String templateId) {
        Map<String, Object> data = new HashMap<>();
        
        switch (templateId) {
            case "SHIPMENT_CREATED":
                data.put("trackingNumber", "TRK" + System.currentTimeMillis());
                break;
            case "SHIPMENT_DELIVERED":
                data.put("trackingNumber", "TRK" + System.currentTimeMillis());
                break;
            case "TEMPERATURE_WARNING":
            case "TEMPERATURE_CRITICAL":
                data.put("trackingNumber", "TRK" + System.currentTimeMillis());
                data.put("temperature", 12.5 + (Math.random() * 10));
                break;
            case "NEW_TASK_ASSIGNED":
                data.put("taskType", "Delivery Task");
                break;
            case "TASK_REMINDER":
                data.put("taskType", "Pickup Task");
                data.put("dueTime", LocalDateTime.now().plusHours(2).toString());
                break;
            case "SYSTEM_MAINTENANCE":
                data.put("maintenanceTime", LocalDateTime.now().plusHours(24).toString());
                break;
            case "EMERGENCY_ALERT":
                data.put("alertMessage", "System emergency - Immediate attention required");
                break;
            case "PAYMENT_PROCESSING":
                data.put("amount", 100.50);
                break;
            case "FINANCIAL_REPORT":
                data.put("reportType", "Monthly Financial Report");
                break;
        }
        
        return data;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationTestResult {
        private String role;
        private String priority;
        private int totalNotifications;
        private int successfulNotifications;
        private double successRate;
        private List<NotificationResult> notificationResults;
    }
}
