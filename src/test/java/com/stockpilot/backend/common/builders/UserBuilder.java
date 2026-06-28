package com.stockpilot.backend.common.builders;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

public final class UserBuilder {

    private UUID id = UUID.randomUUID();

    private UUID tenantId = UUID.randomUUID();

    private String email = "user@example.com";

    private String passwordHash = "$2a$10$dummyEncodedPassword";

    private String firstName = "John";

    private String lastName = "Doe";

    private Role role = Role.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .name(RoleName.OWNER)
            .build();

    private Boolean active = true;

    private UserStatus status = UserStatus.ACTIVE;

    private Boolean locked = false;

    private Integer failedLoginAttempts = 0;

    private Boolean emailVerified = false;

    private Boolean mfaEnabled = false;

    private Instant invitedAt;

    private Instant lastLoginAt;

    private Instant lockedAt;

    /*
     * BaseEntity fields
     */

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private Long version = 0L;

    private boolean deleted = false;

    private UserBuilder() {
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public UserBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder tenantId(UUID tenantId) {
        this.tenantId = tenantId;

        if (role != null) {
            role.setTenantId(tenantId);
        }

        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public UserBuilder active(Boolean active) {
        this.active = active;
        return this;
    }

    public UserBuilder status(UserStatus status) {
        this.status = status;
        return this;
    }

    public UserBuilder locked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public UserBuilder failedLoginAttempts(Integer attempts) {
        this.failedLoginAttempts = attempts;
        return this;
    }

    public UserBuilder emailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
        return this;
    }

    public UserBuilder mfaEnabled(Boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
        return this;
    }

    public UserBuilder invitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
        return this;
    }

    public UserBuilder lastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public UserBuilder lockedAt(Instant lockedAt) {
        this.lockedAt = lockedAt;
        return this;
    }

    public UserBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserBuilder version(Long version) {
        this.version = version;
        return this;
    }

    public UserBuilder deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public User build() {

        return User.builder()
                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .deleted(deleted)
                .tenantId(tenantId)

                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .active(active)
                .status(status)
                .locked(locked)
                .failedLoginAttempts(failedLoginAttempts)
                .emailVerified(emailVerified)
                .mfaEnabled(mfaEnabled)
                .invitedAt(invitedAt)
                .lastLoginAt(lastLoginAt)
                .lockedAt(lockedAt)
                .build();
    }
}
