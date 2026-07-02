package com.stockpilot.backend.org.dto;

import java.io.InputStream;

public record StoredObject(
        InputStream inputStream,
        String contentType,
        long contentLength
) {
}
