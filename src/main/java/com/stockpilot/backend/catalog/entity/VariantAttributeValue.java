package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "variant_attribute_values",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_variant_attribute_value_code",
                        columnNames = {"attribute_id", "code"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VariantAttributeValue extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "attribute_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_variant_attribute_value_attribute"
            )
    )
    private VariantAttribute attribute;

    @Column(nullable = false, length = 50)
    private String value;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}