package com.stockpilot.backend.identity.api.controller;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.identity.application.dto.*;
import com.stockpilot.backend.identity.application.service.*;
import com.stockpilot.backend.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final PasswordResetService passwordResetService;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @PostMapping("/login/public")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/register/public")
    public ResponseEntity<ApiResponse<Void>> registerOrganization(
            @Valid @RequestBody RegisterOrganizationRequest request) {

        CompromisedPasswordDecision decision =
                compromisedPasswordChecker.check(request.getPassword());

        if (decision.isCompromised()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Choose a stronger password; this one is known to be compromised."
                    ));
        }

        authService.registerOrganization(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Organization registered successfully. Please check your email to verify your account."
                )
        );
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
}
