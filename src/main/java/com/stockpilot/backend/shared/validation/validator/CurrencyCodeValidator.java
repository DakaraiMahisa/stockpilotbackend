package com.stockpilot.backend.shared.validation.validator;


import com.stockpilot.backend.shared.validation.annotation.ValidCurrencyCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyCodeValidator
        implements ConstraintValidator<ValidCurrencyCode, String> {

    private static final Set<String> VALID_CURRENCY_CODES =
            Currency.getAvailableCurrencies()
                    .stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toUnmodifiableSet());

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        /*
         * Null values are considered valid.
         * Use @NotNull when a field is mandatory.
         */
        if (value == null) {
            return true;
        }

        String currencyCode = value.trim().toUpperCase(Locale.ROOT);

        return VALID_CURRENCY_CODES.contains(currencyCode);
    }
}