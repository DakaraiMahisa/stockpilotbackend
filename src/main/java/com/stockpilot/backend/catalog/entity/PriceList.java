package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.catalog.enums.PriceListType;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "price_lists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_price_list_tenant_code",
                        columnNames = {"tenant_id", "code"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_price_list_tenant",
                        columnList = "tenant_id"
                ),
                @Index(
                        name = "idx_price_list_tenant_active",
                        columnList = "tenant_id, active"
                ),
                @Index(
                        name = "idx_price_list_tenant_type",
                        columnList = "tenant_id, price_list_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PriceList extends TenantAwareEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "price_list_type",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private PriceListType priceListType = PriceListType.RETAIL;

    /**
     * Always sourced from the tenant's BusinessConfig.
     * The client must not arbitrarily change this value.
     */
    @JdbcTypeCode(Types.CHAR)
    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean defaultList = false;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(
            mappedBy = "priceList",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PriceListItem> items = new ArrayList<>();
}
