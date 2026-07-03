package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrganizationDto(

        UUID id,

        String legalName,

        String displayName,

        String email,

        String phone,

        String addressLine1,

        String addressLine2,

        String city,

        String stateProvince,

        String postalCode,

        String countryCode,

        String gstinVatNumber,

        String logoUrl,

        String website
) {
}