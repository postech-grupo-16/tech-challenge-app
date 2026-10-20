package com.fiap.tech_challenge_backend.cadastro.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email deve ser válido")
        @Size(max = 150, message = "O email deve ter o máximo 150 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
        String senha,

        @NotBlank(message = "O CPF/CNPJ é obrigatório")
        @Size(min = 11, max = 18, message = "O CPF/CNPJ deve ter entre 11 e 18 caracteres")
        @Pattern(
                regexp = "^(\\d{11}|[A-Za-z0-9]{14})$",
                message = "O CPF/CNPJ deve conter apenas letras e números, sem pontuação. CPF deve ter 11 dígitos e CNPJ deve ter 14 caracteres"
        )
        String cpfCnpj,

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
