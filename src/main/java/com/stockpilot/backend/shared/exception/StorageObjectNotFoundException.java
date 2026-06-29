package com.stockpilot.backend.shared.exception;

public class StorageObjectNotFoundException extends StorageException {
    public StorageObjectNotFoundException(String objectKey) {
        super("Storage object not found: " + objectKey);
    }
}