package com.stockpilot.backend.identity.api.controller;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.identity.application.dto.*;
import com.stockpilot.backend.identity.application.service.*;
import com.stockpilot.backend.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final PasswordResetService passwordResetService;
    private final VerificationService verificationService;

    @PostMapping("/login/public")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/register/public")
    public ResponseEntity<ApiResponse<Void>> registerOrganization(
            @Valid @RequestBody RegisterOrganizationRequest request) {
        authService.registerOrganization(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Organization and admin user created successfully. Please check your email to verify your account."));
    }


    @PostMapping("/refresh/public")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenResponse tokenResponse = tokenService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Token refreshed successfully"));
    }

    @PostMapping("/logout/public")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        sessionService.revoke(authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/forgot-password/public")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        passwordResetService.initiate(forgotPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset instructions sent to your email"));
    }

    @PostMapping("/reset-password/public")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        passwordResetService.complete(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null, "Password has been reset successfully"));
    }

    @GetMapping("/verify-email/public")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") String token) {
        verificationService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully. You can now log in."));
    }

    @PostMapping("/accept-invitation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvitation(
            @Valid @RequestBody AcceptInvitationRequestDto request
    ) {
        authService.acceptInvitation(request);
    }
}
