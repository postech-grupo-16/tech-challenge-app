package com.fiap.tech_challenge_backend.atendimento.application.exceptions;

import com.fiap.tech_challenge_backend.shared.application.exceptions.NotFoundException;

/**
 * Lançada quando uma Ordem de Serviço não é encontrada, seja por ID direto,
 * seja por uma busca indireta (ex.: pelo ID de um orçamento associado).
 * Contexto Delimitado: atendimento
 */
public class OrdemServicoNaoEncontradaException extends NotFoundException {

    public OrdemServicoNaoEncontradaException(String mensagem) {
        super("ORDEM_SERVICO_NAO_ENCONTRADA", mensagem);
    }
}
