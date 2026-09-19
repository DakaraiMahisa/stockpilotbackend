package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(
        name = "product_variants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_variant_sku",
                        columnNames = {"tenant_id", "sku"}
                ),
                @UniqueConstraint(
                        name = "uk_product_variant_barcode",
                        columnNames = {"tenant_id", "barcode"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductVariant extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_variant_product"
            )
    )
    private Product product;

    @Column(nullable = false, length = 60)
    private String sku;

    @Column(length = 60)
    private String barcode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            nullable = false,
            columnDefinition = "jsonb"
    )
    private Map<String, String> attributes;

    @Column(
            name = "cost_price",
            nullable = false,
            precision = 14,
            scale = 4
    )
    @Builder.Default
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(
            name = "retail_price",
            nullable = false,
            precision = 14,
            scale = 4
    )
    @Builder.Default
    private BigDecimal retailPrice = BigDecimal.ZERO;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(
            name = "additional_weight_kg",
            nullable = false,
            precision = 8,
            scale = 3
    )
    @Builder.Default
    private BigDecimal additionalWeightKg = BigDecimal.ZERO;
}
