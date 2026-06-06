package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.identity.application.dto.LoginRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.domain.entity.RefreshToken;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.tenant.domain.entity.Tenant;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.events.LoginSuccessEvent;
import com.stockpilot.backend.identity.domain.model.UserSession;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.tenant.domain.repository.TenantRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.shared.exception.AccountDisabledException;
import com.stockpilot.backend.shared.exception.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import com.stockpilot.backend.shared.exception.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.TenantContext;
import com.stockpilot.backend.tenant.service.TenantCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher eventPublisher;
    private final TenantCodeGenerator tenantCodeGenerator;

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
                .active(true)
                .emailVerified(false)
                .mfaEnabled(false)
                .build();

        userRepository.save(ownerUser);

        // TODO:
        // branchService.createDefaultBranch(tenant.getId());

        // TODO:
        // publish UserRegisteredEvent

        log.info(
                "Organization '{}' registered successfully with tenantId={}",
                tenant.getName(),
                tenant.getId()
        );
    }
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Tenant tenant = tenantRepository.findByCode(request.getTenantCode())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid credentials"));
        User user = userRepository.findByEmailAndTenantId(
                        request.getEmail(),
                        tenant.getId()
                )
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
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
