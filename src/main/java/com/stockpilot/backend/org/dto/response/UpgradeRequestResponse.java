package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UpgradeRequestResponse(

        UUID requestId,

        String message

) {
}