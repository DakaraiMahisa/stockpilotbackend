package com.stockpilot.backend.identity.audits.listeners;

import com.stockpilot.backend.identity.application.service.EmailService;
import com.stockpilot.backend.identity.audits.events.PasswordResetCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetNotificationListener {

    private final EmailService emailService;

    @EventListener
    public void handle(PasswordResetCompletedEvent event) {

        emailService.sendPasswordChangedEmail(
                event.getEmail(),
                event.getFirstName()
        );

        log.info(
                "Password reset notification sent to {}",
                event.getEmail()
        );
    }
}