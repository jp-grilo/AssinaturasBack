package com.projeto.subscription.modules.identity.service;

import com.projeto.subscription.modules.identity.dto.AuthRequestDTO;
import com.projeto.subscription.modules.identity.dto.AuthResponseDTO;
import com.projeto.subscription.modules.identity.repository.UserRepository;
import com.projeto.subscription.shared.config.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {
        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = tokenService.generateToken(user);
        return new AuthResponseDTO(token, user.getRole());
    }
}