package com.kdob.resourceservice.metrics;

import com.kdob.resourceservice.pojo.Resource;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MetrixExampleService {

    private final Counter orderCounter;
    private final Timer orderProcessingTimer;

    public MetrixExampleService(MeterRegistry meterRegistry) {
        this.orderCounter = Counter.builder("orders.created")
                .description("Total orders created")
                .tag("type", "business")
                .register(meterRegistry);

        this.orderProcessingTimer = Timer.builder("orders.processing.time")
                .description("Order processing time")
                .register(meterRegistry);
    }

    public void useMetrics(final Resource resource) {
        orderProcessingTimer.record(() -> {
            try {
                log.info("Processing resource: {}", resource.getKey());
                orderCounter.increment();
                log.debug("Order processed successfully: {}", resource.getKey());
            } catch (Exception e) {
                log.error("Error processing resource: {}", resource.getKey(), e);
                throw e;
            }
        });
    }
}