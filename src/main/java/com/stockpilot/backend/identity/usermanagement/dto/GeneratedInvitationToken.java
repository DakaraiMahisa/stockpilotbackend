package com.stockpilot.backend.identity.usermanagement.dto;

import com.stockpilot.backend.identity.usermanagement.entity.InvitationToken;

public record GeneratedInvitationToken(
        String rawToken,
        InvitationToken entity
) {
}