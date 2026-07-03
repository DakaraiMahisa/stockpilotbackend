package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.request.LogoConfirmRequest;

public class LogoConfirmRequestBuilder {

    private String objectKey =
            "org-logos/tenant-123/logo.png";

    private LogoConfirmRequestBuilder() {
    }

    public static LogoConfirmRequestBuilder withDefaults() {
        return new LogoConfirmRequestBuilder();
    }

    public LogoConfirmRequestBuilder objectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public LogoConfirmRequest build() {
        return new LogoConfirmRequest(objectKey);
    }
}
