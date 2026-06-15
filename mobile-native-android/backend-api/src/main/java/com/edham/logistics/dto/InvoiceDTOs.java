package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    private Long customerId;
    private Double amount;
    private Double tax;
    private Double totalAmount;
    private LocalDateTime dueDate;
    private String description;
    private List<Long> shipmentIds;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private Double amount;
    private Double tax;
    private Double totalAmount;
    private LocalDateTime dueDate;
    private String description;
    private String status;
    private List<Long> shipmentIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceUpdateRequest {
    private Double amount;
    private Double tax;
    private Double totalAmount;
    private LocalDateTime dueDate;
    private String description;
    private List<Long> shipmentIds;
    private String status;
}
