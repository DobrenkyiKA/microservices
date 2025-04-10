package com.kdob.resourceservice.advice;

import com.kdob.resourceservice.dto.error.ErrorMessageDto;
import com.kdob.resourceservice.exception.NoSuchResourceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final Exception e) {
        return new ErrorMessageDto(e.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final HttpMediaTypeNotSupportedException e) {
        return new ErrorMessageDto(
                "Invalid file format: " + e.getContentType() + ". Only MP3 files are allowed",
                String.valueOf(HttpStatus.BAD_REQUEST.value())
        );
    }

    @ExceptionHandler(NoSuchResourceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDto handleBadRequest(final NoSuchResourceException e) {
        return new ErrorMessageDto(e.getMessage(), String.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final ConstraintViolationException e) {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        String message;
        if (constraintViolation.getPropertyPath().toString().equals("deleteResource.id"))  {
            message = "CSV string is too long: received " + constraintViolation.getInvalidValue().toString().length() + " characters," + constraintViolation.getMessage();
        } else {
            message = "Invalid value " + constraintViolation.getInvalidValue() + " for " + constraintViolation.getPropertyPath() + ". " + constraintViolation.getMessageTemplate();
        }
        return new ErrorMessageDto(message, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}
