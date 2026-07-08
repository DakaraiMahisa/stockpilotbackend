package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.BusinessConfigUpdateRequest;
import com.stockpilot.backend.org.dto.response.BusinessConfigDto;

public interface BusinessConfigService {

    BusinessConfigDto getConfiguration();

    BusinessConfigDto updateConfiguration(
            BusinessConfigUpdateRequest request
    );
}