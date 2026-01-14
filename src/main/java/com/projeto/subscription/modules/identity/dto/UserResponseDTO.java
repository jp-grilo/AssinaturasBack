package com.projeto.subscription.modules.identity.dto;

import java.util.UUID;

import com.projeto.subscription.shared.util.Enums.UserRole;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role) {
}