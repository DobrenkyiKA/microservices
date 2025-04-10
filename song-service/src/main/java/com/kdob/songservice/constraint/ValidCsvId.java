package com.kdob.songservice.constraint;

import com.kdob.songservice.validator.CsvIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = CsvIdValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidCsvId {
    String message() default "Invalid CSV ID. Ensure it follows the specified format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
