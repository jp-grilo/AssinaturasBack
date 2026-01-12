package com.projeto.subscription.modules.identity.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.subscription.modules.identity.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
