package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.BuscarClienteResponse;

/**
 * Porta de entrada para a busca de um cliente por CPF/CNPJ.
 * Contexto Delimitado: cadastro
 */
public interface BuscarClienteUseCase {

    BuscarClienteResponse execute(String cpfCnpj);
}
