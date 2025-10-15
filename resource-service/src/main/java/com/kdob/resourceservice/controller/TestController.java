package com.kdob.resourceservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/test/logs")
    public String testLogs() {
        log.trace("This is a TRACE log");
        log.debug("This is a DEBUG log");
        log.info("This is an INFO log");
        log.warn("This is a WARN log");
        log.error("This is an ERROR log");

        return "Logs generated successfully";
    }

    @GetMapping("/test/error")
    public String testError() {
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            log.error("Error occurred during test", e);
            throw e;
        }
    }
}