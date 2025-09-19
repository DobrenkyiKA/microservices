package com.kdob.resourceservice.integration;

import com.kdob.resourceservice.messaging.ResourceEventPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"resource-events"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9093",
        "port=9093"
    }
)
@DirtiesContext
@DisplayName("Kafka Messaging Integration Tests")
class KafkaMessagingIntegrationTest {

    @Autowired
    private ResourceEventPublisher resourceEventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("resource-events"));
    }

    @Test
    @DisplayName("Should publish resource created event to Kafka topic")
    void shouldPublishResourceCreatedEventToKafkaTopic() {
        // Given
        Long resourceId = 123L;

        // When
        resourceEventPublisher.publishResourceCreated(resourceId);

        // Then
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records).isNotEmpty();
        
        boolean messageFound = false;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("123")) {
                messageFound = true;
                System.out.println("[DEBUG_LOG] Received Kafka message: " + record.value());
                break;
            }
        }
        
        assertThat(messageFound).isTrue();
        System.out.println("[DEBUG_LOG] Successfully published and consumed resource created event for ID: " + resourceId);
    }

    @Test
    @DisplayName("Should handle multiple resource events")
    void shouldHandleMultipleResourceEvents() {
        // Given
        Long[] resourceIds = {100L, 200L, 300L};

        // When
        for (Long resourceId : resourceIds) {
            resourceEventPublisher.publishResourceCreated(resourceId);
        }

        // Then
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        assertThat(records.count()).isGreaterThanOrEqualTo(3);
        
        int messagesReceived = 0;
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("100") || 
                record.value().contains("200") || 
                record.value().contains("300")) {
                messagesReceived++;
                System.out.println("[DEBUG_LOG] Received message: " + record.value());
            }
        }
        
        assertThat(messagesReceived).isEqualTo(3);
        System.out.println("[DEBUG_LOG] Successfully handled " + messagesReceived + " resource events");
    }
}