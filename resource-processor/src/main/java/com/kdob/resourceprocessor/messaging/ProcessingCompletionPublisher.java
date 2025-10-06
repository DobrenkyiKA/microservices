package com.kdob.resourceprocessor.messaging;

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
public class ProcessingCompletionPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${resource.processing.completion.topic}")
    private String topic;

    @Retryable(
            retryFor = {RuntimeException.class, ExecutionException.class, InterruptedException.class},
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    public void publishProcessingCompleted(final Long resourceId) {
        log.info("Attempting to publish processing-completed event for resourceId=[{}]", resourceId);
        final String payload = String.valueOf(resourceId);
        
        try {
            final SendResult<String, String> result = kafkaTemplate.send(topic, payload).get();
            log.info("Published processing-completed resourceId=[{}] to topic=[{}], partition=[{}], offset=[{}]",
                    resourceId, result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to publish processing-completed event for resourceId=[{}], will retry if possible. Error: [{}]",
                    resourceId, e.getMessage());
            throw new RuntimeException("Failed to publish processing-completed event for resourceId=" + resourceId, e);
        }
    }

    @Recover
    public void recover(Exception ex, Long resourceId) {
        log.error("All retry attempts failed for publishing processing-completed event with resourceId=[{}]. Final error: [{}]",
                resourceId, ex.getMessage(), ex);
        throw new RuntimeException("Failed to publish processing-completed event for resourceId=" + resourceId + " after all retry attempts", ex);
    }
}
