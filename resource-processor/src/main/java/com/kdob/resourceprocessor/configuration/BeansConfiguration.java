package com.kdob.resourceprocessor.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

@Configuration
@EnableRetry
@RequiredArgsConstructor
public class BeansConfiguration {

    private final CustomRetryListener customRetryListener;

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(4)
                .exponentialBackoff(1000, 2.0, 10000)
                .withListener(customRetryListener)
                .build();
    }
}
