package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.*;

public interface OrganizationService {

    OrganizationDto getProfile();

    OrganizationDto updateProfile(OrganizationUpdateRequest request);

    PresignedUploadResponse generatePresignedUrl(
            LogoPresignedRequest request);

    OrganizationDto confirmLogoUpload(
            LogoConfirmRequest request);
}