package com.stockpilot.backend.identity.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptInvitationRequestDto(

        @NotBlank
        String token,

        @NotBlank
        @Size(min = 8, max = 64)
        String password
) {
}
