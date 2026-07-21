package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record GeneralSettingsDto(

        String defaultLanguage,

        String defaultTimezone,

        Boolean maintenanceMode

) {
}