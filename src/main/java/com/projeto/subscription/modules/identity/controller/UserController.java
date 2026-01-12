package com.projeto.subscription.modules.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.subscription.modules.identity.dto.UserRequestDTO;
import com.projeto.subscription.modules.identity.dto.UserResponseDTO;
import com.projeto.subscription.modules.identity.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO create(@RequestBody @Valid UserRequestDTO user) {
        return userService.create(user);
    }

    @GetMapping
    public List<UserResponseDTO> list() {
        return userService.list();
    }
}
