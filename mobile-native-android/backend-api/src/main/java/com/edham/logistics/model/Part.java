package com.edham.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private Integer quantity;

    private Integer minQuantity;

    private String location;

    @Enumerated(EnumType.STRING)
    private PartStatus status;

    public enum PartStatus {
        AVAILABLE, LOW_STOCK, OUT_OF_STOCK
    }
}
