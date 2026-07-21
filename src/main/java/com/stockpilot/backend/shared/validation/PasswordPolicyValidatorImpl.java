package com.stockpilot.backend.shared.validation;

import com.stockpilot.backend.org.entity.OrgSettings;
import com.stockpilot.backend.org.service.OrgSettingsService;
import com.stockpilot.backend.shared.exception.PasswordPolicyViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordPolicyValidatorImpl
        implements PasswordPolicyValidator {

    private final OrgSettingsService orgSettingsService;

    @Override
    public void validate(
            String rawPassword,
            UUID tenantId
    ) {

        OrgSettings settings =
                orgSettingsService.getSettings(tenantId);

        validateLength(rawPassword, settings);

        validateUppercase(rawPassword, settings);

        validateNumber(rawPassword, settings);

        validateSpecialCharacter(rawPassword, settings);
    }

    private void validateLength(
            String password,
            OrgSettings settings
    ) {

        if (password.length() < settings.getMinPasswordLength()) {
            throw new PasswordPolicyViolationException(
                    "Password must be at least "
                            + settings.getMinPasswordLength()
                            + " characters long."
            );
        }
    }

    private void validateUppercase(
            String password,
            OrgSettings settings
    ) {

        if (!settings.getRequireUppercase()) {
            return;
        }

        boolean hasUppercase = password.chars()
                .anyMatch(Character::isUpperCase);

        if (!hasUppercase) {
            throw new PasswordPolicyViolationException(
                    "Password must contain at least one uppercase letter."
            );
        }
    }

    private void validateNumber(
            String password,
            OrgSettings settings
    ) {

        if (!settings.getRequireNumber()) {
            return;
        }

        boolean hasNumber = password.chars()
                .anyMatch(Character::isDigit);

        if (!hasNumber) {
            throw new PasswordPolicyViolationException(
                    "Password must contain at least one numeric digit."
            );
        }
    }

    private void validateSpecialCharacter(
            String password,
            OrgSettings settings
    ) {

        if (!settings.getRequireSpecialChar()) {
            return;
        }

        boolean hasSpecial = password.chars()
                .anyMatch(ch -> !Character.isLetterOrDigit(ch));

        if (!hasSpecial) {
            throw new PasswordPolicyViolationException(
                    "Password must contain at least one special character."
            );
        }
    }
}
