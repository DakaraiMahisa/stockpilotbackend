package com.stockpilot.backend.identity.application.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendVerificationEmail(
            String to,
            String token
    ) {

        String verificationUrl =
                frontendUrl +
                        "/verify-email?token=" +
                        token;

        Context context = new Context();
        context.setVariable(
                "verificationUrl",
                verificationUrl
        );

        String htmlContent =
                templateEngine.process(
                        "verification-email",
                        context
                );

        try {
            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(to);
            helper.setSubject(
                    "Verify Your StockPilot Account"
            );
            helper.setText(htmlContent, true);
            helper.setFrom(from);

            mailSender.send(message);

            log.info(
                    "Verification email sent to {}",
                    to
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to send verification email to {}",
                    to,
                    ex
            );

            throw new RuntimeException(
                    "Email delivery failed",
                    ex
            );
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token; // This should point to a frontend URL

        Context context = new Context();
        context.setVariable("resetUrl", resetUrl);

        String htmlContent = templateEngine.process("password-reset-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Reset Your StockPilot Password");
            helper.setText(htmlContent, true);
            helper.setFrom(from);

            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to {}", to, ex);
            throw new RuntimeException("Email delivery failed", ex);
        }
    }
}
