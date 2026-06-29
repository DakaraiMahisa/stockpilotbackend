package com.stockpilot.backend.org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LogoPresignedRequest(

        @NotBlank(message = "Filename is required.")
        @Size(max = 255, message = "Filename must not exceed 255 characters.")
        String filename,

        @NotBlank(message = "Content type is required.")
        @Pattern(
                regexp = "^image/(png|jpeg)$",
                message = "Only PNG and JPEG images are supported."
        )
        String contentType

) {
}