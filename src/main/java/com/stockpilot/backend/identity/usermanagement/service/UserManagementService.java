package com.stockpilot.backend.identity.usermanagement.service;

import com.stockpilot.backend.identity.audits.annotations.Auditable;
import com.stockpilot.backend.identity.audits.context.AuditMetadataContext;
import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;
import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.usermanagement.dto.*;
import com.stockpilot.backend.identity.usermanagement.entity.InvitationToken;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;
import com.stockpilot.backend.identity.usermanagement.events.RoleChangedEvent;
import com.stockpilot.backend.identity.usermanagement.events.UserInvitedEvent;
import com.stockpilot.backend.identity.usermanagement.repository.InvitationTokenRepository;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.identity.usermanagement.specifications.UserSpecifications;
import com.stockpilot.backend.shared.exception.*;
import com.stockpilot.backend.shared.utils.CurrentUserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final RoleRepository roleRepository;
    private final InvitationTokenRepository invitationTokenRepository;
    private final CurrentUserContext currentUserContext;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public Page<UserSummaryDto> listUsers(
            UUID roleId,
            Boolean active,
            Pageable pageable
    ) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        Specification<User> specification =
                Specification.allOf(
                        UserSpecifications.belongsToTenant(tenantId),
                        UserSpecifications.notDeleted(),
                        UserSpecifications.hasRole(roleId),
                        UserSpecifications.isActive(active)
                );

        return userRepository.findAll(specification, pageable)
                .map(this::toDto);
    }

    private UserSummaryDto toDto(User user) {

        return new UserSummaryDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName().name(),
                user.getStatus(),
                user.getActive(),
                user.getLastLoginAt()
        );
    }

    @Transactional(readOnly = true)
    public UserDetailsDto getUser(UUID userId) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"
                ));

        Set<String> permissions = user.getRole()
                .getPermissions()
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toCollection(TreeSet::new));

        return new UserDetailsDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().getName().name(),
                permissions,
                user.getStatus(),
                user.getActive(),
                user.getEmailVerified(),
                user.getMfaEnabled(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<UserSessionDto> getUserSessions(UUID userId) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userSessionRepository
                .findByUserIdAndTenantIdAndRevokedFalseAndExpiresAtAfter(
                        userId,
                        tenantId,
                        Instant.now()
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    private UserSessionDto toDto(UserSession session) {

        return new UserSessionDto(
                session.getId(),
                session.getIpAddress(),
                session.getUserAgent(),
                session.getLastUsedAt(),
                session.getExpiresAt()
        );
    }

    @Transactional
    @Auditable(
            action = AuditAction.SESSION_REVOKED,
            severity = AuditSeverity.WARNING,
            target = AuditTargetEntity.SESSION
    )
    public void revokeSession(UUID userId, UUID sessionId) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserSession session = userSessionRepository
                .findByIdAndUserIdAndTenantId(
                        sessionId,
                        userId,
                        tenantId
                )
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (session.isRevoked()) {
            return;
        }

        session.setRevoked(true);
        session.setRevokedAt(Instant.now());

        userSessionRepository.save(session);
    }

    @Transactional
    @Auditable(
            action = AuditAction.USER_DEACTIVATED,
            severity = AuditSeverity.WARNING,
            target = AuditTargetEntity.USER
    )
    public void deactivateUser(UUID userId) {

        UUID tenantId = currentUserContext.getCurrentTenantId();
        UUID currentUserId = currentUserContext.getCurrentUserId();

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getId().equals(currentUserId)) {
            throw new SelfDeactivationNotAllowedException();
        }

        user.setActive(false);
        user.setStatus(UserStatus.DEACTIVATED);

        List<UserSession> sessions =
                userSessionRepository.findByUserIdAndTenantIdAndRevokedFalse(
                        userId,
                        tenantId
                );

        Instant now = Instant.now();

        sessions.forEach(session -> {
            session.setRevoked(true);
            session.setRevokedAt(now);
        });

        userSessionRepository.saveAll(sessions);

        userRepository.save(user);
    }

    @Transactional
    @Auditable(
            action = AuditAction.USER_ACTIVATED,
            severity = AuditSeverity.WARNING,
            target = AuditTargetEntity.USER
    )
    public void activateUser(UUID userId) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getActive())) {
            throw new UserAlreadyActiveException();
        }

        user.setActive(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    @Transactional
    @Auditable(
            action = AuditAction.USER_INVITED,
            severity = AuditSeverity.WARNING,
            target = AuditTargetEntity.USER
    )
    public void inviteUser(InviteUserRequestDto request) {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        if (userRepository.existsByEmailAndTenantId(
                request.email(),
                tenantId
        )) {
            throw new DuplicateResourceException("User already exists");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Role not found"));

        if (role.getName() == RoleName.OWNER) {
            throw new BusinessRuleException(
                    "OWNER role cannot be assigned"
            );
        }

        User user = User.builder()
                .tenantId(tenantId)
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .passwordHash(
                        passwordEncoder.encode(
                                UUID.randomUUID().toString()
                        )
                )
                .role(role)
                .status(UserStatus.INVITED)
                .active(false)
                .emailVerified(false)
                .mfaEnabled(false)
                .build();

        user = userRepository.save(user);

        GeneratedInvitationToken generated =
                createInvitationToken(user);

        invitationTokenRepository.save(generated.entity());

        eventPublisher.publishEvent(
                new UserInvitedEvent(user)
        );
    }

    private GeneratedInvitationToken createInvitationToken(User user) {

        String rawToken = UUID.randomUUID().toString();

        InvitationToken token = new InvitationToken();

        token.setTenantId(user.getTenantId());
        token.setUserId(user.getId());
        token.setTokenHash(passwordEncoder.encode(rawToken));
        token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

        return new GeneratedInvitationToken(rawToken, token);
    }

    @Transactional
    @Auditable(
            action = AuditAction.ROLE_CHANGED,
            severity = AuditSeverity.CRITICAL,
            target = AuditTargetEntity.USER
    )
    public void changeUserRole(
            UUID userId,
            ChangeUserRoleRequestDto request
    ) {

        UUID tenantId = currentUserContext.getCurrentTenantId();
        UUID currentUserId = currentUserContext.getCurrentUserId();

        User user = userRepository.findByIdAndTenantId(
                userId,
                tenantId
        ).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        if (user.getId().equals(currentUserId)) {
            throw new SelfRoleChangeNotAllowedException();
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Role not found"));

        if (role.getName() == RoleName.OWNER) {
            throw new BusinessRuleException(
                    "OWNER role cannot be assigned"
            );
        }

        if (user.getRole().getId().equals(role.getId())) {
            throw new UserAlreadyHasRoleException();
        }

        Role previousRole = user.getRole();

        user.setRole(role);
        userRepository.save(user);

        AuditMetadataContext.putAll(
                Map.of(
                        "targetId", user.getId(),
                        "previousRole", previousRole.getName().name(),
                        "newRole", role.getName().name()
                )
        );

        List<UserSession> sessions =
                userSessionRepository.findByUserIdAndTenantIdAndRevokedFalse(
                        userId,
                        tenantId
                );

        Instant now = Instant.now();

        sessions.forEach(session -> {
            session.setRevoked(true);
            session.setRevokedAt(now);
        });

        userSessionRepository.saveAll(sessions);

    }
}
