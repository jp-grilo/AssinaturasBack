package com.projeto.subscription.modules.identity.dto;

import com.projeto.subscription.shared.util.Enums.UserRole;

public record AuthResponseDTO(String token, UserRole role) {
}