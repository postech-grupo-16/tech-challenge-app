package com.fiap.tech_challenge_backend.acesso.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

public class UsuarioNaoEncontradoException extends NotFoundException {
    public UsuarioNaoEncontradoException(String cpfCnpj) {
        super(
                "USUARIO_NAO_ENCONTRADO",
                "Usuário não encontrado com CPF/CNPJ: " + cpfCnpj
        );
    }
}
