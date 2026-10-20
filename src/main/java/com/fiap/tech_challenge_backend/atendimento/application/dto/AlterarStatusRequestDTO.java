package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AlterarStatusRequestDTO(

        @NotNull(message = "O novo status é obrigatório")
        StatusOrdemServico novoStatus,

        UUID mecanicoId
) {
}

