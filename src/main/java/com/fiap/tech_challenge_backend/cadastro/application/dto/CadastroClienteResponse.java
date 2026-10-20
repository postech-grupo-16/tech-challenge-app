package com.fiap.tech_challenge_backend.cadastro.application.dto;

import java.util.UUID;

public record CadastroClienteResponse(
        UUID id,
        String nome,
        String cpfCnpj,
        String telefone,
        String cep,
        String rua,
        String numero,
        String complemento,
        String cidade,
        String estado,
        String status
) {
}
