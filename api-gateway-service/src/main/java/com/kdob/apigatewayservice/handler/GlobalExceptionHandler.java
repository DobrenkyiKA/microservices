package com.kdob.apigatewayservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdob.apigatewayservice.model.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

/**
 * Global exception handler for API Gateway errors
 */
@Component
@Order(-1) // High priority to handle exceptions before default handlers
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = createErrorResponse(ex, exchange.getRequest().getPath().value());
        
        response.setStatusCode(HttpStatus.valueOf(errorResponse.getStatus()));

        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer buffer;
        
        try {
            String errorJson = objectMapper.writeValueAsString(errorResponse);
            buffer = bufferFactory.wrap(errorJson.getBytes());
        } catch (JsonProcessingException e) {
            String fallbackError = "{\"error\":\"Internal Server Error\",\"message\":\"Unable to process error response\"}";
            buffer = bufferFactory.wrap(fallbackError.getBytes());
        }

        return response.writeWith(Mono.just(buffer));
    }

    private ErrorResponse createErrorResponse(Throwable ex, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(path);

        if (ex instanceof NotFoundException) {
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("Route Not Found");
            errorResponse.setMessage("The requested route is not available. Please check the URL and try again.");
            
        } else if (ex instanceof ConnectException) {
            errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            errorResponse.setError("Service Unavailable");
            errorResponse.setMessage("The requested service is currently unavailable. Please try again later.");
            
        } else if (ex instanceof TimeoutException) {
            errorResponse.setStatus(HttpStatus.GATEWAY_TIMEOUT.value());
            errorResponse.setError("Gateway Timeout");
            errorResponse.setMessage("The service took too long to respond. Please try again later.");
            
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException rsEx = (ResponseStatusException) ex;
            errorResponse.setStatus(rsEx.getStatusCode().value());
            errorResponse.setError(HttpStatus.valueOf(rsEx.getStatusCode().value()).getReasonPhrase());
            errorResponse.setMessage(rsEx.getReason() != null ? rsEx.getReason() : "An error occurred while processing your request.");
            
        } else if (ex.getCause() instanceof ConnectException) {
            errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            errorResponse.setError("Service Unavailable");
            errorResponse.setMessage("Unable to connect to the requested service. Please try again later.");
            
        } else {
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError("Internal Server Error");
            errorResponse.setMessage("An unexpected error occurred. Please try again later.");
        }

        return errorResponse;
    }
}