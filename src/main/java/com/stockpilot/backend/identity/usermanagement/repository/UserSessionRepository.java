package com.stockpilot.backend.identity.usermanagement.repository;

import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    List<UserSession> findByUserIdAndTenantIdAndRevokedFalse(
            UUID userId,
            UUID tenantId
    );

    Optional<UserSession> findByIdAndUserIdAndTenantId(
            UUID sessionId,
            UUID userId,
            UUID tenantId
    );

    Optional<UserSession> findByRefreshTokenHashAndRevokedFalse(
            String refreshTokenHash
    );

    @Modifying
    @Query("""
            update UserSession s
               set s.revoked = true,
                   s.revokedAt = :revokedAt
             where s.userId = :userId
               and s.tenantId = :tenantId
               and s.revoked = false
            """)
    int revokeAllUserSessions(
            UUID userId,
            UUID tenantId,
            OffsetDateTime revokedAt
    );

    List<UserSession> findByUserIdAndTenantIdAndRevokedFalseAndExpiresAtAfter(
            UUID userId,
            UUID tenantId,
            Instant now
    );

}
