package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;

/**
 * Porta de entrada para a busca de um veículo por placa.
 * Contexto Delimitado: cadastro
 */
public interface BuscarVeiculoUseCase {

    BuscarVeiculoResponse execute(String placa);
}
