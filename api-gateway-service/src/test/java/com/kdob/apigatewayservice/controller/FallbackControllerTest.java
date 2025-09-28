package com.kdob.apigatewayservice.controller;

import com.kdob.apigatewayservice.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FallbackControllerTest {

    @Autowired
    private FallbackController fallbackController;

    @Test
    void resourceServiceFallback_ReturnsServiceUnavailableError() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/fallback/resource-service").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<ErrorResponse> result = fallbackController.resourceServiceFallback(exchange);

        StepVerifier.create(result)
                .assertNext(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus());
                    assertEquals("Service Unavailable", errorResponse.getError());
                    assertEquals("Resource Service is currently unavailable. Please try again later.", errorResponse.getMessage());
                    assertEquals("/fallback/resource-service", errorResponse.getPath());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    @Test
    void songServiceFallback_ReturnsServiceUnavailableError() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/fallback/song-service").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<ErrorResponse> result = fallbackController.songServiceFallback(exchange);

        StepVerifier.create(result)
                .assertNext(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus());
                    assertEquals("Service Unavailable", errorResponse.getError());
                    assertEquals("Song Service is currently unavailable. Please try again later.", errorResponse.getMessage());
                    assertEquals("/fallback/song-service", errorResponse.getPath());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    @Test
    void resourceProcessorFallback_ReturnsServiceUnavailableError() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/fallback/resource-processor").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<ErrorResponse> result = fallbackController.resourceProcessorFallback(exchange);

        StepVerifier.create(result)
                .assertNext(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus());
                    assertEquals("Service Unavailable", errorResponse.getError());
                    assertEquals("Resource Processor Service is currently unavailable. Please try again later.", errorResponse.getMessage());
                    assertEquals("/fallback/resource-processor", errorResponse.getPath());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    @Test
    void defaultFallback_ReturnsServiceUnavailableError() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/fallback/default").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<ErrorResponse> result = fallbackController.defaultFallback(exchange);

        StepVerifier.create(result)
                .assertNext(errorResponse -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.getStatus());
                    assertEquals("Service Unavailable", errorResponse.getError());
                    assertEquals("The requested service is currently unavailable. Please try again later.", errorResponse.getMessage());
                    assertEquals("/fallback/default", errorResponse.getPath());
                    assertNotNull(errorResponse.getTimestamp());
                })
                .verifyComplete();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }
}