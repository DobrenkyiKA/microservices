package com.kdob.songservice.advice;

import com.kdob.songservice.error.ErrorMessageDto;
import com.kdob.songservice.error.ValidationErrorMessageDto;
import com.kdob.songservice.exception.AlreadyExistSongMetadataException;
import com.kdob.songservice.exception.NoSuchSongMetadataException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final Exception e) {
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setErrorMessage(e.getMessage());
        errorMessageDto.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return errorMessageDto;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final HttpMediaTypeNotSupportedException e) {
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setErrorMessage("Invalid file format: " + e.getContentType() + ". Only MP3 files are allowed");
        errorMessageDto.setErrorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
    return errorMessageDto;
    }

    @ExceptionHandler(NoSuchSongMetadataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDto handleBadRequest(final NoSuchSongMetadataException e) {
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setErrorMessage(e.getMessage());
        errorMessageDto.setErrorCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
        return errorMessageDto;
    }

    @ExceptionHandler(AlreadyExistSongMetadataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessageDto handleBadRequest(final AlreadyExistSongMetadataException e) {
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();
        errorMessageDto.setErrorMessage(e.getMessage());
        errorMessageDto.setErrorCode(String.valueOf(HttpStatus.CONFLICT.value()));
        return errorMessageDto;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorMessageDto handleBadRequest(final MethodArgumentNotValidException e) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            if (details.put(fieldError.getField(), fieldError.getDefaultMessage()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return new ValidationErrorMessageDto(details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleBadRequest(final ConstraintViolationException e) {
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        String message;
        if (constraintViolation.getPropertyPath().toString().equals("deleteSongsMetadata.id"))  {
            message = "CSV string is too long: received " + constraintViolation.getInvalidValue().toString().length() + " characters," + constraintViolation.getMessage();
        } else {
            message = "Invalid value " + constraintViolation.getInvalidValue() + " for " + constraintViolation.getPropertyPath() + ". " + constraintViolation.getMessageTemplate();
        }
        return new ErrorMessageDto(message, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }
}
