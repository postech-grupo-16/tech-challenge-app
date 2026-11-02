package com.fiap.tech_challenge_backend.atendimento.adapters.out.observability;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.MetricasNegocioPort;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.newrelic.api.agent.Insights;
import com.newrelic.api.agent.NewRelic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Envia as métricas de negócio ao New Relic como eventos customizados (ver docs/observability.md).
 * Sem o agente ativo (local, Kind) a API do New Relic não faz nada.
 */
@Component
public class NewRelicMetricasNegocioAdapter implements MetricasNegocioPort {

    private static final Logger log = LoggerFactory.getLogger(NewRelicMetricasNegocioAdapter.class);

    static final String EVENTO_OS_CRIADA = "OrdemServicoCriada";
    static final String EVENTO_STATUS_ALTERADO = "OrdemServicoStatusAlterado";
    static final String EVENTO_INTEGRACAO = "Integracao";

    // Mesma chave do RequestIdFilter. Literal porque atendimento não pode ganhar nova dependência de shared.
    private static final String MDC_REQUEST_ID = "requestId";

    private final Insights insights;

    public NewRelicMetricasNegocioAdapter() {
        this(NewRelic.getAgent().getInsights());
    }

    NewRelicMetricasNegocioAdapter(Insights insights) {
        this.insights = insights;
    }

    @Override
    public void ordemServicoCriada(Long osId, boolean urgente) {
        Map<String, Object> atributos = atributosBase();
        atributos.put("osId", osId);
        atributos.put("urgente", urgente);
        enviarAposCommit(EVENTO_OS_CRIADA, atributos);
    }

    @Override
    public void statusAlterado(Long osId,
                               StatusOrdemServico origem,
                               StatusOrdemServico destino,
                               Duration tempoNoStatusOrigem,
                               Duration tempoDesdeCriacao) {
        Map<String, Object> atributos = atributosBase();
        atributos.put("osId", osId);
        atributos.put("statusOrigem", origem.name());
        atributos.put("statusDestino", destino.name());
        if (tempoNoStatusOrigem != null) {
            atributos.put("tempoNoStatusSegundos", tempoNoStatusOrigem.toSeconds());
        }
        if (tempoDesdeCriacao != null) {
            atributos.put("tempoDesdeCriacaoSegundos", tempoDesdeCriacao.toSeconds());
        }
        enviarAposCommit(EVENTO_STATUS_ALTERADO, atributos);
    }

    @Override
    public void integracao(String integracao, String operacao, boolean sucesso, Duration duracao, String erro) {
        Map<String, Object> atributos = atributosBase();
        atributos.put("integracao", integracao);
        atributos.put("operacao", operacao);
        atributos.put("sucesso", sucesso);
        atributos.put("duracaoMs", duracao.toMillis());
        if (erro != null) {
            atributos.put("erro", erro);
        }
        // Na hora, e não após o commit: a falha precisa aparecer mesmo se a transação for desfeita.
        enviar(EVENTO_INTEGRACAO, atributos);
    }

    private Map<String, Object> atributosBase() {
        Map<String, Object> atributos = new HashMap<>();
        String requestId = MDC.get(MDC_REQUEST_ID);
        if (requestId != null) {
            atributos.put(MDC_REQUEST_ID, requestId);
        }
        return atributos;
    }

    private void enviarAposCommit(String evento, Map<String, Object> atributos) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            enviar(evento, atributos);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                enviar(evento, atributos);
            }
        });
    }

    private void enviar(String evento, Map<String, Object> atributos) {
        try {
            insights.recordCustomEvent(evento, atributos);
        } catch (RuntimeException e) {
            log.debug("Não foi possível enviar o evento {} ao New Relic: {}", evento, e.getMessage());
        }
    }
}
