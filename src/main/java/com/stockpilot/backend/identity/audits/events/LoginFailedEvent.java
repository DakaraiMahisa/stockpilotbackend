package com.stockpilot.backend.identity.audits.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginFailedEvent {

    private final String tenantCode;
    private final String email;
    private final String reason;
    private final String userAgent;
    private final String ipAddress;
}
