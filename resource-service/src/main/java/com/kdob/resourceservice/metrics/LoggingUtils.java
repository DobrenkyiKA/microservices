package com.kdob.resourceservice.metrics;

import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Map;

public class LoggingUtils {
    public static void logWithContext(final Logger logger, final String message, final Map<String, String> context) {
        context.forEach(MDC::put);
        try {
            logger.info(message, StructuredArguments.entries(context));
        } finally {
            context.keySet().forEach(MDC::remove);
        }
    }

    public static void logError(final Logger logger, final String message, final Throwable throwable, final Map<String, Object> additionalInfo) {
        logger.error(message,
                StructuredArguments.entries(additionalInfo),
                StructuredArguments.keyValue("error_message", throwable.getMessage()),
                StructuredArguments.keyValue("error_type", throwable.getClass().getSimpleName()),
                throwable
        );
    }
}