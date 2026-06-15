package com.edham.logistics.notifications;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Unified notification service
 * Provides push notifications, in-app notifications, role-based alerts, and priority management
 */
@Slf4j
@Service
public class UnifiedNotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ShipmentRepository shipmentRepository;
    private final MongoTemplate mongoTemplate;
    private final FirebaseMessaging firebaseMessaging;

    // Notification templates
    private final Map<String, NotificationTemplate> notificationTemplates = new ConcurrentHashMap<>();
    
    // User notification preferences
    private final Map<Long, UserNotificationPreferences> userPreferences = new ConcurrentHashMap<>();
    
    // Role-based notification rules
    private final Map<UserRole, List<NotificationRule>> roleRules = new ConcurrentHashMap<>();
    
    // Active notification sessions
    private final Map<String, NotificationSession> activeSessions = new ConcurrentHashMap<>();
    
    // Scheduled executor for notification processing
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    
    // Performance settings
    private static final int NOTIFICATION_BATCH_SIZE = 100;
    private static final int NOTIFICATION_RETRY_ATTEMPTS = 3;
    private static final int NOTIFICATION_HISTORY_RETENTION_DAYS = 30;
    private static final int MAX_NOTIFICATIONS_PER_USER = 1000;

    @Autowired
    public UnifiedNotificationService(UserRepository userRepository,
                                   NotificationRepository notificationRepository,
                                   ShipmentRepository shipmentRepository,
                                   MongoTemplate mongoTemplate,
                                   FirebaseMessaging firebaseMessaging) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.shipmentRepository = shipmentRepository;
        this.mongoTemplate = mongoTemplate;
        this.firebaseMessaging = firebaseMessaging;
        
        initializeNotificationSystem();
    }

    /**
     * Initialize notification system
     */
    private void initializeNotificationSystem() {
        log.info("Initializing unified notification system");
        
        // Initialize notification templates
        initializeNotificationTemplates();
        
        // Initialize role-based rules
        initializeRoleBasedRules();
        
        // Start notification processing
        startNotificationProcessing();
        
        // Start cleanup of old notifications
        startNotificationCleanup();
        
        // Start performance monitoring
        startPerformanceMonitoring();
        
        log.info("Unified notification system initialized successfully");
    }

    /**
     * Initialize notification templates
     */
    private void initializeNotificationTemplates() {
        // Shipment notifications
        notificationTemplates.put("SHIPMENT_CREATED", NotificationTemplate.builder()
                .templateId("SHIPMENT_CREATED")
                .title("New Shipment Created")
                .body("Your shipment #{trackingNumber} has been created and is ready for pickup")
                .priority(NotificationPriority.MEDIUM)
                .category(NotificationCategory.SHIPMENT)
                .icon("shipment-created")
                .sound("default")
                .build());
        
        notificationTemplates.put("SHIPMENT_PICKED_UP", NotificationTemplate.builder()
                .templateId("SHIPMENT_PICKED_UP")
                .title("Shipment Picked Up")
                .body("Your shipment #{trackingNumber} has been picked up by the driver")
                .priority(NotificationPriority.MEDIUM)
                .category(NotificationCategory.SHIPMENT)
                .icon("shipment-picked-up")
                .sound("default")
                .build());
        
        notificationTemplates.put("SHIPMENT_IN_TRANSIT", NotificationTemplate.builder()
                .templateId("SHIPMENT_IN_TRANSIT")
                .title("Shipment In Transit")
                .body("Your shipment #{trackingNumber} is now in transit")
                .priority(NotificationPriority.LOW)
                .category(NotificationCategory.SHIPMENT)
                .icon("shipment-transit")
                .sound("default")
                .build());
        
        notificationTemplates.put("SHIPMENT_DELIVERED", NotificationTemplate.builder()
                .templateId("SHIPMENT_DELIVERED")
                .title("Shipment Delivered")
                .body("Your shipment #{trackingNumber} has been delivered successfully")
                .priority(NotificationPriority.HIGH)
                .category(NotificationCategory.SHIPMENT)
                .icon("shipment-delivered")
                .sound("success")
                .build());
        
        // Cold-chain notifications
        notificationTemplates.put("TEMPERATURE_WARNING", NotificationTemplate.builder()
                .templateId("TEMPERATURE_WARNING")
                .title("Temperature Warning")
                .body("Temperature alert for shipment #{trackingNumber}: #{temperature}°C")
                .priority(NotificationPriority.HIGH)
                .category(NotificationCategory.COLD_CHAIN)
                .icon("temperature-warning")
                .sound("alert")
                .build());
        
        notificationTemplates.put("TEMPERATURE_CRITICAL", NotificationTemplate.builder()
                .templateId("TEMPERATURE_CRITICAL")
                .title("Critical Temperature Alert")
                .body("Critical temperature for shipment #{trackingNumber}: #{temperature}°C - Immediate action required!")
                .priority(NotificationPriority.CRITICAL)
                .category(NotificationCategory.COLD_CHAIN)
                .icon("temperature-critical")
                .sound("critical")
                .build());
        
        // Driver notifications
        notificationTemplates.put("NEW_TASK_ASSIGNED", NotificationTemplate.builder()
                .templateId("NEW_TASK_ASSIGNED")
                .title("New Task Assigned")
                .body("You have been assigned a new task: #{taskType}")
                .priority(NotificationPriority.HIGH)
                .category(NotificationCategory.DRIVER)
                .icon("new-task")
                .sound("alert")
                .build());
        
        notificationTemplates.put("TASK_REMINDER", NotificationTemplate.builder()
                .templateId("TASK_REMINDER")
                .title("Task Reminder")
                .body("Reminder: Task #{taskType} is due at #{dueTime}")
                .priority(NotificationPriority.MEDIUM)
                .category(NotificationCategory.DRIVER)
                .icon("task-reminder")
                .sound("reminder")
                .build());
        
        // System notifications
        notificationTemplates.put("SYSTEM_MAINTENANCE", NotificationTemplate.builder()
                .templateId("SYSTEM_MAINTENANCE")
                .title("System Maintenance")
                .body("System maintenance scheduled for #{maintenanceTime}")
                .priority(NotificationPriority.MEDIUM)
                .category(NotificationCategory.SYSTEM)
                .icon("system-maintenance")
                .sound("default")
                .build());
        
        notificationTemplates.put("EMERGENCY_ALERT", NotificationTemplate.builder()
                .templateId("EMERGENCY_ALERT")
                .title("Emergency Alert")
                .body("#{alertMessage}")
                .priority(NotificationPriority.CRITICAL)
                .category(NotificationCategory.EMERGENCY)
                .icon("emergency")
                .sound("emergency")
                .build());
        
        log.info("Notification templates initialized: {}", notificationTemplates.size());
    }

    /**
     * Initialize role-based notification rules
     */
    private void initializeRoleBasedRules() {
        // Client rules
        List<NotificationRule> clientRules = Arrays.asList(
                NotificationRule.builder()
                        .userRole(UserRole.CLIENT)
                        .allowedCategories(Arrays.asList(
                                NotificationCategory.SHIPMENT,
                                NotificationCategory.COLD_CHAIN,
                                NotificationCategory.PAYMENT,
                                NotificationCategory.SYSTEM))
                        .priorityThreshold(NotificationPriority.LOW)
                        .maxNotificationsPerHour(20)
                        .quietHoursStart(22)
                        .quietHoursEnd(8)
                        .build()
        );
        
        // Driver rules
        List<NotificationRule> driverRules = Arrays.asList(
                NotificationRule.builder()
                        .userRole(UserRole.DRIVER)
                        .allowedCategories(Arrays.asList(
                                NotificationCategory.DRIVER,
                                NotificationCategory.SHIPMENT,
                                NotificationCategory.COLD_CHAIN,
                                NotificationCategory.EMERGENCY))
                        .priorityThreshold(NotificationPriority.MEDIUM)
                        .maxNotificationsPerHour(50)
                        .quietHoursStart(23)
                        .quietHoursEnd(6)
                        .build()
        );
        
        // Admin rules
        List<NotificationRule> adminRules = Arrays.asList(
                NotificationRule.builder()
                        .userRole(UserRole.ADMIN)
                        .allowedCategories(Arrays.asList(NotificationCategory.values()))
                        .priorityThreshold(NotificationPriority.LOW)
                        .maxNotificationsPerHour(100)
                        .quietHoursStart(0)
                        .quietHoursEnd(0)
                        .build()
        );
        
        // Supervisor rules
        List<NotificationRule> supervisorRules = Arrays.asList(
                NotificationRule.builder()
                        .userRole(UserRole.SUPERVISOR)
                        .allowedCategories(Arrays.asList(
                                NotificationCategory.SHIPMENT,
                                NotificationCategory.DRIVER,
                                NotificationCategory.COLD_CHAIN,
                                NotificationCategory.EMERGENCY,
                                NotificationCategory.SYSTEM))
                        .priorityThreshold(NotificationPriority.LOW)
                        .maxNotificationsPerHour(75)
                        .quietHoursStart(0)
                        .quietHoursEnd(0)
                        .build()
        );
        
        // Accountant rules
        List<NotificationRule> accountantRules = Arrays.asList(
                NotificationRule.builder()
                        .userRole(UserRole.ACCOUNTANT)
                        .allowedCategories(Arrays.asList(
                                NotificationCategory.PAYMENT,
                                NotificationCategory.FINANCIAL,
                                NotificationCategory.SYSTEM))
                        .priorityThreshold(NotificationPriority.MEDIUM)
                        .maxNotificationsPerHour(30)
                        .quietHoursStart(22)
                        .quietHoursEnd(8)
                        .build()
        );
        
        roleRules.put(UserRole.CLIENT, clientRules);
        roleRules.put(UserRole.DRIVER, driverRules);
        roleRules.put(UserRole.ADMIN, adminRules);
        roleRules.put(UserRole.SUPERVISOR, supervisorRules);
        roleRules.put(UserRole.ACCOUNTANT, accountantRules);
        
        log.info("Role-based notification rules initialized: {}", roleRules.size());
    }

    /**
     * Send notification to specific user
     */
    public CompletableFuture<NotificationResult> sendNotification(Long userId, String templateId, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Sending notification to user: {}, template: {}", userId, templateId);
                
                // Get user
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                    return NotificationResult.builder()
                            .success(false)
                            .error("User not found: " + userId)
                            .build();
                }
                
                User user = userOpt.get();
                
                // Get notification template
                NotificationTemplate template = notificationTemplates.get(templateId);
                if (template == null) {
                    return NotificationResult.builder()
                            .success(false)
                            .error("Template not found: " + templateId)
                            .build();
                }
                
                // Check role-based rules
                if (!isNotificationAllowed(user, template)) {
                    return NotificationResult.builder()
                            .success(false)
                            .error("Notification not allowed for user role: " + user.getRole())
                            .build();
                }
                
                // Process template variables
                String title = processTemplate(template.getTitle(), data);
                String body = processTemplate(template.getBody(), data);
                
                // Create notification
                UnifiedNotification notification = UnifiedNotification.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .title(title)
                        .body(body)
                        .priority(template.getPriority())
                        .category(template.getCategory())
                        .templateId(templateId)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .read(false)
                        .delivered(false)
                        .build();
                
                // Save notification
                notificationRepository.save(notification);
                
                // Send push notification
                CompletableFuture<PushNotificationResult> pushResult = sendPushNotification(user, notification);
                
                // Update notification with push result
                notification.setDelivered(pushResult.get().isSuccess());
                notificationRepository.save(notification);
                
                // Update user notification count
                updateUserNotificationCount(userId);
                
                log.info("Notification sent successfully to user: {}, notification: {}", userId, notification.getId());
                
                return NotificationResult.builder()
                        .success(true)
                        .notificationId(notification.getId())
                        .pushResult(pushResult.get())
                        .build();
                
            } catch (Exception e) {
                log.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
                return NotificationResult.builder()
                        .success(false)
                        .error("Failed to send notification: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * Send notification to multiple users
     */
    public CompletableFuture<List<NotificationResult>> sendNotificationToUsers(List<Long> userIds, String templateId, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            List<NotificationResult> results = new ArrayList<>();
            
            // Process in batches
            for (int i = 0; i < userIds.size(); i += NOTIFICATION_BATCH_SIZE) {
                int endIndex = Math.min(i + NOTIFICATION_BATCH_SIZE, userIds.size());
                List<Long> batch = userIds.subList(i, endIndex);
                
                List<CompletableFuture<NotificationResult>> futures = batch.stream()
                        .map(userId -> sendNotification(userId, templateId, data))
                        .collect(Collectors.toList());
                
                // Wait for batch to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                
                // Collect results
                results.addAll(futures.stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (Exception e) {
                                log.error("Error getting notification result", e);
                                return NotificationResult.builder()
                                        .success(false)
                                        .error("Batch processing error: " + e.getMessage())
                                        .build();
                            }
                        })
                        .collect(Collectors.toList()));
                
                // Small delay between batches
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            return results;
        });
    }

    /**
     * Send notification to role
     */
    public CompletableFuture<List<NotificationResult>> sendNotificationToRole(UserRole role, String templateId, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get all users with specified role
                List<User> users = userRepository.findByRole(role);
                List<Long> userIds = users.stream()
                        .map(User::getId)
                        .collect(Collectors.toList());
                
                log.info("Sending notification to role: {}, user count: {}", role, userIds.size());
                
                return sendNotificationToUsers(userIds, templateId, data).get();
                
            } catch (Exception e) {
                log.error("Error sending notification to role {}: {}", role, e.getMessage(), e);
                return Arrays.asList(NotificationResult.builder()
                        .success(false)
                        .error("Failed to send to role: " + e.getMessage())
                        .build());
            }
        });
    }

    /**
     * Send push notification via Firebase
     */
    private CompletableFuture<PushNotificationResult> sendPushNotification(User user, UnifiedNotification notification) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get user's FCM token
                String fcmToken = user.getFcmToken();
                if (fcmToken == null || fcmToken.isEmpty()) {
                    return PushNotificationResult.builder()
                            .success(false)
                            .error("No FCM token for user: " + user.getId())
                            .build();
                }
                
                // Create message
                Message message = Message.builder()
                        .setToken(fcmToken)
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .build())
                        .putAllData(convertToFirebaseData(notification))
                        .setAndroidConfig(AndroidConfig.builder()
                                .setNotification(AndroidNotification.builder()
                                        .setSound(getSoundForPriority(notification.getPriority()))
                                        .setColor(getColorForCategory(notification.getCategory()))
                                        .setIcon(getIconForCategory(notification.getCategory()))
                                        .setPriority(getAndroidPriority(notification.getPriority()))
                                        .build())
                                .build())
                        .setApnsConfig(ApnsConfig.builder()
                                .setAps(Aps.builder()
                                        .setSound(getSoundForPriority(notification.getPriority()))
                                        .setCategory(notification.getCategory().name())
                                        .build())
                                .build())
                        .build();
                
                // Send message
                String messageId = firebaseMessaging.send(message);
                
                log.debug("Push notification sent: {}", messageId);
                
                return PushNotificationResult.builder()
                        .success(true)
                        .messageId(messageId)
                        .build();
                
            } catch (FirebaseMessagingException e) {
                log.error("Firebase messaging error: {}", e.getMessage(), e);
                return PushNotificationResult.builder()
                        .success(false)
                        .error("Firebase error: " + e.getMessage())
                        .errorCode(e.getMessagingErrorCode().name())
                        .build();
            } catch (Exception e) {
                log.error("Error sending push notification: {}", e.getMessage(), e);
                return PushNotificationResult.builder()
                        .success(false)
                        .error("General error: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * Get user notifications
     */
    public CompletableFuture<List<UnifiedNotification>> getUserNotifications(Long userId, 
                                                                          Integer page, 
                                                                          Integer size, 
                                                                          NotificationCategory category) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Query query = new Query(Criteria.where("userId").is(userId));
                
                if (category != null) {
                    query.addCriteria(Criteria.where("category").is(category));
                }
                
                // Sort by timestamp descending
                query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));
                
                // Pagination
                if (page != null && size != null) {
                    query.skip((long) page * size);
                    query.limit(size);
                } else {
                    query.limit(50); // Default limit
                }
                
                List<UnifiedNotification> notifications = mongoTemplate.find(query, UnifiedNotification.class);
                
                log.debug("Retrieved {} notifications for user: {}", notifications.size(), userId);
                
                return notifications;
                
            } catch (Exception e) {
                log.error("Error getting user notifications: {}", e.getMessage(), e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Mark notification as read
     */
    public CompletableFuture<Boolean> markNotificationAsRead(String notificationId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Query query = new Query(Criteria.where("id").is(notificationId));
                Update update = Update.update("read", true).set("readAt", LocalDateTime.now());
                
                var result = mongoTemplate.updateFirst(query, update, "notifications");
                
                boolean success = result.getModifiedCount() > 0;
                log.debug("Marked notification as read: {}, success: {}", notificationId, success);
                
                return success;
                
            } catch (Exception e) {
                log.error("Error marking notification as read: {}", e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * Mark all notifications as read for user
     */
    public CompletableFuture<Integer> markAllNotificationsAsRead(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Query query = new Query(Criteria.where("userId").is(userId).and("read").is(false));
                Update update = Update.update("read", true).set("readAt", LocalDateTime.now());
                
                var result = mongoTemplate.updateMulti(query, update, "notifications");
                
                int count = (int) result.getModifiedCount();
                log.debug("Marked {} notifications as read for user: {}", count, userId);
                
                return count;
                
            } catch (Exception e) {
                log.error("Error marking all notifications as read: {}", e.getMessage(), e);
                return 0;
            }
        });
    }

    /**
     * Get notification statistics
     */
    public CompletableFuture<NotificationStatistics> getNotificationStatistics(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get total notifications
                long totalNotifications = notificationRepository.countByUserId(userId);
                
                // Get unread notifications
                long unreadNotifications = notificationRepository.countByUserIdAndRead(userId, false);
                
                // Get notifications by category
                Map<NotificationCategory, Long> categoryStats = new HashMap<>();
                for (NotificationCategory category : NotificationCategory.values()) {
                    long count = notificationRepository.countByUserIdAndCategory(userId, category);
                    categoryStats.put(category, count);
                }
                
                // Get notifications by priority
                Map<NotificationPriority, Long> priorityStats = new HashMap<>();
                for (NotificationPriority priority : NotificationPriority.values()) {
                    long count = notificationRepository.countByUserIdAndPriority(userId, priority);
                    priorityStats.put(priority, count);
                }
                
                // Get recent notifications (last 7 days)
                LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
                long recentNotifications = notificationRepository.countByUserIdAndTimestampAfter(userId, sevenDaysAgo);
                
                return NotificationStatistics.builder()
                        .userId(userId)
                        .totalNotifications(totalNotifications)
                        .unreadNotifications(unreadNotifications)
                        .categoryStats(categoryStats)
                        .priorityStats(priorityStats)
                        .recentNotifications(recentNotifications)
                        .lastUpdated(LocalDateTime.now())
                        .build();
                
            } catch (Exception e) {
                log.error("Error getting notification statistics: {}", e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * Update user notification preferences
     */
    public CompletableFuture<Boolean> updateUserNotificationPreferences(Long userId, UserNotificationPreferences preferences) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userPreferences.put(userId, preferences);
                log.info("Updated notification preferences for user: {}", userId);
                return true;
            } catch (Exception e) {
                log.error("Error updating notification preferences: {}", e.getMessage(), e);
                return false;
            }
        });
    }

    // Helper methods
    private void startNotificationProcessing() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processPendingNotifications();
            } catch (Exception e) {
                log.error("Error processing pending notifications", e);
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    private void startNotificationCleanup() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupOldNotifications();
            } catch (Exception e) {
                log.error("Error cleaning up old notifications", e);
            }
        }, 1, 24, TimeUnit.HOURS);
    }

    private void startPerformanceMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                monitorNotificationPerformance();
            } catch (Exception e) {
                log.error("Error monitoring notification performance", e);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    private void processPendingNotifications() {
        // Implementation for processing queued notifications
    }

    private void cleanupOldNotifications() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(NOTIFICATION_HISTORY_RETENTION_DAYS);
            long deletedCount = notificationRepository.deleteByTimestampBefore(cutoffDate);
            log.info("Cleaned up old notifications: {}", deletedCount);
        } catch (Exception e) {
            log.error("Error cleaning up old notifications", e);
        }
    }

    private void monitorNotificationPerformance() {
        try {
            long totalNotifications = notificationRepository.count();
            long undeliveredNotifications = notificationRepository.countByDelivered(false);
            
            log.info("Notification performance - Total: {}, Undelivered: {}, Delivery rate: {:.2f}%", 
                    totalNotifications, undeliveredNotifications, 
                    totalNotifications > 0 ? (1.0 - (double) undeliveredNotifications / totalNotifications) * 100 : 0);
        } catch (Exception e) {
            log.error("Error monitoring notification performance", e);
        }
    }

    private boolean isNotificationAllowed(User user, NotificationTemplate template) {
        List<NotificationRule> rules = roleRules.get(user.getRole());
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        
        NotificationRule rule = rules.get(0); // Get first rule for the role
        
        // Check category
        if (!rule.getAllowedCategories().contains(template.getCategory())) {
            return false;
        }
        
        // Check priority threshold
        if (template.getPriority().ordinal() < rule.getPriorityThreshold().ordinal()) {
            return false;
        }
        
        // Check quiet hours
        if (isQuietHours(rule)) {
            return template.getPriority() == NotificationPriority.CRITICAL;
        }
        
        return true;
    }

    private boolean isQuietHours(NotificationRule rule) {
        if (rule.getQuietHoursStart() == rule.getQuietHoursEnd()) {
            return false; // No quiet hours
        }
        
        int currentHour = LocalDateTime.now().getHour();
        int start = rule.getQuietHoursStart();
        int end = rule.getQuietHoursEnd();
        
        if (start < end) {
            return currentHour >= start && currentHour < end;
        } else {
            // Overnight quiet hours (e.g., 22:00 to 08:00)
            return currentHour >= start || currentHour < end;
        }
    }

    private String processTemplate(String template, Map<String, Object> data) {
        String result = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("#{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private Map<String, String> convertToFirebaseData(UnifiedNotification notification) {
        Map<String, String> data = new HashMap<>();
        data.put("notificationId", notification.getId());
        data.put("category", notification.getCategory().name());
        data.put("priority", notification.getPriority().name());
        data.put("timestamp", notification.getTimestamp().toString());
        
        if (notification.getData() != null) {
            for (Map.Entry<String, Object> entry : notification.getData().entrySet()) {
                data.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        
        return data;
    }

    private void updateUserNotificationCount(Long userId) {
        try {
            Query query = new Query(Criteria.where("userId").is(userId));
            Update update = Update.inc("notificationCount", 1);
            mongoTemplate.updateFirst(query, update, "users");
        } catch (Exception e) {
            log.error("Error updating user notification count", e);
        }
    }

    private String getSoundForPriority(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return "critical";
            case HIGH: return "alert";
            case MEDIUM: return "default";
            case LOW: return "silent";
            default: return "default";
        }
    }

    private String getColorForCategory(NotificationCategory category) {
        switch (category) {
            case SHIPMENT: return "#3B82F6";
            case COLD_CHAIN: return "#10B981";
            case DRIVER: return "#F59E0B";
            case PAYMENT: return "#8B5CF6";
            case FINANCIAL: return "#6366F1";
            case SYSTEM: return "#6B7280";
            case EMERGENCY: return "#EF4444";
            default: return "#3B82F6";
        }
    }

    private String getIconForCategory(NotificationCategory category) {
        switch (category) {
            case SHIPMENT: return "shipment";
            case COLD_CHAIN: return "temperature";
            case DRIVER: return "driver";
            case PAYMENT: return "payment";
            case FINANCIAL: return "financial";
            case SYSTEM: return "system";
            case EMERGENCY: return "emergency";
            default: return "notification";
        }
    }

    private AndroidConfig.Priority getAndroidPriority(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return AndroidConfig.Priority.HIGH;
            case HIGH: return AndroidConfig.Priority.HIGH;
            case MEDIUM: return AndroidConfig.Priority.DEFAULT;
            case LOW: return AndroidConfig.Priority.LOW;
            default: return AndroidConfig.Priority.DEFAULT;
        }
    }

    // Data classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationResult {
        private Boolean success;
        private String error;
        private String notificationId;
        private PushNotificationResult pushResult;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PushNotificationResult {
        private Boolean success;
        private String messageId;
        private String error;
        private String errorCode;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationStatistics {
        private Long userId;
        private Long totalNotifications;
        private Long unreadNotifications;
        private Map<NotificationCategory, Long> categoryStats;
        private Map<NotificationPriority, Long> priorityStats;
        private Long recentNotifications;
        private LocalDateTime lastUpdated;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationTemplate {
        private String templateId;
        private String title;
        private String body;
        private NotificationPriority priority;
        private NotificationCategory category;
        private String icon;
        private String sound;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationRule {
        private UserRole userRole;
        private List<NotificationCategory> allowedCategories;
        private NotificationPriority priorityThreshold;
        private Integer maxNotificationsPerHour;
        private Integer quietHoursStart;
        private Integer quietHoursEnd;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserNotificationPreferences {
        private Boolean pushNotificationsEnabled;
        private Boolean inAppNotificationsEnabled;
        private Set<NotificationCategory> enabledCategories;
        private Set<NotificationPriority> enabledPriorities;
        private Boolean quietHoursEnabled;
        private Integer quietHoursStart;
        private Integer quietHoursEnd;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificationSession {
        private String sessionId;
        private Long userId;
        private LocalDateTime startTime;
        private LocalDateTime lastActivity;
        private Integer notificationCount;
        private Boolean isActive;
    }

    // Enums
    public enum NotificationPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum NotificationCategory {
        SHIPMENT, COLD_CHAIN, DRIVER, PAYMENT, FINANCIAL, SYSTEM, EMERGENCY
    }
}
