package com.stockpilot.backend.tenant.domain.entity;

import com.stockpilot.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "tenants",
        indexes = {
                @Index(name = "idx_tenants_code", columnList = "code", unique = true),
                @Index(name = "idx_tenants_active", columnList = "active")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "legal_name", length = 200)
    private String legalName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "tax_registration_number", length = 100)
    private String taxRegistrationNumber;

    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    private String timezone = "Asia/Kolkata";

    @Column(name = "currency_code", nullable = false, length = 10)
    @Builder.Default
    private String currencyCode = "INR";

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}