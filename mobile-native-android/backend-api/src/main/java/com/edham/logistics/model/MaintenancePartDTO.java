package com.edham.logistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Maintenance part DTO for tracking parts used in service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePartDTO {
    private String partNumber;
    private String partName;
    private String manufacturer;
    private String category;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String supplier;
    private LocalDateTime purchaseDate;
    private String warrantyInfo;
    private Boolean oemPart;
    private String condition;
    private String serialNumber;
    private LocalDateTime installationDate;
    private String notes;
}
