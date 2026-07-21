package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateInvitePolicyRequest(

        @Min(1)
        @Max(168)
        Integer inviteExpiryHours,

        Boolean allowSelfRegistration,

        Boolean requireEmailVerification

) {
}
