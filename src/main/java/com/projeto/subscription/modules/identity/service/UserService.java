package com.projeto.subscription.modules.identity.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.subscription.modules.identity.dto.UserRequestDTO;
import com.projeto.subscription.modules.identity.dto.UserResponseDTO;
import com.projeto.subscription.modules.identity.model.User;
import com.projeto.subscription.modules.identity.repository.UserRepository;
import com.projeto.subscription.shared.tenant_context.TenantContext;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO create(UserRequestDTO dto) {

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        user.setTenantId(TenantContext.getCurrentTenant());

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole());
    }

    public List<UserResponseDTO> list() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()))
                .toList();
    }

}
