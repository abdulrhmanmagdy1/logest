package com.edham.logistics.service;

import com.edham.logistics.model.ActivityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for logging user activities and audit events
 */
@Slf4j
@Service
public class ActivityLogService {

    /**
     * Log a user activity
     */
    public void logActivity(Long userId, ActivityType type, String category, 
                           String entityId, String description, Map<String, Object> metadata) {
        log.info("Activity logged - User: {}, Type: {}, Category: {}, Entity: {}, Description: {}",
                userId, type, category, entityId, description);
        // In a full implementation, this would persist to an activity log database table
        // For now, we log to the application log
    }
}
