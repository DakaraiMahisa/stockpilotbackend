package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.LogoConfirmRequest;
import com.stockpilot.backend.org.dto.request.LogoPresignedRequest;
import com.stockpilot.backend.org.dto.response.OrganizationDto;
import com.stockpilot.backend.org.dto.request.OrganizationUpdateRequest;
import com.stockpilot.backend.org.dto.response.PresignedUploadResponse;
import com.stockpilot.backend.org.dto.storage.StoredObject;

public interface OrganizationService {

    OrganizationDto getProfile();
    StoredObject getOrganizationLogo();
    OrganizationDto updateProfile(OrganizationUpdateRequest request);

    PresignedUploadResponse generatePresignedUrl(
            LogoPresignedRequest request);

    OrganizationDto confirmLogoUpload(
            LogoConfirmRequest request);
}