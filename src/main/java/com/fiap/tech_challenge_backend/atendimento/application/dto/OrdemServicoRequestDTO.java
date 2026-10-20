package com.fiap.tech_challenge_backend.atendimento.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrdemServicoRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "O veículo é obrigatório")
        UUID veiculoId,

        UUID mecanicoId,

        Boolean urgente
) {
}

