package com.stockpilot.backend.org.dto.storage;

import java.io.InputStream;

public record StoredObject(
        InputStream inputStream,
        String contentType,
        long contentLength
) {
}
