package com.stockpilot.backend.shared.validation.validator;


import com.stockpilot.backend.shared.validation.annotation.ValidFiscalYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class FiscalYearValidator
        implements ConstraintValidator<ValidFiscalYear, String> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

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
            MonthDay.parse(value.trim(), FORMATTER);
            return true;
        } catch (DateTimeException ex) {
            return false;
        }
    }
}