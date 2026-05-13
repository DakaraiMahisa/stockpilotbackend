package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.LoginRequest;
import com.stockpilot.backend.identity.application.dto.RegisterRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.domain.entity.RefreshToken;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.events.LoginSuccessEvent;
import com.stockpilot.backend.identity.domain.model.UserSession;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.shared.exception.AccountDisabledException;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import com.stockpilot.backend.shared.utils.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void register(RegisterRequest registerRequest) {
        // TODO: Implement user registration logic
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {

        UUID tenantId = TenantContext.getTenantId();

        User user = userRepository.findByEmailAndTenantId(request.getEmail(), tenantId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new AccountDisabledException("Your account has been deactivated");
        }

        Set<String> permissions = roleRepository.findPermissionsByRoleId(user.getRole().getId());

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        UserSession userSession = UserSession.fromUser(user, permissions);

        String accessToken = jwtService.generateAccessToken(userSession);
        RefreshToken refreshToken = createAndPersistRefreshToken(user, request.getDeviceInfo());

        eventPublisher.publishEvent(new LoginSuccessEvent(this, userSession));

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private RefreshToken createAndPersistRefreshToken(User user, String deviceInfo) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(OffsetDateTime.now().plusDays(7))
                .deviceInfo(deviceInfo)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
