package com.fiap.tech_challenge_backend.atendimento.adapters.out.observability;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.OsHistoricoStatusRepositoryAdapter;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.MetricasNegocioPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.services.TempoAtendimentoDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Toda criação e troca de status de OS grava no histórico, então este é o único ponto
 * necessário para gerar as métricas, sem mexer nos serviços que alteram o status.
 */
@Component
@Primary
public class OsHistoricoStatusMetricasDecorator implements OsHistoricoStatusRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(OsHistoricoStatusMetricasDecorator.class);

    private final OsHistoricoStatusRepositoryAdapter delegate;
    private final MetricasNegocioPort metricas;

    public OsHistoricoStatusMetricasDecorator(OsHistoricoStatusRepositoryAdapter delegate,
                                              MetricasNegocioPort metricas) {
        this.delegate = delegate;
        this.metricas = metricas;
    }

    @Override
    public OsHistoricoStatus salvar(OsHistoricoStatus historicoStatus) {
        // Criação da OS: o histórico começa com statusOrigem nulo.
        if (historicoStatus.getStatusOrigem() == null) {
            OsHistoricoStatus salvo = delegate.salvar(historicoStatus);
            registrarCriacao(historicoStatus.getOrdemServico());
            return salvo;
        }

        // Mesmo status: só trocou o mecânico, não é uma mudança de etapa.
        if (historicoStatus.getStatusOrigem() == historicoStatus.getStatusDestino()) {
            return delegate.salvar(historicoStatus);
        }

        // O histórico é lido antes de salvar para não incluir a mudança atual.
        List<OsHistoricoStatus> anteriores = delegate.buscarPorOrdensServicoOrdenado(
                List.of(historicoStatus.getOrdemServico().getId()));
        OsHistoricoStatus salvo = delegate.salvar(historicoStatus);
        registrarMudanca(historicoStatus, anteriores, salvo.getDataMudanca());
        return salvo;
    }

    @Override
    public List<OsHistoricoStatus> buscarPorOrdensServicoOrdenado(List<Long> ordemIds) {
        return delegate.buscarPorOrdensServicoOrdenado(ordemIds);
    }

    private void registrarCriacao(OrdemServico os) {
        try {
            metricas.ordemServicoCriada(os.getId(), Boolean.TRUE.equals(os.getUrgente()));
        } catch (RuntimeException e) {
            log.debug("Não foi possível registrar a criação da OS {}: {}", os.getId(), e.getMessage());
        }
    }

    private void registrarMudanca(OsHistoricoStatus historicoStatus,
                                  List<OsHistoricoStatus> anteriores,
                                  LocalDateTime dataMudanca) {
        OrdemServico os = historicoStatus.getOrdemServico();
        try {
            LocalDateTime agora = dataMudanca != null ? dataMudanca : LocalDateTime.now();
            Duration tempoNoStatus = TempoAtendimentoDomainService.tempoNoStatusAtual(os, anteriores, agora);
            Duration tempoDesdeCriacao = os.getDataCriacao() != null
                    ? Duration.between(os.getDataCriacao(), agora)
                    : null;
            metricas.statusAlterado(os.getId(), historicoStatus.getStatusOrigem(), historicoStatus.getStatusDestino(),
                    tempoNoStatus, tempoDesdeCriacao);
        } catch (RuntimeException e) {
            log.debug("Não foi possível registrar a mudança de status da OS {}: {}", os.getId(), e.getMessage());
        }
    }
}
