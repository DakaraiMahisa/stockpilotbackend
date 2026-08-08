package com.stockpilot.backend.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sku_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuSequence {

    @Id
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(
            name = "next_value",
            nullable = false
    )
    @Builder.Default
    private Long nextValue = 1L;
}