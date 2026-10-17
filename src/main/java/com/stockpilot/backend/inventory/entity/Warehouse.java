
package com.stockpilot.backend.inventory.entity;

import com.stockpilot.backend.inventory.enums.WarehouseStatus;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(
        name = "inventory_warehouses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_warehouse_tenant_branch_code",
                        columnNames = {"tenant_id", "branch_id", "code"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Warehouse extends TenantAwareEntity {

    @Column(name = "branch_id", nullable = false, updatable = false)
    private UUID branchId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean defaultWarehouse = false;

    @Builder.Default
    @Column(name = "bin_tracking_enabled", nullable = false)
    private boolean binTrackingEnabled = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WarehouseStatus status = WarehouseStatus.ACTIVE;

    public boolean isActive() {
        return status == WarehouseStatus.ACTIVE;
    }

    public void activate() {
        this.status = WarehouseStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = WarehouseStatus.INACTIVE;
    }

    public void archive() {
        this.status = WarehouseStatus.ARCHIVED;
    }

    public void markAsDefault() {
        this.defaultWarehouse = true;
    }

    public void removeAsDefault() {
        this.defaultWarehouse = false;
    }
}
