package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.ForgotPasswordRequest;
import com.stockpilot.backend.identity.application.dto.ResetPasswordRequest;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    public void initiate(ForgotPasswordRequest forgotPasswordRequest) {
        // TODO: Implement password reset initiation logic
    }

    public void complete(ResetPasswordRequest resetPasswordRequest) {
        // TODO: Implement password reset completion logic
    }
}

