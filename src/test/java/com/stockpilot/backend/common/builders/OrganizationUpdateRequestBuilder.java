package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.request.OrganizationUpdateRequest;

public class OrganizationUpdateRequestBuilder {

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

    private String website = "https://stockpilot.com";

    private OrganizationUpdateRequestBuilder() {
    }

    public static OrganizationUpdateRequestBuilder withDefaults() {
        return new OrganizationUpdateRequestBuilder();
    }

    public OrganizationUpdateRequestBuilder legalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    public OrganizationUpdateRequestBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public OrganizationUpdateRequestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public OrganizationUpdateRequestBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public OrganizationUpdateRequestBuilder addressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public OrganizationUpdateRequestBuilder addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public OrganizationUpdateRequestBuilder city(String city) {
        this.city = city;
        return this;
    }

    public OrganizationUpdateRequestBuilder stateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
        return this;
    }

    public OrganizationUpdateRequestBuilder postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public OrganizationUpdateRequestBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public OrganizationUpdateRequestBuilder gstinVatNumber(String gstinVatNumber) {
        this.gstinVatNumber = gstinVatNumber;
        return this;
    }

    public OrganizationUpdateRequestBuilder website(String website) {
        this.website = website;
        return this;
    }

    public OrganizationUpdateRequest build() {
        return new OrganizationUpdateRequest(
                legalName,
                displayName,
                email,
                phone,
                addressLine1,
                addressLine2,
                city,
                stateProvince,
                postalCode,
                countryCode,
                gstinVatNumber,
                website
        );
    }
}
