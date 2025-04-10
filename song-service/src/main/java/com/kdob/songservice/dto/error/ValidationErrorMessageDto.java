package com.kdob.songservice.dto.error;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidationErrorMessageDto extends ErrorMessageDto {
    private Map<String, String> details;

    public ValidationErrorMessageDto (Map<String, String> details) {
        super("Validation error", "400");
        this.details = details;
    }
}
