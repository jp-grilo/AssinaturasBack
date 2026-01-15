package com.projeto.subscription.modules.plan.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.subscription.modules.plan.dto.PlanRequestDTO;
import com.projeto.subscription.modules.plan.dto.PlanResponseDTO;
import com.projeto.subscription.modules.plan.service.PlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/plans")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public PlanResponseDTO create(@RequestBody @Valid PlanRequestDTO plan) {
        return planService.create(plan);
    }

    @GetMapping
    public List<PlanResponseDTO> list() {
        return planService.list();
    }

}
