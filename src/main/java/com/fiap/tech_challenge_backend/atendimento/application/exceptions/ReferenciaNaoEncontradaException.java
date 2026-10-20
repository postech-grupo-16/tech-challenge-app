package com.fiap.tech_challenge_backend.atendimento.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

/**
 * Lançada quando uma referência necessária para criar/atualizar uma Ordem de Serviço
 * (cliente, veículo, mecânico, serviço do catálogo ou peça do estoque) não é encontrada.
 * Contexto Delimitado: atendimento
 */
public class ReferenciaNaoEncontradaException extends NotFoundException {

    public ReferenciaNaoEncontradaException(String mensagem) {
        super("REFERENCIA_NAO_ENCONTRADA", mensagem);
    }
}
