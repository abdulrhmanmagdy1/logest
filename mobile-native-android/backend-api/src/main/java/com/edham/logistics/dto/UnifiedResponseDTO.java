package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedResponseDTO<T> {
    private Boolean success;
    private T data;
    private String message;
    private String error;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    private String requestId;
    private String version;
    private Map<String, Object> metadata;
}
