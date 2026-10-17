package com.stockpilot.backend.inventory.entity;


import com.stockpilot.backend.inventory.enums.BinType;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(
        name = "inventory_bin_locations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bin_location_warehouse_code",
                        columnNames = {"warehouse_id", "code"}
                ),
                @UniqueConstraint(
                        name = "uk_bin_location_id_warehouse_tenant",
                        columnNames = {"id", "warehouse_id", "tenant_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_bin_locations_tenant_warehouse",
                        columnList = "tenant_id, warehouse_id"
                ),
                @Index(
                        name = "idx_bin_locations_parent",
                        columnList = "parent_bin_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BinLocation extends TenantAwareEntity {

    @Column(name = "warehouse_id", nullable = false, updatable = false)
    private UUID warehouseId;

    @Column(name = "parent_bin_id")
    private UUID parentBinId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "bin_type", nullable = false, length = 20)
    private BinType binType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
