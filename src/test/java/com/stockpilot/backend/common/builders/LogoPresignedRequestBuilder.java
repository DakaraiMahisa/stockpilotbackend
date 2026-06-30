package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.LogoPresignedRequest;

public class LogoPresignedRequestBuilder {

    private String filename = "logo.png";
    private String contentType = "image/png";

    private LogoPresignedRequestBuilder() {
    }

    public static LogoPresignedRequestBuilder withDefaults() {
        return new LogoPresignedRequestBuilder();
    }

    public LogoPresignedRequestBuilder filename(String filename) {
        this.filename = filename;
        return this;
    }

    public LogoPresignedRequestBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public LogoPresignedRequest build() {
        return new LogoPresignedRequest(
                filename,
                contentType
        );
    }
}