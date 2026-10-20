package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.AtualizarClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;

/**
 * Porta de entrada para a atualização de dados de um cliente.
 * Contexto Delimitado: cadastro
 */
public interface AtualizarClienteUseCase {

    BuscarClienteResponse execute(String cpfCnpj, AtualizarClienteRequest request);
}
