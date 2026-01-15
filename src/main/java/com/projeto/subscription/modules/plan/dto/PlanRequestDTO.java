package com.projeto.subscription.modules.plan.dto;

import java.math.BigDecimal;

import com.projeto.subscription.shared.util.Enums.BillingCycle;

import jakarta.validation.constraints.NotBlank;

public record PlanRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank BigDecimal price,
        @NotBlank BillingCycle billingCycle) {

}
