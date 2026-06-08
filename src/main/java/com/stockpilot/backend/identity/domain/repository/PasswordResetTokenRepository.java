package com.stockpilot.backend.identity.domain.repository;

import com.stockpilot.backend.identity.domain.entity.PasswordResetToken;
import com.stockpilot.backend.identity.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);
}

