package com.projeto.subscription.shared.tenant_context;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantAspect {

    private final EntityManager entityManager;

    public TenantAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("execution(* com.projeto.subscription..*Repository.*(..))")
    public void enableTenantFilter() {
        if (TenantContext.getCurrentTenant() != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", TenantContext.getCurrentTenant());
        }
    }
}