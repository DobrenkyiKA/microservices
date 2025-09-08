package com.kdob.resourceprocessor.integration;

import com.kdob.resourceprocessor.dto.request.SongMetadataRequestDto;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceMetadataIntegrationService {

    private final RestClient restClient;
    private final EurekaClient discoveryClient;

    @Value("${song.service.application.name}")
    private String applicationName;

    @Retryable(
            retryFor = {HttpServerErrorException.class, ResourceAccessException.class, RuntimeException.class},
            noRetryFor = {HttpClientErrorException.BadRequest.class, HttpClientErrorException.Unauthorized.class,
                    HttpClientErrorException.Forbidden.class, HttpClientErrorException.NotFound.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    public void send(final SongMetadataRequestDto request) {
        log.info("Attempting to send song metadata to song service");
        final String songServiceUrl = discoveryClient.getNextServerFromEureka(applicationName, false).getHomePageUrl();

        try {
            final ResponseEntity<String> response = restClient.post()
                    .uri(songServiceUrl)
                    .body(request)
                    .retrieve()
                    .toEntity(String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new HttpServerErrorException(response.getStatusCode(), "Failed to send metadata: " + response.getStatusCode());
            }
            log.info("Successfully sent song metadata to song service");
        } catch (Exception e) {
            log.warn("Failed to send song metadata, will retry if possible. Error: {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public void recover(Exception ex, SongMetadataRequestDto request) {
        log.error("All retry attempts failed for sending song metadata. Final error: {}", ex.getMessage(), ex);
        throw new RuntimeException("Failed to send song metadata after all retry attempts", ex);
    }
}
