package com.stockpilot.backend.shared.storage;

import com.stockpilot.backend.org.dto.PresignedUploadResponse;
import com.stockpilot.backend.org.dto.StoredObject;
import com.stockpilot.backend.shared.exception.StorageException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    @Override
    public PresignedUploadResponse generateOrganizationLogoUploadUrl(
            UUID tenantId,
            String filename,
            String contentType
    ) {

        validateContentType(contentType);

        if (filename == null || filename.isBlank()) {
            throw new StorageException("Filename is required.");
        }
        String extension = getFileExtension(filename);

        log.debug("Generated presigned upload URL for tenant {}", tenantId);

        String objectKey = "%s/%s/%s.%s".formatted(
                storageProperties.getFolders().getOrganizationLogos(),
                tenantId,
                UUID.randomUUID(),
                extension
        );

        try {

            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.PUT)
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .expiry((int) storageProperties.getUpload()
                                    .getPresignedUrlExpirySeconds())
                            .build()
            );

            return PresignedUploadResponse.builder()
                    .uploadUrl(uploadUrl)
                    .objectKey(objectKey)
                    .expiresIn(storageProperties.getUpload()
                            .getPresignedUrlExpirySeconds())
                    .build();

        } catch (Exception ex) {
            throw new StorageException("Failed to generate presigned upload URL.", ex);
        }
    }
    @Override
    public StoredObject getObject(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .build());

            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .build());

            return new StoredObject(
                    inputStream,
                    stat.contentType(),
                    stat.size()
            );

        } catch (Exception ex) {
            throw new StorageException(
                    "Failed to retrieve object: " + objectKey,
                    ex
            );
        }
    }
    @Override
    public boolean objectExists(String objectKey) {

        try {

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .build()
            );

            return true;

        } catch (ErrorResponseException ex) {

            if ("NoSuchKey".equals(ex.errorResponse().code())) {
                return false;
            }

            throw new StorageException("Failed to verify object existence.", ex);

        } catch (Exception ex) {
            throw new StorageException("Failed to verify object existence.", ex);
        }
    }

    @Override
    public void deleteObject(String objectKey) {

        try {

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageProperties.getBucket())
                            .object(objectKey)
                            .build()
            );

        } catch (Exception ex) {
            throw new StorageException("Failed to delete object.", ex);
        }
    }

    private void validateContentType(String contentType) {

        if (!storageProperties.getAllowedContentTypes().contains(contentType)) {
            throw new StorageException(
                    "Unsupported content type: " + contentType
            );
        }
    }

    private String getFileExtension(String filename) {

        int index = filename.lastIndexOf('.');

        if (index < 0) {
            return "png";
        }

        return filename.substring(index + 1).toLowerCase();
    }
}