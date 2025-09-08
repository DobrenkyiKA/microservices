package com.kdob.resourceservice.integration;

import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceIntegrationService {

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
    public void deleteSongMetadata(final String id) {
        log.info("Attempting to delete song metadata with id: {}", id);
        final String songServiceUrl = discoveryClient.getNextServerFromEureka(applicationName, false).getHomePageUrl();
        
        try {
            restClient.delete()
                    .uri(songServiceUrl + "?id=" + id)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Successfully deleted song metadata with id: {}", id);
        } catch (Exception e) {
            log.warn("Failed to delete song metadata with id: {}, will retry if possible. Error: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting song metadata with id: " + id, e);
        }
    }

    @Recover
    public void recover(Exception ex, String id) {
        log.error("All retry attempts failed for deleting song metadata with id: {}. Final error: {}", id, ex.getMessage(), ex);
        throw new RuntimeException("Failed to delete song metadata with id: " + id + " after all retry attempts", ex);
    }
}
