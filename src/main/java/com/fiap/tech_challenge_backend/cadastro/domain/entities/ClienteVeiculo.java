package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Entidade de domínio que representa a associação entre Cliente e Veículo.
 * Permite relacionamento N:M (muitos para muitos) entre clientes e veículos.
 * Mantém histórico de veículos que já pertenceram ao cliente através do campo 'ativo'.
 * Contexto Delimitado: cadastro
 *
 * <p>Referencia {@code Cliente} e {@code Veiculo} só por ID — nenhum código consome as
 * antigas referências de objeto {@code cliente}/{@code veiculo} (confirmado antes de removê-las),
 * que existiam apenas como espelho somente-leitura da relação JPA.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteVeiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "O ID do cliente é obrigatório")
    private UUID clienteId;

    @NotNull(message = "O ID do veículo é obrigatório")
    private UUID veiculoId;

    /**
     * Indica se o veículo é atualmente ativo para o cliente.
     * true = veículo atualmente pertence ao cliente
     * false = veículo que pertenceu ao cliente no passado (histórico)
     * Padrão: true (ativo)
     */
    @NotNull(message = "O status de ativo é obrigatório")
    @Builder.Default
    private Boolean ativo = true;
}


