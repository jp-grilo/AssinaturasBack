package com.projeto.subscription.modules.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projeto.subscription.modules.tenant.model.Tenant;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID>{
    
}
