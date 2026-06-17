package com.stockpilot.backend.identity.usermanagement.listener;

import com.stockpilot.backend.identity.application.service.EmailService;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.usermanagement.entity.InvitationToken;
import com.stockpilot.backend.identity.usermanagement.events.UserInvitedEvent;
import com.stockpilot.backend.identity.usermanagement.repository.InvitationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInvitedEventListener {

    private final InvitationTokenRepository invitationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @EventListener
    @Transactional
    public void handleUserInvitedEvent(UserInvitedEvent event) {

        User user = event.getUser();

        String rawToken = UUID.randomUUID().toString();

        InvitationToken invitationToken = InvitationToken.builder()
                .tenantId(user.getTenantId())
                .userId(user.getId())
                .tokenHash(passwordEncoder.encode(rawToken))
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .used(false)
                .build();

        invitationTokenRepository.save(invitationToken);

        log.info("Generated invitation token for user {}", user.getEmail());

        emailService.sendInvitationEmail(
                user.getEmail(),
                user.getFirstName(),
                rawToken
        );
    }
}