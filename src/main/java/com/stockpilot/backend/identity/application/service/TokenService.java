package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.RefreshTokenRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.domain.entity.RefreshToken;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.model.UserSession;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidCredentialsException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        if (!user.getActive()) {
            throw new InvalidCredentialsException("User is not active");
        }

        refreshTokenRepository.delete(refreshToken);

        Set<String> permissions = roleRepository.findPermissionsByRoleId(user.getRole().getId());
        UserSession userSession = UserSession.fromUser(user, permissions);

        String newAccessToken = jwtService.generateAccessToken(userSession);
        RefreshToken newRefreshToken = createAndPersistRefreshToken(user, refreshToken.getDeviceInfo());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    private RefreshToken createAndPersistRefreshToken(
            User user,
            String deviceInfo
    ) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByUser(user)
                        .orElse(
                                RefreshToken.builder()
                                        .user(user)
                                        .build()
                        );

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                OffsetDateTime.now().plusDays(7)
        );
        refreshToken.setDeviceInfo(deviceInfo);

        return refreshTokenRepository.save(refreshToken);
    }
}
