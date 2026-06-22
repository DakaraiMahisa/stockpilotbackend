package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.identity.application.dto.AcceptInvitationRequestDto;
import com.stockpilot.backend.identity.application.dto.LoginRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.events.InvitationAcceptedEvent;
import com.stockpilot.backend.identity.audits.events.LoginFailedEvent;
import com.stockpilot.backend.identity.domain.entity.RefreshToken;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.audits.events.UserRegisteredEvent;
import com.stockpilot.backend.identity.usermanagement.entity.InvitationToken;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;
import com.stockpilot.backend.identity.usermanagement.repository.InvitationTokenRepository;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.shared.exception.*;
import com.stockpilot.backend.tenant.domain.entity.Tenant;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.audits.events.LoginSuccessEvent;
import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.tenant.domain.repository.TenantRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.tenant.service.TenantCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleProvisioningService roleProvisioningService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final InvitationTokenRepository invitationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TenantCodeGenerator tenantCodeGenerator;
    private final RequestAuditContext requestContext;

    @Transactional
    public void registerOrganization(RegisterOrganizationRequest request) {

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User with email " + request.getEmail() + " already exists."
            );
        }

        // Generate tenant code
        String baseCode =
                tenantCodeGenerator.generate(
                        request.getOrganizationName()
                );

        String tenantCode = baseCode;
        int counter = 1;

        while (tenantRepository.existsByCode(tenantCode)) {
            tenantCode = baseCode + "-" + counter++;
        }

        // Create tenant
        Tenant tenant = Tenant.builder()
                .name(request.getOrganizationName())
                .code(tenantCode)
                .timezone(
                        request.getTimezone() != null
                                ? request.getTimezone()
                                : "Asia/Kolkata"
                )
                .currencyCode(
                        request.getCurrencyCode() != null
                                ? request.getCurrencyCode()
                                : "INR"
                )
                .active(true)
                .build();

        tenant = tenantRepository.save(tenant);

        // Create default tenant roles
        roleProvisioningService.provisionDefaultRoles(
                tenant.getId()
        );

        // Fetch OWNER role
        Role ownerRole = roleRepository
                .findByNameAndTenantId(
                        RoleName.OWNER,
                        tenant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "OWNER role not found for tenant."
                        )
                );

        // Create owner user
        User ownerUser = User.builder()
                .tenantId(tenant.getId())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .role(ownerRole)
                .active(false)
                .emailVerified(false)
                .mfaEnabled(false)
                .build();

        userRepository.save(ownerUser);

        // Publish UserRegisteredEvent
        eventPublisher.publishEvent(new UserRegisteredEvent(this, ownerUser));

        log.info(
                "Organization '{}' registered successfully with tenantId={}",
                tenant.getName(),
                tenant.getId()
        );
    }
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Tenant tenant = tenantRepository.findByCode(request.getTenantCode())
                .orElseThrow(() -> {

                    eventPublisher.publishEvent(
                            new LoginFailedEvent(
                                    request.getTenantCode(),
                                    request.getEmail(),
                                    "INVALID_TENANT",
                                    request.getDeviceInfo(),
                                    requestContext.getClientIp()
                            )
                    );

                    return new InvalidCredentialsException(
                            "Invalid credentials"
                    );
                });
        User user = userRepository.findByEmailAndTenantId(
                        request.getEmail(),
                        tenant.getId()
                )
                .orElseThrow(() ->
                {

                    eventPublisher.publishEvent(
                            new LoginFailedEvent(
                                    request.getTenantCode(),
                                    request.getEmail(),
                                    "USER_NOT_FOUND",
                                    request.getDeviceInfo(),
                                    requestContext.getClientIp()
                            )
                    );

                    return new InvalidCredentialsException(
                            "Invalid credentials"
                    );
                });
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {

            eventPublisher.publishEvent(
                    new LoginFailedEvent(
                            request.getTenantCode(),
                            request.getEmail(),
                            "INVALID_PASSWORD",
                            request.getDeviceInfo(),
                            requestContext.getClientIp()
                    )
            );

            throw new InvalidCredentialsException(
                    "Invalid password"
            );
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            eventPublisher.publishEvent(
                    new LoginFailedEvent(
                            request.getTenantCode(),
                            request.getEmail(),
                            "ACCOUNT_DISABLED",
                            request.getDeviceInfo(),
                            requestContext.getClientIp()
                    )
            );

            throw new AccountDisabledException(
                    "Your account has been deactivated. Please verify your email first."
            );
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            eventPublisher.publishEvent(
                    new LoginFailedEvent(
                            request.getTenantCode(),
                            request.getEmail(),
                            "EMAIL_NOT_VERIFIED",
                            request.getDeviceInfo(),
                            requestContext.getClientIp()
                    )
            );

            throw new AccountDisabledException(
                    "Please verify your email before logging in."
            );
        }

        Set<String> permissions = roleRepository.findPermissionsByRoleId(user.getRole().getId());

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        CurrentUserPrincipal currentUserPrincipal = CurrentUserPrincipal.fromUser(user, permissions);

        RefreshToken refreshToken = createAndPersistRefreshToken(user, request.getDeviceInfo());
        UserSession session = persistUserSession(
                user,
                refreshToken,
                request
        );
        refreshToken.setSessionId(session.getId());
        String accessToken = jwtService.generateAccessToken(
                currentUserPrincipal,
                session.getId()
        );


        eventPublisher.publishEvent(
                new LoginSuccessEvent(
                        currentUserPrincipal,
                        request.getDeviceInfo()
                )
        );
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private RefreshToken createAndPersistRefreshToken(
            User user,
            String deviceInfo) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByUser(user)
                        .orElseGet(() ->
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

    private UserSession persistUserSession(
            User user,
            RefreshToken refreshToken,
            LoginRequest request
    ) {

        UserSession session = new UserSession();

        session.setTenantId(user.getTenantId());

        session.setUserId(user.getId());

        session.setRefreshTokenHash(
                passwordEncoder.encode(refreshToken.getToken())
        );

        session.setUserAgent(request.getDeviceInfo());

        session.setLastUsedAt(Instant.now());

        session.setExpiresAt(
                refreshToken.getExpiryDate().toInstant()
        );

        session.setRevoked(false);

       return userSessionRepository.save(session);
    }

    @Transactional
    public void acceptInvitation(
            AcceptInvitationRequestDto request
    ) {

        InvitationToken invitationToken =
                invitationTokenRepository
                        .findByUsedFalseAndExpiresAtAfter(Instant.now())
                        .stream()
                        .filter(token -> passwordEncoder.matches(
                                request.token(),
                                token.getTokenHash()
                        ))
                        .findFirst()
                        .orElseThrow(InvalidInvitationTokenException::new);

        User user = userRepository.findById(invitationToken.getUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.INVITED) {
            throw new BusinessRuleException(
                    "User invitation is no longer valid"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setEmailVerified(true);
        user.setActive(true);
        user.setStatus(UserStatus.ACTIVE);

        invitationToken.setUsed(true);
        invitationToken.setUsedAt(Instant.now());

        userRepository.save(user);
        invitationTokenRepository.save(invitationToken);
        eventPublisher.publishEvent(
                new InvitationAcceptedEvent(
                        user.getId(),
                        user.getTenantId(),
                        user.getEmail(),
                        requestContext.getClientIp(),
                        requestContext.getUserAgent()
                )
        );
    }
}
