package com.kdob.storageservice.advice;

import com.kdob.storageservice.dto.error.ErrorMessageDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final Exception e) {
        return new ErrorMessageDto(e.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final MethodArgumentTypeMismatchException e) {
        return new ErrorMessageDto(e.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final ConstraintViolationException e) {
        return new ErrorMessageDto(e.getConstraintViolations().stream().findAny().orElseThrow().getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}
