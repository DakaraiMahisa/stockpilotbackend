package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.entity.VerificationToken;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.domain.repository.VerificationTokenRepository;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import com.stockpilot.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new InvalidCredentialsException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setActive(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
}

