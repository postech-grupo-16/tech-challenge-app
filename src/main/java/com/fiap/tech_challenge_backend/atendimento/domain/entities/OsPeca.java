package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsPeca {

    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull(message = "O orcamento da ordem de servico e obrigatorio")
    @ToString.Exclude
    private OsOrcamento orcamento;

    private Long ordemServicoId;

    /**
     * Referência por ID à peça/insumo do contexto {@code estoque} — não navegamos o objeto
     * diretamente, contextos delimitados diferentes (ver seção 13 do REFATORACAO_HEXAGONAL.md).
     */
    @NotNull(message = "A peca/insumo e obrigatoria")
    private UUID pecaInsumoId;

    @NotNull(message = "A quantidade da peca usada na OS e obrigatoria")
    @Positive(message = "A quantidade da peca deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "O preco de venda aplicado e obrigatorio")
    @PositiveOrZero(message = "O preco de venda aplicado nao pode ser negativo")
    private BigDecimal precoVendaAplicado;
}
