package com.stockpilot.backend.shared.validation.validator;


import com.stockpilot.backend.shared.validation.annotation.ValidTimezone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class TimezoneValidator
        implements ConstraintValidator<ValidTimezone, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        /*
         * Null values are considered valid.
         * Use @NotNull when the field is mandatory.
         */
        if (value == null) {
            return true;
        }

        try {
            ZoneId.of(value.trim());
            return true;
        } catch (DateTimeException ex) {
            return false;
        }
    }
}