package com.kdob.resourceservice.validator;

import com.kdob.resourceservice.constraint.PositiveIntegerId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ValidIdValidator implements ConstraintValidator<PositiveIntegerId, Long> {

    @Override
    public boolean isValid(final Long idValue, final ConstraintValidatorContext context) {
        try {
            return idValue > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
