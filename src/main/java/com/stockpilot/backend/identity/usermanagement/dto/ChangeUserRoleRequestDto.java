package com.stockpilot.backend.identity.usermanagement.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeUserRoleRequestDto(

        @NotNull
        UUID roleId
) {
}
