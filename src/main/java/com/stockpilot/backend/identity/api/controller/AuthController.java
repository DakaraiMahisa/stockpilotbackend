package com.stockpilot.backend.identity.api.controller;

import com.stockpilot.backend.identity.application.dto.*;
import com.stockpilot.backend.identity.application.service.*;
import com.stockpilot.backend.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Registration successful. Please check your email to verify your account."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenResponse tokenResponse = tokenService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        sessionService.revoke(authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        passwordResetService.initiate(forgotPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset instructions sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        passwordResetService.complete(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password has been reset successfully"));
    }
}

