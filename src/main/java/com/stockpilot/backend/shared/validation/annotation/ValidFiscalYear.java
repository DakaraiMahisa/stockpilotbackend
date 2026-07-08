package com.stockpilot.backend.shared.validation.annotation;


import com.stockpilot.backend.shared.validation.validator.FiscalYearValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FiscalYearValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFiscalYear {

    String message() default "Fiscal year start must be a valid MM-DD value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}