package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.org.enums.RateType;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tax_rates")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
public class TaxRate extends TenantAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_class_id", nullable = false)
    private TaxClass taxClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false, length = 10)
    private RateType rateType;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal rate;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    public boolean isVatRate() {
        return rateType == RateType.VAT;
    }

    public boolean isCgstRate() {
        return rateType == RateType.CGST;
    }

    public boolean isSgstRate() {
        return rateType == RateType.SGST;
    }

    public boolean isIgstRate() {
        return rateType == RateType.IGST;
    }

    public boolean isEffectiveOn(LocalDate date) {
        return !effectiveFrom.isAfter(date)
                && (effectiveTo == null || !effectiveTo.isBefore(date));
    }

    public boolean isCurrent() {
        return isEffectiveOn(LocalDate.now());
    }

    public boolean isOpenEnded() {
        return effectiveTo == null;
    }

    public void close(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
}