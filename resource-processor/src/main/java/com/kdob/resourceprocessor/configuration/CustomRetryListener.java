package com.kdob.resourceprocessor.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomRetryListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        String methodName = (String) context.getAttribute(RetryContext.NAME);
        log.debug("Starting retry for method: {}", methodName);
        return true;
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        String methodName = (String) context.getAttribute(RetryContext.NAME);
        int retryCount = context.getRetryCount();
        
        if (retryCount == 1) {
            // First retry - log with info level
            log.info("First retry attempt for method: {}. Error: {}", methodName, throwable.getMessage());
        } else {
            // Subsequent retries - log with warn level
            log.warn("Retry attempt #{} for method: {}. Error: {}", retryCount, methodName, throwable.getMessage());
        }
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        String methodName = (String) context.getAttribute(RetryContext.NAME);
        int retryCount = context.getRetryCount();
        
        if (throwable != null) {
            log.error("All retry attempts ({}) failed for method: {}. Final error: {}", retryCount, methodName, throwable.getMessage());
        } else {
            if (retryCount > 0) {
                log.info("Method: {} succeeded after {} retry attempts", methodName, retryCount);
            }
        }
    }
}