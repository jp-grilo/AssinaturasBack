package com.projeto.subscription.shared.tenant_context;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    public static UUID getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static void clear() {
        currentTenant.remove();
    }
}
