package com.fiap.tech_challenge_backend.cadastro.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroVeiculoRequest(
        @NotBlank(message = "A placa é obrigatória")
        String placa,

        @NotBlank(message = "A marca é obrigatória")
        @Size(min = 2, max = 100, message = "A marca deve ter entre 2 e 100 caracteres")
        String marca,

        @NotBlank(message = "O modelo é obrigatório")
        @Size(min = 2, max = 100, message = "O modelo deve ter entre 2 e 100 caracteres")
        String modelo,

        @NotNull(message = "O ano é obrigatório")
        @Min(value = 1900, message = "O ano deve ser no mínimo 1900")
        Integer ano,

        @NotBlank(message = "A cor é obrigatória")
        @Size(min = 2, max = 50, message = "A cor deve ter entre 2 e 50 caracteres")
        String cor,

        @NotBlank(message = "O CPF/CNPJ é obrigatório")
        String cpfCnpj
) {
}
