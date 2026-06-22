package com.stockpilot.backend.identity.audits.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetCompletedEvent {

    private final String email;

    private final String firstName;
}