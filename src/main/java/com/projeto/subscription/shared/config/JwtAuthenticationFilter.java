package com.projeto.subscription.shared.config;

import com.projeto.subscription.modules.identity.repository.UserRepository;
import com.projeto.subscription.shared.tenant_context.TenantContext;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recoveryToken(request);

        try {
            Claims claims = tokenService.getClaims(token);

            if (claims != null) {

                String login = claims.getSubject();
                String tenantId = claims.get("tenantId", String.class);

                if (tenantId != null) {
                    TenantContext.setCurrentTenant(UUID.fromString(tenantId));
                }

                userRepository.findByEmail(login).ifPresent(user -> {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }

    }

    private String recoveryToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}