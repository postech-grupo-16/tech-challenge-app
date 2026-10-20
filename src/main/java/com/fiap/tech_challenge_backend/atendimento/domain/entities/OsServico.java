package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import jakarta.validation.constraints.NotNull;
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
public class OsServico {

    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull(message = "O orcamento da ordem de servico e obrigatorio")
    @ToString.Exclude
    private OsOrcamento orcamento;

    private Long ordemServicoId;

    @NotNull(message = "O servico do catalogo e obrigatorio")
    private ServicoCatalogo servico;

    @NotNull(message = "O preco de mao de obra aplicado e obrigatorio")
    @PositiveOrZero(message = "O preco de mao de obra aplicado nao pode ser negativo")
    private BigDecimal precoMaoDeObraAplicado;
}
