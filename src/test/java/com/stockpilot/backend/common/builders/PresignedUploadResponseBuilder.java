package com.stockpilot.backend.common.builders;


import com.stockpilot.backend.org.dto.PresignedUploadResponse;

public class PresignedUploadResponseBuilder {

    private String uploadUrl =
            "http://localhost:9000/sme-platform/org-logos/test/logo.png?signature=test";

    private String objectKey =
            "org-logos/test/logo.png";

    private long expiresIn = 300;

    private PresignedUploadResponseBuilder() {
    }

    public static PresignedUploadResponseBuilder withDefaults() {
        return new PresignedUploadResponseBuilder();
    }

    public PresignedUploadResponseBuilder uploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
        return this;
    }

    public PresignedUploadResponseBuilder objectKey(String objectKey) {
        this.objectKey = objectKey;
        return this;
    }

    public PresignedUploadResponseBuilder expiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public PresignedUploadResponse build() {
        return PresignedUploadResponse.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .expiresIn(expiresIn)
                .build();
    }
}