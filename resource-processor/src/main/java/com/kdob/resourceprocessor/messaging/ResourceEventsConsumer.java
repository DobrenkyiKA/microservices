package com.kdob.resourceprocessor.messaging;

import com.kdob.resourceprocessor.dto.ResourceDto;
import com.kdob.resourceprocessor.dto.request.SongMetadataRequestDto;
import com.kdob.resourceprocessor.integration.ResourceMetadataIntegrationService;
import com.kdob.resourceprocessor.service.ResourceMetadataExtractionService;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventsConsumer {

    private final RestClient restClient;
    private final EurekaClient discoveryClient;
    private final ResourceMetadataExtractionService resourceMetadataExtractionService;
    private final ResourceMetadataIntegrationService resourceMetadataIntegrationService;

    @Value("${resource.service.application.name}")
    private String applicationName;

    @KafkaListener(topics = "${resource.kafka.topic}", groupId = "${resource.kafka.groupId}")
    public void onResourceCreated(final Long resourceId) {
        log.info("Consumed resource-created event, resourceId={}", resourceId);
        final ResourceDto response = fetchResourceData(resourceId);
        final SongMetadataRequestDto songMetadata = resourceMetadataExtractionService.createSongMetadata(response);
        resourceMetadataIntegrationService.send(songMetadata);
    }

    @Retryable(
            retryFor = {HttpServerErrorException.class, ResourceAccessException.class, RuntimeException.class},
            noRetryFor = {HttpClientErrorException.BadRequest.class, HttpClientErrorException.Unauthorized.class, 
                         HttpClientErrorException.Forbidden.class, HttpClientErrorException.NotFound.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    private ResourceDto fetchResourceData(final Long resourceId) {
        log.info("Attempting to fetch resource data for resourceId: {}", resourceId);
        final String resourceService = discoveryClient.getNextServerFromEureka(applicationName, false).getHomePageUrl();
        
        try {
            final ResourceDto response = restClient.get()
                    .uri(resourceService + resourceId)
                    .retrieve()
                    .body(ResourceDto.class);
            log.info("Successfully fetched resource data for resourceId: {}", resourceId);
            return response;
        } catch (Exception e) {
            log.warn("Failed to fetch resource data for resourceId: {}, will retry if possible. Error: {}", resourceId, e.getMessage());
            throw e;
        }
    }

    @Recover
    private ResourceDto recover(Exception ex, String resourceId) {
        log.error("All retry attempts failed for fetching resource data with resourceId: {}. Final error: {}", resourceId, ex.getMessage(), ex);
        throw new RuntimeException("Failed to fetch resource data for resourceId: " + resourceId + " after all retry attempts", ex);
    }
}
