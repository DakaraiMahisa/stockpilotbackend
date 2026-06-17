package com.stockpilot.backend.identity.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record InviteUserRequestDto(
        @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        UUID roleId) {
}
