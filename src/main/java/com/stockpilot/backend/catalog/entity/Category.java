package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_tenant_code",
                        columnNames = {"tenant_id", "code"}
                ),
                @UniqueConstraint(
                        name = "uk_category_tenant_name_parent",
                        columnNames = {"tenant_id", "parent_id", "name"}
                )
        },
        indexes = {
                @Index(name = "idx_category_tenant", columnList = "tenant_id"),
                @Index(name = "idx_category_parent", columnList = "parent_id"),
                @Index(name = "idx_category_active", columnList = "is_active"),
                @Index(name = "idx_category_sort_order", columnList = "sort_order")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends TenantAwareEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id",
            foreignKey = @ForeignKey(name = "fk_category_parent")
    )
    private Category parent;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

}