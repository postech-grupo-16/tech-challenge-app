package com.fiap.tech_challenge_backend.estoque.domain.entities;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que representa uma Peça ou Insumo do estoque.
 * Responsável pelo gerenciamento de inventário de peças e insumos utilizados nos serviços.
 *
 * As validações garantem que:
 * - Preços são sempre positivos
 * - Preço de compra nunca é maior que preço de venda
 * - Quantidade em estoque nunca é negativa
 *
 * Contexto Delimitado: estoque
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PecaInsumo {

    private UUID id;

    @NotNull(message = "O tipo é obrigatório")
    private TipoPecaInsumo tipo;

    @NotBlank(message = "O nome da peça/insumo é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "O preço de venda é obrigatório")
    @Positive(message = "O preço de venda deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço de venda deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal precoVenda;

    @NotNull(message = "O preço de compra é obrigatório")
    @Positive(message = "O preço de compra deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço de compra deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal precoCompra;

    @Size(max = 50, message = "A quantidade por unidade deve ter no máximo 50 caracteres")
    private String quantidadePorUnidade;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotNull(message = "A quantidade mínima é obrigatória")
    @PositiveOrZero(message = "A quantidade mínima não pode ser negativa")
    private Integer quantidadeMinima;

    private LocalDateTime deletedAt;

    public void entrada(Integer quantidade) {
        this.quantidadeEstoque += quantidade;
    }

    public void saida(Integer quantidade) {
        if (quantidade > this.quantidadeEstoque) {
            throw new IllegalArgumentException(
                "Quantidade insuficiente em estoque para: " + this.nome);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public boolean estoqueAbaixoDoMinimo() {
        return this.quantidadeEstoque < this.quantidadeMinima;
    }
}


