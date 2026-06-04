package com.stockpilot.backend.identity.domain.repository;

import com.stockpilot.backend.identity.domain.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByIdAndDeletedFalse(UUID id);

    Optional<User> findByEmailAndTenantId(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email, UUID tenantId);

    boolean existsByEmailAndTenantId(@NotBlank @Email String email, UUID tenantId);
}

