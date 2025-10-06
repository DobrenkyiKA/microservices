package com.kdob.storageservice.validator;

import com.kdob.storageservice.constraint.PositiveIntegerId;
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
