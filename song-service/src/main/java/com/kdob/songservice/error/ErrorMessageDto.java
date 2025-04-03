package com.kdob.songservice.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorMessageDto {
    private String errorMessage;
    private String errorCode;
}
