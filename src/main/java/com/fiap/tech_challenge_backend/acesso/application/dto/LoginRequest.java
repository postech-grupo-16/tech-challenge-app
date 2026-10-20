package com.fiap.tech_challenge_backend.acesso.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
