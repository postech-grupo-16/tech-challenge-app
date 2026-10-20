package com.fiap.tech_challenge_backend.cadastro.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarVeiculoRequest(
        @NotBlank(message = "O modelo é obrigatório")
        @Size(min = 2, max = 100, message = "O modelo deve ter entre 2 e 100 caracteres")
        String modelo
) {
}
