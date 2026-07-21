package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record InvitePolicyDto(

        Integer inviteExpiryHours,

        Boolean allowSelfRegistration,

        Boolean requireEmailVerification

) {
}