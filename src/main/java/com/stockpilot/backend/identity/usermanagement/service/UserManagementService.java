package com.stockpilot.backend.identity.usermanagement.service;

import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.usermanagement.dto.UserDetailsDto;
import com.stockpilot.backend.identity.usermanagement.dto.UserSessionDto;
import com.stockpilot.backend.identity.usermanagement.dto.UserSummaryDto;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.identity.usermanagement.specifications.UserSpecifications;
import com.stockpilot.backend.shared.exception.SelfDeactivationNotAllowedException;
import com.stockpilot.backend.shared.exception.UserAlreadyActiveException;
import com.stockpilot.backend.shared.utils.CurrentUserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManagementService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final CurrentUserContext currentUserContext;

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
}
