package com.stockpilot.backend.identity.usermanagement.repository;

import com.stockpilot.backend.identity.usermanagement.entity.InvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationTokenRepository extends JpaRepository<InvitationToken, UUID> {

    Optional<InvitationToken> findByTokenHashAndUsedFalse(
            String tokenHash
    );

    Optional<InvitationToken> findByUserIdAndUsedFalse(
            UUID userId
    );

    List<InvitationToken> findByUsedFalseAndExpiresAtAfter(
            Instant now
    );
}
