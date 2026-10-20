package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import jakarta.validation.constraints.NotNull;

public record AprovarRejeitarOrcamentoRequestDTO(

        @NotNull(message = "O status do orçamento é obrigatório")
        StatusOrcamento status
) {
}
