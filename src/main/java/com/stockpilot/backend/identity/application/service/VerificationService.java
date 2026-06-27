package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.entity.VerificationToken;
import com.stockpilot.backend.identity.domain.enums.VerificationResult;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.domain.repository.VerificationTokenRepository;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import com.stockpilot.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public VerificationResult verifyEmail(String token) {

        VerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Invalid verification token"));

        if (verificationToken.getUsedAt() != null) {
            return VerificationResult.ALREADY_VERIFIED;
        }

        User user = verificationToken.getUser();

        if (user.getEmailVerified()) {
            return VerificationResult.ALREADY_VERIFIED;
        }

        if (verificationToken.isExpired()) {
            throw new InvalidCredentialsException(
                    "Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setActive(true);

        verificationToken.setUsedAt(Instant.now());

        userRepository.save(user);
        tokenRepository.save(verificationToken);

        return VerificationResult.VERIFIED;
    }
}

