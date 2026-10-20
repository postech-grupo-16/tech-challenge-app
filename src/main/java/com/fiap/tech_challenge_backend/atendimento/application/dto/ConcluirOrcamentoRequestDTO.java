package com.fiap.tech_challenge_backend.atendimento.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConcluirOrcamentoRequestDTO(
        @NotNull(message = "O ID do orçamento é obrigatório")
        Long orcamentoId,

        @NotNull(message = "O e-mail do cliente é obrigatório")
        String emailCliente,

        @NotNull(message = "O prazo estipulado é obrigatório")
        LocalDateTime prazoEstipulado
) {}
