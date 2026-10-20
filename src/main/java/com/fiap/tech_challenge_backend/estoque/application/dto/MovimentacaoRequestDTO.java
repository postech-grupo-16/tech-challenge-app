package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record MovimentacaoRequestDTO(
        @NotNull(message = "O ID da peça/insumo é obrigatório") UUID pecaInsumoId,
        @NotNull(message = "O tipo de movimentação é obrigatório") TipoMovimentacao tipoMovimentacao,
        @NotNull @Positive Integer quantidade,
        String observacao
) {
}
