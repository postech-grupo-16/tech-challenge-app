package com.fiap.tech_challenge_backend.cadastro.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ConflictException;

public class ClienteJaCadastradoException extends ConflictException {

    public ClienteJaCadastradoException(String cpfCnpj) {
        super(
                "CLIENTE_JA_CADASTRADO",
                "Cliente já cadastrado com CPF/CNPJ: " + cpfCnpj
        );
    }
}
