package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OsHistoricoStatus {

    private UUID id;

    @NotNull(message = "A ordem de serviço do histórico é obrigatória")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrdemServico ordemServico;

    private StatusOrdemServico statusOrigem;

    @NotNull(message = "O status de destino é obrigatório")
    private StatusOrdemServico statusDestino;

    private LocalDateTime dataMudanca;

    /**
     * Referência por ID ao usuário do contexto {@code acesso} que realizou a mudança — não
     * navegamos o objeto diretamente, contextos delimitados diferentes (ver seção 13 do
     * REFATORACAO_HEXAGONAL.md).
     */
    private UUID usuarioId;
}
