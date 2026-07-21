package com.stockpilot.backend.shared.validation.validator;

import com.stockpilot.backend.shared.validation.annotation.ValidPasswordExpiryDays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class PasswordExpiryDaysValidator
        implements ConstraintValidator<
                ValidPasswordExpiryDays,
                Integer> {

    @Override
    public boolean isValid(
            Integer value,
            ConstraintValidatorContext context
    ) {

        if (value == null) {
            return true;
        }

        return switch (value) {
            case 0, 30, 60, 90 -> true;
            default -> false;
        };
    }
}
