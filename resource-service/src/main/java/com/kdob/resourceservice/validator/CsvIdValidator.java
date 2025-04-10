package com.kdob.resourceservice.validator;

import com.kdob.resourceservice.constraint.ValidCsvId;
import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class CsvIdValidator implements ConstraintValidator<ValidCsvId, String> {

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildConstraintViolationMessage(context, "CSV string is blank. At least one ID must be provided");
            return false;
        }

        final String[] ids = value.split(",");
        if (ids.length == 0) {
            buildConstraintViolationMessage(context, "CSV string doesn't contain valid IDs. At least one ID must be provided");
            return false;
        }

        if (value.length() > 200) {
            buildConstraintViolationMessage(context, "CSV string is too long: received " + value.length() + " characters, maximum allowed is 200");
            return false;
        }

        List<String> negativeIds;
        try {
            negativeIds = Arrays.stream(ids).filter(id -> parseInt(id) < 0).toList();
        } catch (final NumberFormatException e) {
            buildConstraintViolationMessage(context, "Invalid ID format: " + value + ". Only positive integers are allowed");
            return false;
        }

        if (!negativeIds.isEmpty()) {
            buildConstraintViolationMessage(context, "Invalid ID format: " + negativeIds + ". Only positive integers are allowed");
            return false;
        } else {
            return true;
        }
    }

    private static void buildConstraintViolationMessage(final ConstraintValidatorContext context, final String value) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(value).addConstraintViolation();
    }
}
