package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.UpdateGeneralSettingsRequest;
import com.stockpilot.backend.org.dto.request.UpdateInvitePolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdatePasswordPolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdateSessionPolicyRequest;
import com.stockpilot.backend.org.dto.response.OrgSettingsDto;
import com.stockpilot.backend.org.entity.OrgSettings;
import com.stockpilot.backend.org.entity.Organization;

import java.util.UUID;

public interface OrgSettingsService {


    OrgSettingsDto getSettings();

    OrgSettingsDto updatePasswordPolicy(
            UpdatePasswordPolicyRequest request
    );

    OrgSettingsDto updateSessionPolicy(
            UpdateSessionPolicyRequest request
    );

    OrgSettingsDto updateInvitePolicy(
            UpdateInvitePolicyRequest request
    );

    OrgSettingsDto updateGeneralSettings(
            UpdateGeneralSettingsRequest request
    );

    OrgSettings createDefaultSettings(
            Organization organization
    );

    OrgSettings getSettings(UUID tenantId);
}
