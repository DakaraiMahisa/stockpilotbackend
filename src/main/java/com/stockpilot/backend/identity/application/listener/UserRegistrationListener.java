package com.stockpilot.backend.identity.application.listener;

import com.stockpilot.backend.identity.application.service.EmailService;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.entity.VerificationToken;
import com.stockpilot.backend.identity.domain.events.UserRegisteredEvent;
import com.stockpilot.backend.identity.domain.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationListener {

    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    @EventListener
    @Transactional
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(OffsetDateTime.now().plusHours(24))
                .build();

        tokenRepository.save(verificationToken);
        log.info("Generated verification token for user {}", user.getEmail());

        emailService.sendVerificationEmail(user.getEmail(), token);
    }
}

