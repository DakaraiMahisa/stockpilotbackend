package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LogoConfirmRequest(

        @NotBlank(message = "Object key is required.")
        @Size(max = 500, message = "Object key must not exceed 500 characters.")
        String objectKey

) {
}