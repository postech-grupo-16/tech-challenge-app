package com.fiap.tech_challenge_backend.cadastro.domain.entities;

import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio que representa um Veículo cadastrado no sistema.
 * Responsável pelo gerenciamento de dados de veículos dos clientes.
 * Contexto Delimitado: cadastro
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {

    private UUID id;

    private Placa placa;

    @NotBlank(message = "A marca é obrigatória")
    @Size(min = 2, max = 100, message = "A marca deve ter entre 2 e 100 caracteres")
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")
    @Size(min = 2, max = 100, message = "O modelo deve ter entre 2 e 100 caracteres")
    private String modelo;

    private Integer ano;

    @NotBlank(message = "A cor é obrigatória")
    @Size(min = 2, max = 50, message = "A cor deve ter entre 2 e 50 caracteres")
    private String cor;

    private LocalDateTime deletedAt;
}

