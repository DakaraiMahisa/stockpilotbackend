package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.entity.Organization;

import java.util.UUID;

public class OrganizationBuilder {

    private UUID id = UUID.randomUUID();
    private UUID tenantId = UUID.randomUUID();

    private String legalName = "StockPilot Demo Pvt Ltd";
    private String displayName = "StockPilot Demo";
    private String email = "info@stockpilot.com";
    private String phone = "+263771234567";

    private String addressLine1 = "123 Enterprise Street";
    private String addressLine2 = null;
    private String city = "Harare";
    private String stateProvince = null;
    private String postalCode = null;

    private String countryCode = "ZW";
    private String gstinVatNumber = null;

    private String logoUrl = null;
    private String website = "https://stockpilot.com";

    private OrganizationBuilder() {
    }

    public static OrganizationBuilder anOrganization() {
        return new OrganizationBuilder();
    }

    public OrganizationBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public OrganizationBuilder tenantId(UUID tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public OrganizationBuilder legalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    public OrganizationBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public OrganizationBuilder email(String email) {
        this.email = email;
        return this;
    }

    public OrganizationBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public OrganizationBuilder addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public OrganizationBuilder addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public OrganizationBuilder city(String city) {
        this.city = city;
        return this;
    }

    public OrganizationBuilder stateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
        return this;
    }

    public OrganizationBuilder postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public OrganizationBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public OrganizationBuilder gstinVatNumber(String gstinVatNumber) {
        this.gstinVatNumber = gstinVatNumber;
        return this;
    }

    public OrganizationBuilder logoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public OrganizationBuilder website(String website) {
        this.website = website;
        return this;
    }

    public Organization build() {
        Organization organization = Organization.builder()
                .legalName(legalName)
                .displayName(displayName)
                .email(email)
                .phone(phone)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .countryCode(countryCode)
                .gstinVatNumber(gstinVatNumber)
                .logoUrl(logoUrl)
                .website(website)
                .build();

        organization.setId(id);
        organization.setTenantId(tenantId);

        return organization;
    }
}