package com.fiap.tech_challenge_backend.atendimento.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ServicoOrcamentoRequestDTO(

        @NotNull(message = "O ID do serviço do catálogo é obrigatório")
        UUID servicoId
) {
}
