package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.ForgotPasswordRequest;
import com.stockpilot.backend.identity.application.dto.ResetPasswordRequest;
import com.stockpilot.backend.identity.domain.entity.PasswordResetToken;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.repository.PasswordResetTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void initiate(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail().trim().toLowerCase();
        userRepository.findByEmail(email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(OffsetDateTime.now().plusHours(1))
                    .build();
            passwordResetTokenRepository.save(passwordResetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        });
    }

    @Transactional
    public void complete(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid password reset token"));

        if (passwordResetToken.isUsed()) {
            throw new InvalidCredentialsException("Password reset token has already been used");
        }

        if (passwordResetToken.getExpiryDate().isBefore(OffsetDateTime.now())) {
            throw new InvalidCredentialsException("Password reset token has expired");
        }

        User user = passwordResetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        refreshTokenRepository.deleteAllByUserId(user.getId());
    }
}
