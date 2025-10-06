package com.kdob.resourceservice.service;

import com.kdob.resourceservice.dto.StorageDto;
import com.kdob.resourceservice.enumeration.StorageType;
import com.kdob.resourceservice.integration.StorageIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kdob.resourceservice.enumeration.StorageType.PERMANENT;
import static com.kdob.resourceservice.enumeration.StorageType.STAGING;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageIntegrationService storageIntegrationService;

    public Optional<StorageDto> getPermanentStorage() {
        return getStorageByType(PERMANENT);
    }

    public Optional<StorageDto> getStagingStorage() {
        return getStorageByType(STAGING);
    }

    public Optional<StorageDto> getStorageByType(final StorageType storageType) {
        return getStorage(storageType);
    }

    private Optional<StorageDto> getStorage(final StorageType storageType) {
        return storageIntegrationService.getAllStorages().stream()
                .filter(s -> storageType.equals(s.getStorageType()))
                .findFirst();
    }

}