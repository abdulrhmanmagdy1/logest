package com.edham.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_locations")
public class DriverLocation {
    @Id
    private Long driverId;

    private Double latitude;
    private Double longitude;
    private Double heading;
    private Double speed;
    private String address;
    private LocalDateTime updatedAt;
}
