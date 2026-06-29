package com.stockpilot.backend.org.dto;

import lombok.Builder;

@Builder
public record PresignedUploadResponse(

        String uploadUrl,

        String objectKey,

        long expiresIn

) {
}
