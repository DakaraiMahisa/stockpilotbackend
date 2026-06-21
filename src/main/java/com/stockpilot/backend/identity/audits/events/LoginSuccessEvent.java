package com.stockpilot.backend.identity.audits.events;

import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import lombok.Getter;

@Getter
public class LoginSuccessEvent {

    private final CurrentUserPrincipal principal;
    private final String userAgent;

    public LoginSuccessEvent(
            CurrentUserPrincipal principal,
            String userAgent
    ) {
        this.principal = principal;
        this.userAgent = userAgent;
    }
}

