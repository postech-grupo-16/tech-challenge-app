package com.fiap.tech_challenge_backend.cadastro.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O telefone deve conter apenas números, com 10 ou 11 dígitos"
        )
        String telefone,

        @Pattern(
                regexp = "^\\d{8}$",
                message = "O CEP deve conter exatamente 8 dígitos, sem hífen ou pontuação"
        )
        String cep,

        @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres")
        String rua,

        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
        String numero,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complemento,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres")
        String estado
) {
}
