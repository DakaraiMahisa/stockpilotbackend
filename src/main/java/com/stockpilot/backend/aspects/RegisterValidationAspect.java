package com.stockpilot.backend.aspects;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.shared.exception.RegistrationValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterValidationAspect {
    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Before("""
        execution(* com.stockpilot.backend.identity
        .api.controller.AuthController
        .registerOrganization(..))
        """)
    public void validateBeforeRegister(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof RegisterOrganizationRequest request)) {
            return;
        }

        Map<String, String> errors = new HashMap<>();

        // 1. Password Complexity
        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            errors.put("password", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        // 2. Compromised Password Check
        var decision = compromisedPasswordChecker.check(request.getPassword());
        if (decision.isCompromised()) {
            errors.put("password", "This password is known to be compromised. Please choose a different one.");
        }

        if (!errors.isEmpty()) {
            throw new RegistrationValidationException(errors);
        }
        log.info("✅ Registration validation passed");
    }
}
