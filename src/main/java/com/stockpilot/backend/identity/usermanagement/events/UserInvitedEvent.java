package com.stockpilot.backend.identity.usermanagement.events;

import com.stockpilot.backend.identity.domain.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInvitedEvent {
    private final User user;
}
