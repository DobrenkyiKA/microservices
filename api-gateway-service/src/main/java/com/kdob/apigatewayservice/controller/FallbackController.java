package com.kdob.apigatewayservice.controller;

import com.kdob.apigatewayservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Fallback controller to handle circuit breaker fallbacks
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/resource-service")
    @PostMapping("/resource-service")
    public Mono<ErrorResponse> resourceServiceFallback(ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "Resource Service is currently unavailable. Please try again later.",
                exchange.getRequest().getPath().value()
        );
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(errorResponse);
    }

    @GetMapping("/song-service")
    @PostMapping("/song-service")
    public Mono<ErrorResponse> songServiceFallback(ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "Song Service is currently unavailable. Please try again later.",
                exchange.getRequest().getPath().value()
        );
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(errorResponse);
    }

    @GetMapping("/resource-processor")
    @PostMapping("/resource-processor")
    public Mono<ErrorResponse> resourceProcessorFallback(ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "Resource Processor Service is currently unavailable. Please try again later.",
                exchange.getRequest().getPath().value()
        );
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(errorResponse);
    }

    @GetMapping("/default")
    @PostMapping("/default")
    public Mono<ErrorResponse> defaultFallback(ServerWebExchange exchange) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "The requested service is currently unavailable. Please try again later.",
                exchange.getRequest().getPath().value()
        );
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return Mono.just(errorResponse);
    }
}