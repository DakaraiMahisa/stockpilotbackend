package com.stockpilot.backend.identity.domain.model;

import com.stockpilot.backend.identity.domain.entity.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserPrincipal implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID tenantId;
    private UUID sessionId;
    private String email;

    @ToString.Exclude
    private String password;

    private Set<String> permissions;

    private boolean enabled;

    private boolean locked;

    public static CurrentUserPrincipal fromUser(
            User user,
            Set<String> permissions
    ) {
        return CurrentUserPrincipal.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .permissions(permissions)
                .enabled(Boolean.TRUE.equals(user.getActive()))
                .locked(Boolean.TRUE.equals(user.getLocked()))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions == null
                ? Set.of()
                : permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

