package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PecaInsumoRequestDTO(
        @NotBlank(message = "O nome é obrigatório") String nome,
        String descricao,
        @NotNull @Positive BigDecimal precoVenda,
        @NotNull @Positive BigDecimal precoCompra,
        String quantidadePorUnidade,
        @NotNull @PositiveOrZero Integer quantidadeEstoque,
        @NotNull @PositiveOrZero Integer quantidadeMinima,
        @NotNull(message = "O tipo é obrigatório") TipoPecaInsumo tipo
) {
}
