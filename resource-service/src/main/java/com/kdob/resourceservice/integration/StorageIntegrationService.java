package com.kdob.resourceservice.integration;

import com.kdob.resourceservice.dto.StorageDto;
import com.kdob.resourceservice.enumeration.StorageType;
import com.netflix.discovery.EurekaClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static com.kdob.resourceservice.enumeration.StorageType.PERMANENT;
import static com.kdob.resourceservice.enumeration.StorageType.STAGING;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageIntegrationService {
    private static final String STORAGE_SERVICE_CIRCUIT_BREAKER = "storage-service-cb";
    private static final String STORAGE_SERVICE_IS_UNAVAILABLE = "Storage Service is unavailable, returning fallback stub data for type: [{}]. Error: [{}]";
    private static final List<StorageDto> ALL_STORAGES_FALLBACK = List.of(
            StorageDto.builder()
                    .id(1L)
                    .storageType(STAGING)
                    .bucket("staging-bucket")
                    .path("/staging").build(),

            StorageDto.builder()
                    .id(2L)
                    .storageType(PERMANENT)
                    .bucket("permanent-bucket")
                    .path("/permanent").build());

    private final RestClient restClient;
    private final EurekaClient discoveryClient;

    @Value("${storage.service.name}")
    private String storageServiceName;

    @CircuitBreaker(name = STORAGE_SERVICE_CIRCUIT_BREAKER, fallbackMethod = "getAllStoragesFallback")
    public List<StorageDto> getAllStorages() {
        String url = discoveryClient.getNextServerFromEureka(storageServiceName, false).getHomePageUrl();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<StorageDto> getAllStoragesFallback(final Exception ex) {
        log.warn("Storage Service is unavailable, returning fallback stub data. Error: [{}]", ex.getMessage());
        return ALL_STORAGES_FALLBACK;
    }

    private Optional<StorageDto> getStorageByTypeFallback(final StorageType storageType, final Exception ex) {
        log.warn(STORAGE_SERVICE_IS_UNAVAILABLE, storageType, ex.getMessage());
        return ALL_STORAGES_FALLBACK.stream()
                .filter(s -> storageType.equals(s.getStorageType()))
                .findFirst();
    }
}
