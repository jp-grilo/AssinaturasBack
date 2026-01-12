package com.projeto.subscription.modules.identity.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.subscription.modules.identity.dto.AuthRequestDTO;
import com.projeto.subscription.modules.identity.dto.AuthResponseDTO;
import com.projeto.subscription.modules.identity.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody @Valid AuthRequestDTO dto) {
        return authService.login(dto);
    }
}
