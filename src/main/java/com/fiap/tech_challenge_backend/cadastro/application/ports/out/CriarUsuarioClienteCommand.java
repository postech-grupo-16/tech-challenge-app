package com.fiap.tech_challenge_backend.cadastro.application.ports.out;

import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Email;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Telefone;

public record CriarUsuarioClienteCommand(
        String nome,
        Email email,
        String senha,
        Telefone telefone,
        CpfCnpj cpfCnpj
) {
}
