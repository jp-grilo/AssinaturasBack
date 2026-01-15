package com.projeto.subscription.modules.plan.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.subscription.modules.plan.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

}
