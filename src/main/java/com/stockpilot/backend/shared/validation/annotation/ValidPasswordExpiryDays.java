package com.stockpilot.backend.shared.validation.annotation;

import com.stockpilot.backend.shared.validation.validator.PasswordExpiryDaysValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordExpiryDaysValidator.class)
@Documented
public @interface ValidPasswordExpiryDays {

    String message()
            default "Password expiry must be one of: 0, 30, 60 or 90.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
