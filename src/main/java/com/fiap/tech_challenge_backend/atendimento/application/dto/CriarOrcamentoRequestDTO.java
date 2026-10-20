package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriarOrcamentoRequestDTO(

        @NotNull(message = "O tipo do orçamento é obrigatório")
        TipoOrcamento tipo,

        @NotNull(message = "O prazo estimado é obrigatório")
        String prazoEstipulado,

        @NotEmpty(message = "Deve haver pelo menos um serviço ou peça no orçamento")
        @Valid
        List<ServicoOrcamentoRequestDTO> servicos,

        @Valid
        List<PecaOrcamentoRequestDTO> pecas
) {
}
