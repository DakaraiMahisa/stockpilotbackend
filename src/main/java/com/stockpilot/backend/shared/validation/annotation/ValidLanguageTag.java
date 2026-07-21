package com.stockpilot.backend.shared.validation.annotation;

import com.stockpilot.backend.shared.validation.validator.LanguageTagValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LanguageTagValidator.class)
@Documented
public @interface ValidLanguageTag {

    String message() default "Invalid language tag.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
