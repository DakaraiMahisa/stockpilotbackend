package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OrganizationUpdateRequest(

        @Size(max = 200)
        String legalName,

        @Size(max = 120)
        String displayName,

        @Email
        @Size(max = 150)
        String email,

        @Pattern(
                regexp = "^\\+[1-9]\\d{1,14}$",
                message = "Phone number must be in E.164 format."
        )
        String phone,

        @Size(max = 200)
        String addressLine1,

        @Size(max = 200)
        String addressLine2,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String stateProvince,

        @Size(max = 20)
        String postalCode,

        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Country code must be a valid ISO-3166 alpha-2 code."
        )
        String countryCode,

        @Size(max = 20)
        String gstinVatNumber,

        @Size(max = 200)
        String website

) {
}