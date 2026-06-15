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

/**
 * Unified notification controller
 * Provides REST API for notifications with role-based access and priority management
 */
@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class NotificationController {

    private final UnifiedNotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationController(UnifiedNotificationService notificationService,
                              UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /**
     * Send notification to specific user
     */
    @PostMapping("/send/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<NotificationResult>>> sendNotification(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        
        String templateId = (String) request.get("templateId");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        
        return notificationService.sendNotification(userId, templateId, data)
                .thenApply(result -> {
                    if (result.getSuccess()) {
                        log.info("Notification sent successfully to user: {}, notification: {}", userId, result.getNotificationId());
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<NotificationResult>builder()
                                        .success(true)
                                        .data(result)
                                        .message("Notification sent successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Failed to send notification to user: {}, error: {}", userId, result.getError());
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<NotificationResult>builder()
                                        .success(false)
                                        .error(result.getError())
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error sending notification to user {}: {}", userId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<NotificationResult>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Send notification to multiple users
     */
    @PostMapping("/send/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<NotificationResult>>>> sendNotificationBatch(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) request.get("userIds");
        String templateId = (String) request.get("templateId");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        
        return notificationService.sendNotificationToUsers(userIds, templateId, data)
                .thenApply(results -> {
                    long successCount = results.stream().mapToLong(r -> r.getSuccess() ? 1L : 0L).sum();
                    log.info("Batch notification sent: {} successful out of {} total", successCount, userIds.size());
                    
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<NotificationResult>>builder()
                                    .success(true)
                                    .data(results)
                                    .message(String.format("Batch notification sent: %d/%d successful", successCount, userIds.size()))
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error sending batch notification: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<NotificationResult>>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Send notification to role
     */
    @PostMapping("/send/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<NotificationResult>>>> sendNotificationToRole(
            @PathVariable UserRole role,
            @RequestBody Map<String, Object> request) {
        
        String templateId = (String) request.get("templateId");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.get("data");
        
        return notificationService.sendNotificationToRole(role, templateId, data)
                .thenApply(results -> {
                    long successCount = results.stream().mapToLong(r -> r.getSuccess() ? 1L : 0L).sum();
                    log.info("Role notification sent to {}: {} successful out of {} total", role, successCount, results.size());
                    
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<NotificationResult>>builder()
                                    .success(true)
                                    .data(results)
                                    .message(String.format("Role notification sent: %d/%d successful", successCount, results.size()))
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error sending role notification to {}: {}", role, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<NotificationResult>>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get user notifications
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<UnifiedNotification>>>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) NotificationCategory category) {
        
        // Validate access - users can only see their own notifications
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.SUPERVISOR) && !currentUserId.equals(userId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<List<UnifiedNotification>>builder()
                                    .success(false)
                                    .error("Access denied: Cannot view other user's notifications")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return notificationService.getUserNotifications(userId, page, size, category)
                .thenApply(notifications -> {
                    log.debug("Retrieved {} notifications for user: {}", notifications.size(), userId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<UnifiedNotification>>builder()
                                    .success(true)
                                    .data(notifications)
                                    .message("Notifications retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting notifications for user {}: {}", userId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<UnifiedNotification>>builder()
                                    .success(false)
                                    .error("Failed to get notifications: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get current user notifications
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<UnifiedNotification>>>> getMyNotifications(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) NotificationCategory category) {
        
        Long currentUserId = getCurrentUserId();
        
        return notificationService.getUserNotifications(currentUserId, page, size, category)
                .thenApply(notifications -> {
                    log.debug("Retrieved {} notifications for current user: {}", notifications.size(), currentUserId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<UnifiedNotification>>builder()
                                    .success(true)
                                    .data(notifications)
                                    .message("Notifications retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting notifications for current user: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<UnifiedNotification>>builder()
                                    .success(false)
                                    .error("Failed to get notifications: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/read/{notificationId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> markNotificationAsRead(
            @PathVariable String notificationId) {
        
        return notificationService.markNotificationAsRead(notificationId)
                .thenApply(success -> {
                    if (success) {
                        log.debug("Notification marked as read: {}", notificationId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<String>builder()
                                        .success(true)
                                        .data("Notification marked as read")
                                        .message("Notification marked as read successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Failed to mark notification as read: {}", notificationId);
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<String>builder()
                                        .success(false)
                                        .error("Failed to mark notification as read")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error marking notification as read {}: {}", notificationId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Mark all notifications as read for user
     */
    @PostMapping("/read/all/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Integer>>> markAllNotificationsAsRead(
            @PathVariable Long userId) {
        
        // Validate access - users can only mark their own notifications
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.SUPERVISOR) && !currentUserId.equals(userId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(false)
                                    .error("Access denied: Cannot mark other user's notifications")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return notificationService.markAllNotificationsAsRead(userId)
                .thenApply(count -> {
                    log.debug("Marked {} notifications as read for user: {}", count, userId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(true)
                                    .data(count)
                                    .message(String.format("Marked %d notifications as read", count))
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error marking all notifications as read for user {}: {}", userId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(false)
                                    .error("Failed to mark notifications as read: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Mark all current user notifications as read
     */
    @PostMapping("/read/all/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Integer>>> markMyNotificationsAsRead() {
        
        Long currentUserId = getCurrentUserId();
        
        return notificationService.markAllNotificationsAsRead(currentUserId)
                .thenApply(count -> {
                    log.debug("Marked {} notifications as read for current user: {}", count, currentUserId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(true)
                                    .data(count)
                                    .message(String.format("Marked %d notifications as read", count))
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error marking all notifications as read for current user: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Integer>builder()
                                    .success(false)
                                    .error("Failed to mark notifications as read: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/statistics/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<NotificationStatistics>>> getNotificationStatistics(
            @PathVariable Long userId) {
        
        // Validate access - users can only see their own statistics
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.SUPERVISOR) && !currentUserId.equals(userId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<NotificationStatistics>builder()
                                    .success(false)
                                    .error("Access denied: Cannot view other user's statistics")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return notificationService.getNotificationStatistics(userId)
                .thenApply(statistics -> {
                    if (statistics != null) {
                        log.debug("Retrieved notification statistics for user: {}", userId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<NotificationStatistics>builder()
                                        .success(true)
                                        .data(statistics)
                                        .message("Statistics retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<NotificationStatistics>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No statistics available")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting notification statistics for user {}: {}", userId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<NotificationStatistics>builder()
                                    .success(false)
                                    .error("Failed to get statistics: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get current user notification statistics
     */
    @GetMapping("/statistics/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<NotificationStatistics>>> getMyNotificationStatistics() {
        
        Long currentUserId = getCurrentUserId();
        
        return notificationService.getNotificationStatistics(currentUserId)
                .thenApply(statistics -> {
                    if (statistics != null) {
                        log.debug("Retrieved notification statistics for current user: {}", currentUserId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<NotificationStatistics>builder()
                                        .success(true)
                                        .data(statistics)
                                        .message("Statistics retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<NotificationStatistics>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No statistics available")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting notification statistics for current user: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<NotificationStatistics>builder()
                                    .success(false)
                                    .error("Failed to get statistics: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Update user notification preferences
     */
    @PostMapping("/preferences/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> updateNotificationPreferences(
            @PathVariable Long userId,
            @RequestBody UserNotificationPreferences preferences) {
        
        // Validate access - users can only update their own preferences
        Long currentUserId = getCurrentUserId();
        UserRole currentUserRole = getCurrentUserRole();
        
        if (!currentUserRole.equals(UserRole.ADMIN) && !currentUserRole.equals(UserRole.SUPERVISOR) && !currentUserId.equals(userId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Access denied: Cannot update other user's preferences")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return notificationService.updateUserNotificationPreferences(userId, preferences)
                .thenApply(success -> {
                    if (success) {
                        log.info("Updated notification preferences for user: {}", userId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<String>builder()
                                        .success(true)
                                        .data("Preferences updated successfully")
                                        .message("Notification preferences updated successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Failed to update notification preferences for user: {}", userId);
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<String>builder()
                                        .success(false)
                                        .error("Failed to update preferences")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error updating notification preferences for user {}: {}", userId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Update current user notification preferences
     */
    @PostMapping("/preferences/my")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> updateMyNotificationPreferences(
            @RequestBody UserNotificationPreferences preferences) {
        
        Long currentUserId = getCurrentUserId();
        
        return notificationService.updateUserNotificationPreferences(currentUserId, preferences)
                .thenApply(success -> {
                    if (success) {
                        log.info("Updated notification preferences for current user: {}", currentUserId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<String>builder()
                                        .success(true)
                                        .data("Preferences updated successfully")
                                        .message("Notification preferences updated successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Failed to update notification preferences for current user: {}", currentUserId);
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<String>builder()
                                        .success(false)
                                        .error("Failed to update preferences")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error updating notification preferences for current user: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get notification templates
     */
    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> getNotificationTemplates() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> templates = new HashMap<>();
                
                // Shipment templates
                templates.put("SHIPMENT_CREATED", Map.of(
                        "title", "New Shipment Created",
                        "body", "Your shipment #{trackingNumber} has been created and is ready for pickup",
                        "priority", "MEDIUM",
                        "category", "SHIPMENT"
                ));
                
                templates.put("SHIPMENT_DELIVERED", Map.of(
                        "title", "Shipment Delivered",
                        "body", "Your shipment #{trackingNumber} has been delivered successfully",
                        "priority", "HIGH",
                        "category", "SHIPMENT"
                ));
                
                // Cold-chain templates
                templates.put("TEMPERATURE_WARNING", Map.of(
                        "title", "Temperature Warning",
                        "body", "Temperature alert for shipment #{trackingNumber}: #{temperature}°C",
                        "priority", "HIGH",
                        "category", "COLD_CHAIN"
                ));
                
                templates.put("TEMPERATURE_CRITICAL", Map.of(
                        "title", "Critical Temperature Alert",
                        "body", "Critical temperature for shipment #{trackingNumber}: #{temperature}°C - Immediate action required!",
                        "priority", "CRITICAL",
                        "category", "COLD_CHAIN"
                ));
                
                // Driver templates
                templates.put("NEW_TASK_ASSIGNED", Map.of(
                        "title", "New Task Assigned",
                        "body", "You have been assigned a new task: #{taskType}",
                        "priority", "HIGH",
                        "category", "DRIVER"
                ));
                
                // System templates
                templates.put("EMERGENCY_ALERT", Map.of(
                        "title", "Emergency Alert",
                        "body", "#{alertMessage}",
                        "priority", "CRITICAL",
                        "category", "EMERGENCY"
                ));
                
                log.debug("Retrieved {} notification templates", templates.size());
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(templates)
                                .message("Notification templates retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting notification templates: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Failed to get templates: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    /**
     * Get notification categories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<Map<String, Object>>>>> getNotificationCategories() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> categories = new ArrayList<>();
                
                for (NotificationCategory category : NotificationCategory.values()) {
                    Map<String, Object> categoryInfo = new HashMap<>();
                    categoryInfo.put("name", category.name());
                    categoryInfo.put("displayName", getDisplayName(category));
                    categoryInfo.put("description", getDescription(category));
                    categoryInfo.put("icon", getIcon(category));
                    categoryInfo.put("color", getColor(category));
                    categories.add(categoryInfo);
                }
                
                log.debug("Retrieved {} notification categories", categories.size());
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<List<Map<String, Object>>>builder()
                                .success(true)
                                .data(categories)
                                .message("Notification categories retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting notification categories: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<List<Map<String, Object>>>builder()
                                .success(false)
                                .error("Failed to get categories: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    /**
     * Get priority levels
     */
    @GetMapping("/priorities")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<Map<String, Object>>>>> getPriorityLevels() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> priorities = new ArrayList<>();
                
                for (NotificationPriority priority : NotificationPriority.values()) {
                    Map<String, Object> priorityInfo = new HashMap<>();
                    priorityInfo.put("name", priority.name());
                    priorityInfo.put("displayName", getPriorityDisplayName(priority));
                    priorityInfo.put("description", getPriorityDescription(priority));
                    priorityInfo.put("level", priority.ordinal());
                    priorityInfo.put("color", getPriorityColor(priority));
                    priorityInfo.put("sound", getPrioritySound(priority));
                    priorities.add(priorityInfo);
                }
                
                log.debug("Retrieved {} priority levels", priorities.size());
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<List<Map<String, Object>>>builder()
                                .success(true)
                                .data(priorities)
                                .message("Priority levels retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting priority levels: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<List<Map<String, Object>>>builder()
                                .success(false)
                                .error("Failed to get priorities: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    // Helper methods
    private Long getCurrentUserId() {
        // Implementation to get current user ID from security context
        return 1L; // Placeholder
    }

    private UserRole getCurrentUserRole() {
        // Implementation to get current user role from security context
        return UserRole.CLIENT; // Placeholder
    }

    private String getDisplayName(NotificationCategory category) {
        switch (category) {
            case SHIPMENT: return "Shipments";
            case COLD_CHAIN: return "Cold Chain";
            case DRIVER: return "Driver Tasks";
            case PAYMENT: return "Payments";
            case FINANCIAL: return "Financial";
            case SYSTEM: return "System";
            case EMERGENCY: return "Emergency";
            default: return category.name();
        }
    }

    private String getDescription(NotificationCategory category) {
        switch (category) {
            case SHIPMENT: return "Notifications related to shipment status updates";
            case COLD_CHAIN: return "Temperature monitoring and cold-chain alerts";
            case DRIVER: return "Driver task assignments and updates";
            case PAYMENT: return "Payment confirmations and financial updates";
            case FINANCIAL: return "Financial reports and accounting notifications";
            case SYSTEM: return "System maintenance and updates";
            case EMERGENCY: return "Emergency alerts and critical notifications";
            default: return "General notifications";
        }
    }

    private String getIcon(NotificationCategory category) {
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

    private String getColor(NotificationCategory category) {
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

    private String getPriorityDisplayName(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return "Critical";
            case HIGH: return "High";
            case MEDIUM: return "Medium";
            case LOW: return "Low";
            default: return priority.name();
        }
    }

    private String getPriorityDescription(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return "Immediate attention required - Critical alerts";
            case HIGH: return "Important notifications requiring attention";
            case MEDIUM: return "Standard notifications - Regular priority";
            case LOW: return "Informational notifications - Low priority";
            default: return "General notifications";
        }
    }

    private String getPriorityColor(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return "#EF4444";
            case HIGH: return "#F59E0B";
            case MEDIUM: return "#3B82F6";
            case LOW: return "#6B7280";
            default: return "#3B82F6";
        }
    }

    private String getPrioritySound(NotificationPriority priority) {
        switch (priority) {
            case CRITICAL: return "critical";
            case HIGH: return "alert";
            case MEDIUM: return "default";
            case LOW: return "silent";
            default: return "default";
        }
    }
}
