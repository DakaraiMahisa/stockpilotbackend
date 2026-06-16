package com.stockpilot.backend.identity.infrastructure.security.jwt;

import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.shared.utils.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && jwtService.validateToken(token)) {
                CurrentUserPrincipal session = jwtService.extractUserSession(token);

                TenantContext.setTenantId(session.getTenantId());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        session, null, session.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
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


    private void setSecurityContext(String token) {
        String email = jwtService.extractEmail(token);
        List<String> permissions = jwtService.extractPermissions(token);
        var tenantId = jwtService.extractTenantId(token);

        if (email != null && tenantId != null) {
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority("ROLE_" + permission))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            authentication.setDetails(java.util.Map.of(
                    "tenantId", tenantId.toString(),
                    "email", email
            ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set tenant-aware authentication for user: {} in tenant: {} with permissions: {}", 
                    email, tenantId, permissions);
        } else {
            log.warn("Failed to extract email or tenantId from JWT token");
        }
    }
}

