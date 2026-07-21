package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateSessionPolicyRequest(

        @Min(5)
        @Max(480)
        Integer sessionTimeoutMins,

        @Min(1)
        @Max(5)
        Integer maxConcurrentSessions,

        @Min(0)
        @Max(30)
        Integer rememberMeDays,

        Boolean enforceDeviceTrust

) {
}
