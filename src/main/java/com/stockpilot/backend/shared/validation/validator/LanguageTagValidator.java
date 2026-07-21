package com.stockpilot.backend.shared.validation.validator;

import com.stockpilot.backend.shared.validation.annotation.ValidLanguageTag;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;

public class LanguageTagValidator
        implements ConstraintValidator<ValidLanguageTag, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null || value.isBlank()) {
            return true;
        }

        return value.equals(Locale.forLanguageTag(value).toLanguageTag());
    }
}
