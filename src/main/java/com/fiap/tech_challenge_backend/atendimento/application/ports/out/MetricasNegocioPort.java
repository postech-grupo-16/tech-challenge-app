package com.fiap.tech_challenge_backend.atendimento.application.ports.out;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;

import java.time.Duration;

/**
 * Métricas de negócio do atendimento (volume de OS, tempo por status e integrações).
 * Implementações nunca devem lançar exceção: métrica não pode derrubar regra de negócio.
 */
public interface MetricasNegocioPort {

    void ordemServicoCriada(Long osId, boolean urgente);

    void statusAlterado(Long osId,
                        StatusOrdemServico origem,
                        StatusOrdemServico destino,
                        Duration tempoNoStatusOrigem,
                        Duration tempoDesdeCriacao);

    void integracao(String integracao, String operacao, boolean sucesso, Duration duracao, String erro);
}
