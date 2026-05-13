package com.stockpilot.backend.identity.domain.events;

import com.stockpilot.backend.identity.domain.model.UserSession;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoginSuccessEvent extends ApplicationEvent {

    private final UserSession userSession;

    public LoginSuccessEvent(Object source, UserSession userSession) {
        super(source);
        this.userSession = userSession;
    }
}

