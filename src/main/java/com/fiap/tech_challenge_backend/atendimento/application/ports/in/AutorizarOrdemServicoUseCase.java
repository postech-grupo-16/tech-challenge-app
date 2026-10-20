package com.fiap.tech_challenge_backend.atendimento.application.ports.in;


public interface AutorizarOrdemServicoUseCase {

    /**
     * Autoriza uma ordem de serviço passando o status de AGUARDANDO_APROVACAO para EM_EXECUCAO.
     * Registra automaticamente o histórico de transição com usuário "CLIENTE_VIA_EMAIL".
     *
     * @param id ID da ordem de serviço a ser autorizada
     * @throws com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException
     *         se a OS não for encontrada
     * @throws com.fiap.tech_challenge_backend.atendimento.domain.exceptions.OrdemServicoStatusException
     *         se o status atual não for AGUARDANDO_APROVACAO
     */
    void autorizar(Long id);
}
