package com.stockpilot.backend.common.builders;



import com.stockpilot.backend.tenant.domain.entity.Tenant;

import java.time.Instant;
import java.util.UUID;

public final class TenantBuilder {


    private UUID id = UUID.randomUUID();

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private Long version = 0L;

    private boolean deleted = false;


    private String name = "StockPilot Demo Ltd";

    private String code = "STOCKPILOT";

    private String legalName = "StockPilot Demo Private Limited";

    private String email = "contact@stockpilot.test";

    private String phone = "+91-9876543210";

    private String taxRegistrationNumber = "GSTIN123456789";

    private String timezone = "Asia/Kolkata";

    private String currencyCode = "INR";

    private boolean active = true;

    private TenantBuilder() {
    }

    public static TenantBuilder aTenant() {
        return new TenantBuilder();
    }

    public static TenantBuilder activeTenant() {
        return aTenant().active(true);
    }

    public static TenantBuilder inactiveTenant() {
        return aTenant().active(false);
    }

    public TenantBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public TenantBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TenantBuilder code(String code) {
        this.code = code;
        return this;
    }

    public TenantBuilder legalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    public TenantBuilder email(String email) {
        this.email = email;
        return this;
    }

    public TenantBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public TenantBuilder taxRegistrationNumber(String taxRegistrationNumber) {
        this.taxRegistrationNumber = taxRegistrationNumber;
        return this;
    }

    public TenantBuilder timezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    public TenantBuilder currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public TenantBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public TenantBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TenantBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public TenantBuilder version(Long version) {
        this.version = version;
        return this;
    }

    public TenantBuilder deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Tenant build() {

        return Tenant.builder()
                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .deleted(deleted)
                .name(name)
                .code(code)
                .legalName(legalName)
                .email(email)
                .phone(phone)
                .taxRegistrationNumber(taxRegistrationNumber)
                .timezone(timezone)
                .currencyCode(currencyCode)
                .active(active)
                .build();
    }
}
