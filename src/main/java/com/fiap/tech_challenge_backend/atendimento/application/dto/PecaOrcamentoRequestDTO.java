package com.fiap.tech_challenge_backend.atendimento.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PecaOrcamentoRequestDTO(

        @NotNull(message = "O ID da peça/insumo é obrigatório")
        UUID pecaId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
