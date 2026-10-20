package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;


public record OrdemServicoAtualizarRequestDTO(

        @NotNull(message = "O cliente é obrigatório")
        UUID clienteId,

        @NotNull(message = "O veículo é obrigatório")
        UUID veiculoId,

        UUID mecanicoId,

        @NotNull(message = "O status é obrigatório")
        StatusOrdemServico status,

        LocalDateTime dataInicioExecucao,

        LocalDateTime dataFinalizacao,

        @NotNull(message = "A flag de urgência é obrigatória")
        Boolean urgente
) {
}

