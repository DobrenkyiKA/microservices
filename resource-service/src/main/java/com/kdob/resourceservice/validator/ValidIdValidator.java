package com.kdob.resourceservice.validator;

import com.kdob.resourceservice.constraint.PositiveIntegerId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static java.lang.Integer.parseInt;


public class ValidIdValidator implements ConstraintValidator<PositiveIntegerId, String> {

    @Override
    public boolean isValid(final String idValue, final ConstraintValidatorContext context) {
        try {
            return parseInt(idValue) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
