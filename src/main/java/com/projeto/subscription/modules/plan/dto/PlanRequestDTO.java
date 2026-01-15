package com.projeto.subscription.modules.plan.dto;

import java.math.BigDecimal;

import com.projeto.subscription.shared.util.Enums.BillingCycle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlanRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive BigDecimal price,
        @NotNull BillingCycle billingCycle) {

}
