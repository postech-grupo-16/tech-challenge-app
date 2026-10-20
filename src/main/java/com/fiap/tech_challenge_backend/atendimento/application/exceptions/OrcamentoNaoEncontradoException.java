package com.fiap.tech_challenge_backend.atendimento.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

/**
 * Lançada quando um orçamento não é encontrado dentro de uma Ordem de Serviço.
 * Contexto Delimitado: atendimento
 */
public class OrcamentoNaoEncontradoException extends NotFoundException {

    public OrcamentoNaoEncontradoException(String mensagem) {
        super("ORCAMENTO_NAO_ENCONTRADO", mensagem);
    }
}
