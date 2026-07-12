package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.org.enums.TaxType;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
        name = "tax_classes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tax_class_tenant_name",
                        columnNames = {"tenant_id", "name"}
                ),
                @UniqueConstraint(
                        name = "uk_tax_class_tenant_code",
                        columnNames = {"tenant_id", "code"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
public class TaxClass extends TenantAwareEntity {

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false, length = 10)
    private TaxType taxType = TaxType.VAT;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean defaultTaxClass = false;

    @Column(name = "hsn_sac_code", length = 10)
    private String hsnSacCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @OneToMany(
            mappedBy = "taxClass",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<TaxRate> rates = new ArrayList<>();

    public boolean isGst() {
        return taxType == TaxType.GST;
    }

    public boolean isVat() {
        return taxType == TaxType.VAT;
    }

    public boolean isTaxExempt() {
        return taxType == TaxType.NONE;
    }

    public boolean hasHsnSacCode() {
        return hsnSacCode != null && !hsnSacCode.isBlank();
    }

    public void markAsDefault() {
        this.defaultTaxClass = true;
    }

    public void removeAsDefault() {
        this.defaultTaxClass = false;
    }

    public String getDisplayName() {
        return code + " - " + name;
    }
}