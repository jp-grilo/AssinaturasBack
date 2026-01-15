package com.projeto.subscription.modules.plan.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.projeto.subscription.shared.util.Enums.BillingCycle;

public record PlanResponseDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        BillingCycle billingCycle) {
}
