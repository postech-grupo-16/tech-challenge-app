package com.fiap.tech_challenge_backend.cadastro.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

public class VeiculoNaoEncontradoException extends NotFoundException {
    public VeiculoNaoEncontradoException(String placa) {
        super(
                "VEICULO_NAO_ENCONTRADO",
                "Veículo não encontrado com placa: " + placa
        );
    }
}
