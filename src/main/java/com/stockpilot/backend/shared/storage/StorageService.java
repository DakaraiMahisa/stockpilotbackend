package com.stockpilot.backend.shared.storage;

import com.stockpilot.backend.org.dto.PresignedUploadResponse;

import java.util.UUID;

public interface StorageService {

    PresignedUploadResponse generateOrganizationLogoUploadUrl(
            UUID tenantId,
            String filename,
            String contentType
    );


    boolean objectExists(String objectKey);


    void deleteObject(String objectKey);
}