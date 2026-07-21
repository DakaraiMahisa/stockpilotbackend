package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.shared.validation.annotation.ValidLanguageTag;
import com.stockpilot.backend.shared.validation.annotation.ValidTimezone;
import lombok.Builder;

@Builder
public record UpdateGeneralSettingsRequest(

        @ValidLanguageTag
        String defaultLanguage,

        @ValidTimezone
        String defaultTimezone,

        Boolean maintenanceMode

) {
}
