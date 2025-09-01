package com.kdob.resourceservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${resource.kafka.topic}")
    private String topic;

    @Retryable(
            retryFor = {RuntimeException.class, ExecutionException.class, InterruptedException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    public void publishResourceCreated(final Long resourceId) {
        log.info("Attempting to publish resource-created event for id={}", resourceId);
        final String payload = String.valueOf(resourceId);
        
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, payload).get();
            log.info("Published resource-created id={} to topic={}, partition={}, offset={}",
                    resourceId, result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to publish resource-created event for id={}, will retry if possible. Error: {}", 
                    resourceId, e.getMessage());
            throw new RuntimeException("Failed to publish resource-created event for id=" + resourceId, e);
        }
    }

    @Recover
    public void recover(Exception ex, Long resourceId) {
        log.error("All retry attempts failed for publishing resource-created event with id={}. Final error: {}", 
                resourceId, ex.getMessage(), ex);
        throw new RuntimeException("Failed to publish resource-created event for id=" + resourceId + " after all retry attempts", ex);
    }
}
