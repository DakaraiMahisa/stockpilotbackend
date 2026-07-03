package com.stockpilot.backend.shared.storage;

import com.stockpilot.backend.org.dto.response.PresignedUploadResponse;
import com.stockpilot.backend.org.dto.storage.StoredObject;

import java.util.UUID;

public interface StorageService {

    PresignedUploadResponse generateOrganizationLogoUploadUrl(
            UUID tenantId,
            String filename,
            String contentType
    );
    StoredObject getObject(String objectKey);

    boolean objectExists(String objectKey);


    void deleteObject(String objectKey);
}