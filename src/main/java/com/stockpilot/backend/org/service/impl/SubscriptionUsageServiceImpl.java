package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.org.dto.response.SubscriptionDto;
import com.stockpilot.backend.org.dto.response.SubscriptionUsageDto;
import com.stockpilot.backend.org.entity.Subscription;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.mapper.SubscriptionMapper;
import com.stockpilot.backend.org.repository.BranchRepository;
import com.stockpilot.backend.org.service.SubscriptionUsageService;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionUsageServiceImpl implements SubscriptionUsageService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public SubscriptionUsageDto getUsage(UUID tenantId) {

        long users = userRepository.countByTenantIdAndActiveTrue(tenantId);

        long branches = branchRepository.countByTenantIdAndStatus(
                tenantId,
                BranchStatus.ACTIVE
        );

        return subscriptionMapper.toUsageDto(
                users,
                branches,
                0L // temporary until Product module
        );
    }
}