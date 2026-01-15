package com.projeto.subscription.modules.plan.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projeto.subscription.modules.plan.dto.PlanRequestDTO;
import com.projeto.subscription.modules.plan.dto.PlanResponseDTO;
import com.projeto.subscription.modules.plan.model.Plan;
import com.projeto.subscription.modules.plan.repository.PlanRepository;
import com.projeto.subscription.shared.tenant_context.TenantContext;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public PlanResponseDTO create(PlanRequestDTO planDTO) {

        Plan plan = Plan.builder()
                .name(planDTO.name())
                .description(planDTO.description())
                .price(planDTO.price())
                .billingCycle(planDTO.billingCycle())
                .build();

        plan.setTenantId(TenantContext.getCurrentTenant());

        Plan savedPlan = planRepository.save(plan);

        return new PlanResponseDTO(
                savedPlan.getId(),
                savedPlan.getName(),
                savedPlan.getDescription(),
                savedPlan.getPrice(),
                savedPlan.getBillingCycle());
    }

    public List<PlanResponseDTO> list() {
        return planRepository.findAll()
                .stream()
                .map(plan -> new PlanResponseDTO(
                        plan.getId(),
                        plan.getName(),
                        plan.getDescription(),
                        plan.getPrice(),
                        plan.getBillingCycle()))
                .toList();
    }

}
