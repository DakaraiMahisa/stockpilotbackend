package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import com.stockpilot.backend.org.enums.CurrencyPosition;
import com.stockpilot.backend.org.enums.DimensionUnit;
import com.stockpilot.backend.org.enums.NumberFormat;
import com.stockpilot.backend.org.enums.TimeFormat;
import com.stockpilot.backend.org.enums.WeightUnit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(
        name = "business_configs",
        indexes = {
                @Index(
                        name = "idx_business_configs_tenant_id",
                        columnList = "tenant_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_business_configs_organization",
                        columnNames = "organization_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
public class BusinessConfig extends TenantAwareEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            unique = true
    )
    private Organization organization;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String timezone = "UTC";

    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currencyCode = "USD";

    @Builder.Default
    @Column(nullable = false, length = 5)
    private String currencySymbol = "$";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CurrencyPosition currencyPosition = CurrencyPosition.PREFIX;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String dateFormat = "dd/MM/yyyy";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TimeFormat timeFormat = TimeFormat.H12;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NumberFormat numberFormat = NumberFormat.DOT_COMMA;

    @Builder.Default
    @Column(nullable = false)
    private Integer decimalPlaces = 2;

    @Builder.Default
    @Column(nullable = false, length = 5)
    private String fiscalYearStart = "01-01";

    @Builder.Default
    @Column(nullable = false, length = 5)
    private String defaultLanguage = "en-US";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private WeightUnit weightUnit = WeightUnit.KG;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private DimensionUnit dimensionUnit = DimensionUnit.CM;
}