package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {
    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private List<String> targetRoles;
    private List<String> targetUsers;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private Boolean requiresAction;
}
