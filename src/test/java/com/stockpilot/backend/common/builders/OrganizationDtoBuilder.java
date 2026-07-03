package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.response.OrganizationDto;

import java.util.UUID;

public class OrganizationDtoBuilder {

    private UUID id = UUID.randomUUID();

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

    private OrganizationDtoBuilder() {
    }

    public static OrganizationDtoBuilder anOrganizationDto() {
        return new OrganizationDtoBuilder();
    }

    public OrganizationDtoBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public OrganizationDtoBuilder legalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    public OrganizationDtoBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public OrganizationDtoBuilder email(String email) {
        this.email = email;
        return this;
    }

    public OrganizationDtoBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public OrganizationDtoBuilder addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public OrganizationDtoBuilder addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public OrganizationDtoBuilder city(String city) {
        this.city = city;
        return this;
    }

    public OrganizationDtoBuilder stateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
        return this;
    }

    public OrganizationDtoBuilder postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public OrganizationDtoBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public OrganizationDtoBuilder gstinVatNumber(String gstinVatNumber) {
        this.gstinVatNumber = gstinVatNumber;
        return this;
    }

    public OrganizationDtoBuilder logoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public OrganizationDtoBuilder website(String website) {
        this.website = website;
        return this;
    }

    public OrganizationDto build() {
        return OrganizationDto.builder()
                .id(id)
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
    }
}
