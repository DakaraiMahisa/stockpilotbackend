package com.stockpilot.backend.org.mapper;

import com.stockpilot.backend.org.dto.request.UpdateGeneralSettingsRequest;
import com.stockpilot.backend.org.dto.request.UpdateInvitePolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdatePasswordPolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdateSessionPolicyRequest;
import com.stockpilot.backend.org.dto.response.*;
import com.stockpilot.backend.org.entity.OrgSettings;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrgSettingsMapper {

    // ============================================================
    // Response DTOs
    // ============================================================

    PasswordPolicyDto toPasswordPolicyDto(OrgSettings settings);

    SessionPolicyDto toSessionPolicyDto(OrgSettings settings);

    InvitePolicyDto toInvitePolicyDto(OrgSettings settings);

    GeneralSettingsDto toGeneralSettingsDto(OrgSettings settings);

    @Mapping(
            target = "passwordPolicy",
            expression = "java(toPasswordPolicyDto(settings))"
    )
    @Mapping(
            target = "sessionPolicy",
            expression = "java(toSessionPolicyDto(settings))"
    )
    @Mapping(
            target = "invitePolicy",
            expression = "java(toInvitePolicyDto(settings))"
    )
    @Mapping(
            target = "general",
            expression = "java(toGeneralSettingsDto(settings))"
    )
    OrgSettingsDto toDto(OrgSettings settings);

    // ============================================================
    // Password Policy
    // ============================================================

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "sessionTimeoutMins", ignore = true)
    @Mapping(target = "maxConcurrentSessions", ignore = true)
    @Mapping(target = "rememberMeDays", ignore = true)
    @Mapping(target = "enforceDeviceTrust", ignore = true)
    @Mapping(target = "inviteExpiryHours", ignore = true)
    @Mapping(target = "allowSelfRegistration", ignore = true)
    @Mapping(target = "requireEmailVerification", ignore = true)
    @Mapping(target = "defaultLanguage", ignore = true)
    @Mapping(target = "defaultTimezone", ignore = true)
    @Mapping(target = "maintenanceMode", ignore = true)
    void updatePasswordPolicy(
            UpdatePasswordPolicyRequest request,
            @MappingTarget OrgSettings settings
    );

    // ============================================================
    // Session Policy
    // ============================================================

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "minPasswordLength", ignore = true)
    @Mapping(target = "requireUppercase", ignore = true)
    @Mapping(target = "requireNumber", ignore = true)
    @Mapping(target = "requireSpecialChar", ignore = true)
    @Mapping(target = "passwordExpiryDays", ignore = true)
    @Mapping(target = "maxLoginAttempts", ignore = true)
    @Mapping(target = "lockoutDurationMins", ignore = true)
    @Mapping(target = "inviteExpiryHours", ignore = true)
    @Mapping(target = "allowSelfRegistration", ignore = true)
    @Mapping(target = "requireEmailVerification", ignore = true)
    @Mapping(target = "defaultLanguage", ignore = true)
    @Mapping(target = "defaultTimezone", ignore = true)
    @Mapping(target = "maintenanceMode", ignore = true)
    void updateSessionPolicy(
            UpdateSessionPolicyRequest request,
            @MappingTarget OrgSettings settings
    );

    // ============================================================
    // Invitation Policy
    // ============================================================

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "minPasswordLength", ignore = true)
    @Mapping(target = "requireUppercase", ignore = true)
    @Mapping(target = "requireNumber", ignore = true)
    @Mapping(target = "requireSpecialChar", ignore = true)
    @Mapping(target = "passwordExpiryDays", ignore = true)
    @Mapping(target = "maxLoginAttempts", ignore = true)
    @Mapping(target = "lockoutDurationMins", ignore = true)
    @Mapping(target = "sessionTimeoutMins", ignore = true)
    @Mapping(target = "maxConcurrentSessions", ignore = true)
    @Mapping(target = "rememberMeDays", ignore = true)
    @Mapping(target = "enforceDeviceTrust", ignore = true)
    @Mapping(target = "defaultLanguage", ignore = true)
    @Mapping(target = "defaultTimezone", ignore = true)
    @Mapping(target = "maintenanceMode", ignore = true)
    void updateInvitePolicy(
            UpdateInvitePolicyRequest request,
            @MappingTarget OrgSettings settings
    );

    // ============================================================
    // General Settings
    // ============================================================

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "minPasswordLength", ignore = true)
    @Mapping(target = "requireUppercase", ignore = true)
    @Mapping(target = "requireNumber", ignore = true)
    @Mapping(target = "requireSpecialChar", ignore = true)
    @Mapping(target = "passwordExpiryDays", ignore = true)
    @Mapping(target = "maxLoginAttempts", ignore = true)
    @Mapping(target = "lockoutDurationMins", ignore = true)
    @Mapping(target = "sessionTimeoutMins", ignore = true)
    @Mapping(target = "maxConcurrentSessions", ignore = true)
    @Mapping(target = "rememberMeDays", ignore = true)
    @Mapping(target = "enforceDeviceTrust", ignore = true)
    @Mapping(target = "inviteExpiryHours", ignore = true)
    @Mapping(target = "allowSelfRegistration", ignore = true)
    @Mapping(target = "requireEmailVerification", ignore = true)
    void updateGeneralSettings(
            UpdateGeneralSettingsRequest request,
            @MappingTarget OrgSettings settings
    );
}
