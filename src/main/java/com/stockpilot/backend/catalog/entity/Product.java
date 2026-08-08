package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.catalog.enums.UnitOfMeasure;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_sku",
                        columnNames = {"tenant_id", "sku"}
                ),
                @UniqueConstraint(
                        name = "uk_product_barcode",
                        columnNames = {"tenant_id", "barcode"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class Product extends TenantAwareEntity {

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(length = 60)
    private String barcode;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_class_id", nullable = false)
    private TaxClass taxClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UnitOfMeasure unitOfMeasure = UnitOfMeasure.PCS;

    @Column(nullable = false)
    @Builder.Default
    private boolean trackBatches = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean hasVariants = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer minStockLevel = 0;

    private Integer maxStockLevel;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean service = false;

    @Column(precision = 8, scale = 3)
    private BigDecimal weightKg;

    @Column(columnDefinition = "TEXT")
    private String notes;
}