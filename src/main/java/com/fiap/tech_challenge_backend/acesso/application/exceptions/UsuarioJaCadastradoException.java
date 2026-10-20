package com.fiap.tech_challenge_backend.acesso.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ConflictException;

public class UsuarioJaCadastradoException extends ConflictException {

    public UsuarioJaCadastradoException(String campo, String valor) {
        super(
                "USUARIO_JA_CADASTRADO",
                "Usuário já cadastrado com " + campo + ": " + valor
        );
    }
}
