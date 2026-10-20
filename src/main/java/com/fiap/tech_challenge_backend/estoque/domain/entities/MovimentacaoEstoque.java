package com.fiap.tech_challenge_backend.estoque.domain.entities;

import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que registra cada movimentação de estoque (entrada, saída ou ajuste).
 * Contexto Delimitado: estoque
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque {

    private UUID id;

    @NotNull(message = "A peça/insumo é obrigatória")
    @ToString.Exclude
    private PecaInsumo pecaInsumo;

    @NotNull(message = "O tipo de movimentação é obrigatório")
    private TipoMovimentacao tipoMovimentacao;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @Size(max = 500)
    private String observacao;

    private LocalDateTime criadoEm;
}
