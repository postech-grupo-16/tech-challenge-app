package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoResponse;

/**
 * Porta de entrada para o cadastro de um novo veículo.
 * Contexto Delimitado: cadastro
 */
public interface CadastrarVeiculoUseCase {

    CadastroVeiculoResponse execute(CadastroVeiculoRequest request);
}
