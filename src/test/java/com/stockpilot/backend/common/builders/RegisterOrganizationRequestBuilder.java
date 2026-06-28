package com.stockpilot.backend.common.builders;


import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;


public final class RegisterOrganizationRequestBuilder {

    private String organizationName = "StockPilot Demo";

    private String firstName = "John";

    private String lastName = "Doe";

    private String email = "owner@test.com";

    private String password = "Password@123";

    private String timezone = "Asia/Kolkata";

    private String currencyCode = "INR";

    private RegisterOrganizationRequestBuilder() {
    }

    public static RegisterOrganizationRequestBuilder aRequest() {
        return new RegisterOrganizationRequestBuilder();
    }

    public RegisterOrganizationRequestBuilder organizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public RegisterOrganizationRequestBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public RegisterOrganizationRequestBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public RegisterOrganizationRequestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public RegisterOrganizationRequestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public RegisterOrganizationRequestBuilder timezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public RegisterOrganizationRequestBuilder currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public RegisterOrganizationRequest build() {

        return RegisterOrganizationRequest.builder()
                .organizationName(organizationName)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .timezone(timezone)
                .currencyCode(currencyCode)
                .build();
    }

    public static RegisterOrganizationRequestBuilder withDefaults() {
        return aRequest();
    }

    public static RegisterOrganizationRequestBuilder withoutOptionalFields() {
        return aRequest()
                .timezone(null)
                .currencyCode(null);
    }
}