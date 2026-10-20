package com.fiap.tech_challenge_backend.cadastro.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

public class ClienteNaoEncontradoException extends NotFoundException {
    public ClienteNaoEncontradoException(String cpfCnpj) {
        super(
                "CLIENTE_NAO_ENCONTRADO",
                "Cliente não encontrado com CPF/CNPJ: " + cpfCnpj
        );
    }
}
