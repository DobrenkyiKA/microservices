package com.kdob.resourceservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorMessageDto {
    private String errorMessage;
    private String errorCode;
}
