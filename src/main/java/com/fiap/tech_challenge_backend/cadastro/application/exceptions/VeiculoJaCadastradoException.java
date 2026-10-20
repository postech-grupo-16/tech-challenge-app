package com.fiap.tech_challenge_backend.cadastro.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.ConflictException;

public class VeiculoJaCadastradoException extends ConflictException {
    public VeiculoJaCadastradoException(String placa) {
        super(
                "VEICULO_JA_CADASTRADO",
                "Veículo já cadastrado com placa: " + placa
        );
    }
}
