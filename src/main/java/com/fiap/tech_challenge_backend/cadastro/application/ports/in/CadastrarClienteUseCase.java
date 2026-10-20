package com.fiap.tech_challenge_backend.cadastro.application.ports.in;

import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroClienteResponse;

/**
 * Porta de entrada para o cadastro de um novo cliente.
 * Contexto Delimitado: cadastro
 */
public interface CadastrarClienteUseCase {

    CadastroClienteResponse execute(CadastroClienteRequest request);
}
