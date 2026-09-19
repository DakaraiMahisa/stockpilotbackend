package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "price_list_items",
        indexes = {
                @Index(
                        name = "idx_price_list_item_price_list",
                        columnList = "price_list_id"
                ),
                @Index(
                        name = "idx_price_list_item_product",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_price_list_item_variant",
                        columnList = "variant_id"
                ),
                @Index(
                        name = "idx_price_list_item_product_quantity",
                        columnList = "product_id, min_quantity"
                ),
                @Index(
                        name = "idx_price_list_item_variant_quantity",
                        columnList = "variant_id, min_quantity"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceListItem extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "price_list_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_price_list_item_price_list"
            )
    )
    private PriceList priceList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            foreignKey = @ForeignKey(
                    name = "fk_price_list_item_product"
            )
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "variant_id",
            foreignKey = @ForeignKey(
                    name = "fk_price_list_item_variant"
            )
    )
    private ProductVariant variant;

    @Column(
            name = "min_quantity",
            nullable = false,
            precision = 12,
            scale = 3
    )
    @Builder.Default
    private BigDecimal minQuantity = BigDecimal.ONE;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 14,
            scale = 4
    )
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(
            name = "discount_pct",
            precision = 5,
            scale = 2
    )
    @Builder.Default
    private BigDecimal discountPct = BigDecimal.ZERO;
}