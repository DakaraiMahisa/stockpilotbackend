package com.stockpilot.backend.org.mapper;

import com.stockpilot.backend.org.dto.response.SubscriptionDto;
import com.stockpilot.backend.org.dto.response.SubscriptionLimitsDto;
import com.stockpilot.backend.org.dto.response.SubscriptionUsageDto;
import com.stockpilot.backend.org.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubscriptionMapper {

    @Mapping(target = "usage", ignore = true)
    @Mapping(target = "limits", source = ".")
    SubscriptionDto toDto(Subscription subscription);

    SubscriptionLimitsDto toLimitsDto(Subscription subscription);

    SubscriptionUsageDto toUsageDto(
            long users,
            long branches,
            long skus
    );
}