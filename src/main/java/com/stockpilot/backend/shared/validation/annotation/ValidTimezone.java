package com.stockpilot.backend.shared.validation.annotation;


import com.stockpilot.backend.shared.validation.validator.TimezoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimezoneValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimezone {

    String message() default "Invalid IANA timezone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}