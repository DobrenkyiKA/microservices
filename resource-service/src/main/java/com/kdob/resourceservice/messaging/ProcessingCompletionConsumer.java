package com.kdob.resourceservice.messaging;

import com.kdob.resourceservice.service.ResourceS3AwsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessingCompletionConsumer {
    
    private final ResourceS3AwsService resourceS3AwsService;
    
    @KafkaListener(topics = "${resource.processing.completion.topic:resource-processing-completed}", 
                   groupId = "${resource.processing.completion.groupId:resource-service-group}")
    public void onProcessingCompleted(String resourceIdPayload) {
        Long resourceId;
        try {
            resourceId = Long.parseLong(resourceIdPayload);
        } catch (NumberFormatException ex) {
            log.error("Received invalid processing completion payload: [{}]", resourceIdPayload, ex);
            return; // Skip processing for invalid payloads
        }
        log.info("Received processing completion notification for resource ID: {}", resourceId);
        
        try {
            resourceS3AwsService.moveToPermanentStorage(resourceId);
            log.info("Successfully processed completion notification for resource ID: [{}]", resourceId);
        } catch (Exception e) {
            log.error("Failed to process completion notification for resource ID: [{}], error: [{}]",
                     resourceId, e.getMessage(), e);
        }
    }
}