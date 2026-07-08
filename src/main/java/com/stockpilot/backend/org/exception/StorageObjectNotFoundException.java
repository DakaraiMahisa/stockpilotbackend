package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.StorageException;

public class StorageObjectNotFoundException extends StorageException {
    public StorageObjectNotFoundException(String objectKey) {
        super("Storage object not found: " + objectKey);
    }
}