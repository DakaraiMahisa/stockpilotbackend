package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record OrgSettingsDto(

        PasswordPolicyDto passwordPolicy,

        SessionPolicyDto sessionPolicy,

        InvitePolicyDto invitePolicy,

        GeneralSettingsDto general

) {
}