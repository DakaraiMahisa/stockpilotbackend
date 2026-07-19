package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.request.SubscriptionUpgradeRequest;
import com.stockpilot.backend.org.dto.response.SubscriptionDto;
import com.stockpilot.backend.org.dto.response.UpgradeRequestResponse;
import com.stockpilot.backend.org.service.SubscriptionService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;

import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.SUBSCRIPTIONS)
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.SubscriptionPermissions).READ)")
    public ResponseEntity<ApiResponse<SubscriptionDto>> getCurrentSubscription() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        subscriptionService.getCurrentSubscription(),
                        ApiMessages.SUBSCRIPTION_RETRIEVED
                )
        );
    }

    @PostMapping("/upgrade-request")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.SubscriptionPermissions).UPGRADE)")
    public ResponseEntity<ApiResponse<UpgradeRequestResponse>> submitUpgradeRequest(
            @Valid @RequestBody SubscriptionUpgradeRequest request
    ) {

        return ResponseEntity.accepted().body(
                ApiResponse.success(
                        subscriptionService.submitUpgradeRequest(request),
                        ApiMessages.SUBSCRIPTION_UPGRADE_REQUEST_SUBMITTED
                )
        );
    }
}