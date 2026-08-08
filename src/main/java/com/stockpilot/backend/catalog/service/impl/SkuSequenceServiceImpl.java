package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.repository.SkuSequenceRepository;
import com.stockpilot.backend.catalog.service.SkuSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkuSequenceServiceImpl
        implements SkuSequenceService {

    private final SkuSequenceRepository skuSequenceRepository;

    @Override
    @Transactional
    public long nextValue(UUID tenantId) {

        return skuSequenceRepository.getNextValue(tenantId);
    }
}