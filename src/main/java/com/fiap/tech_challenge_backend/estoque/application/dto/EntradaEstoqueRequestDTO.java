package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Requisição de entrada de estoque.
 *
 * <p>Fluxo unificado: quando {@code id} aponta para uma peça/insumo existente,
 * a entrada apenas incrementa o estoque (reposição). Caso contrário, uma nova
 * peça/insumo é cadastrada com {@code quantidade} como estoque inicial. Em ambos
 * os casos é gerada uma movimentação do tipo ENTRADA.</p>
 *
 * <p>Os campos cadastrais (nome, preços, tipo) são obrigatórios apenas no cadastro de
 * uma peça nova; na reposição de uma peça existente podem ser omitidos.</p>
 */
public record EntradaEstoqueRequestDTO(
        UUID id,
        String nome,
        String descricao,
        @Positive BigDecimal precoVenda,
        @Positive BigDecimal precoCompra,
        String quantidadePorUnidade,
        @NotNull(message = "A quantidade de entrada é obrigatória")
        @Positive(message = "A quantidade de entrada deve ser maior que zero")
        Integer quantidade,
        @PositiveOrZero Integer quantidadeMinima,
        String observacao,
        TipoPecaInsumo tipo
) {
}
