package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "variant_attributes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_variant_attribute_code",
                        columnNames = {"tenant_id", "code"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VariantAttribute extends TenantAwareEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(
            mappedBy = "attribute",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<VariantAttributeValue> values = new ArrayList<>();
}