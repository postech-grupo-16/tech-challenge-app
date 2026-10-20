package com.fiap.tech_challenge_backend.acompanhamento.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

/**
 * Lançada quando a ordem de serviço consultada não existe ou não pertence
 * ao cliente informado.
 * Contexto Delimitado: acompanhamento
 */
public class OrdemServicoNaoEncontradaException extends NotFoundException {

    public OrdemServicoNaoEncontradaException(String mensagem) {
        super("ORDEM_SERVICO_NAO_ENCONTRADA", mensagem);
    }
}
