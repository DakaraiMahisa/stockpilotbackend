package com.stockpilot.backend.identity.domain.events;

import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoginSuccessEvent extends ApplicationEvent {

    private final CurrentUserPrincipal currentUserPrincipal;

    public LoginSuccessEvent(Object source, CurrentUserPrincipal currentUserPrincipal) {
        super(source);
        this.currentUserPrincipal = currentUserPrincipal;
    }
}

