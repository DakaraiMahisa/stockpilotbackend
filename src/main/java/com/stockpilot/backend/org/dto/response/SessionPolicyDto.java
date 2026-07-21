package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record SessionPolicyDto(

        Integer sessionTimeoutMins,

        Integer maxConcurrentSessions,

        Integer rememberMeDays,

        Boolean enforceDeviceTrust

) {
}
