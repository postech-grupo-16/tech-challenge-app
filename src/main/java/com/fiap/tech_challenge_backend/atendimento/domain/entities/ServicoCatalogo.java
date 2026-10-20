package com.fiap.tech_challenge_backend.atendimento.domain.entities;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade de domínio que representa um Serviço do Catálogo de Mão de Obra.
 * Define os serviços/mão de obra que a oficina oferece aos clientes.
 * Exemplos: Troca de Óleo, Alinhamento, Balanceamento, Freio, Suspensão, etc.
 *
 * As validações garantem que:
 * - O preço de mão de obra é sempre positivo
 * - O nome e descrição são válidos
 *
 * Contexto Delimitado: atendimento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoCatalogo {

    private UUID id;

    @NotBlank(message = "O nome do serviço é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "O preço de mão de obra é obrigatório")
    @Positive(message = "O preço de mão de obra deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal precoMaoDeObra;

    @NotBlank(message = "A categoria do serviço é obrigatória")
    @Pattern(
        regexp = "DIAGNOSTICO|CORRETIVA|PREVENTIVA|GARANTIA",
        message = "A categoria deve ser DIAGNOSTICO, CORRETIVA, PREVENTIVA ou GARANTIA"
    )
    @Builder.Default
    private String categoria = "PREVENTIVA";
}
