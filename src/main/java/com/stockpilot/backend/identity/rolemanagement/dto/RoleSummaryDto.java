package com.stockpilot.backend.identity.rolemanagement.dto;

import java.util.UUID;

public record RoleSummaryDto(
        UUID id,
        String name
) {
}