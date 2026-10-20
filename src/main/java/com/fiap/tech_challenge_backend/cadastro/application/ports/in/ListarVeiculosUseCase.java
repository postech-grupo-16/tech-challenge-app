package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarVeiculoResponse;

import java.util.List;

/**
 * Porta de entrada para a listagem de todos os veículos.
 * Contexto Delimitado: cadastro
 */
public interface ListarVeiculosUseCase {

    List<BuscarVeiculoResponse> execute();
}
