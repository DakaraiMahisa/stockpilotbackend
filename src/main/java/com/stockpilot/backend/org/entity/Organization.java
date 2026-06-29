package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
public class Organization extends TenantAwareEntity {


    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;


    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;


    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Builder.Default
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode = "ZW";


    @Column(name = "gstin_vat_number", length = 20)
    private String gstinVatNumber;


    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(length = 200)
    private String website;

    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
