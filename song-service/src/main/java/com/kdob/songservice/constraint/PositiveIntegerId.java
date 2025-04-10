package com.kdob.songservice.constraint;

import com.kdob.songservice.validator.ValidIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidIdValidator.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface PositiveIntegerId {
    String message() default "Invalid value '${validatedValue}' for ID. Must be a positive integer";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
