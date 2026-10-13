package com.stockpilot.backend.identity.infrastructure.security.jwt;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.exception.InvalidTokenException;
import com.stockpilot.backend.identity.infrastructure.security.handler.CustomAuthenticationEntryPoint;
import com.stockpilot.backend.identity.infrastructure.security.permission.PermissionResolutionService;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.shared.utils.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PermissionResolutionService permissionResolutionService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.validateToken(token)) {
            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);
            return;
        }

        try {
            UUID userId = jwtService.extractUserId(token);
            UUID tenantId = jwtService.extractTenantId(token);
            UUID sessionId = jwtService.extractSessionId(token);

            UserSession session = userSessionRepository
                    .findByIdAndUserIdAndTenantId(sessionId, userId, tenantId)
                    .filter(s -> !s.isRevoked())
                    .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                    .orElseThrow(() ->
                            new InvalidTokenException("Session is invalid or expired")
                    );

            TenantContext.setTenantId(tenantId);

            User user = userRepository.findByIdAndTenantId(userId, tenantId)
                    .filter(User::getActive)
                    .orElseThrow(() ->
                            new InvalidTokenException("User is not available")
                    );

            if (user.isDeleted()) {
                throw new InvalidTokenException("User is not available");
            }

            Set<String> permissions =
                    permissionResolutionService.resolveForRole(
                            tenantId,
                            user.getRole().getId()
                    );

            CurrentUserPrincipal principal =
                    CurrentUserPrincipal.fromUser(user, permissions);

            principal.setSessionId(jwtService.extractSessionId(token));

            TenantContext.setTenantId(principal.getTenantId());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (InvalidTokenException ex) {
            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException(
                            "Invalid authentication token",
                            ex
                    )
            );

        } finally {
            TenantContext.clear();
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}

