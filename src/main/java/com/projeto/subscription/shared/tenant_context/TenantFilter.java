package com.projeto.subscription.shared.tenant_context;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenantIdHeader = request.getHeader(TENANT_HEADER);

        if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
            try {
                UUID tenantId = UUID.fromString(tenantIdHeader);
                TenantContext.setCurrentTenant(tenantId);
            } catch (IllegalArgumentException e) {
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }

    }

}
