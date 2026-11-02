package com.fiap.tech_challenge_backend.atendimento.adapters.out.observability;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.OsHistoricoStatusRepositoryAdapter;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusRepository;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.MetricasNegocioPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringJUnitConfig(MetricasNegocioWiringTest.Config.class)
@DisplayName("Ligação dos beans de métricas de negócio")
class MetricasNegocioWiringTest {

    @Import({OsHistoricoStatusRepositoryAdapter.class,
            OsHistoricoStatusMetricasDecorator.class,
            NewRelicMetricasNegocioAdapter.class})
    static class Config {
    }

    @MockitoBean
    private OsHistoricoStatusRepository historicoRepository;

    @MockitoBean
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private OsHistoricoStatusRepositoryPort historicoPort;

    @Autowired
    private MetricasNegocioPort metricas;

    @Test
    @DisplayName("os serviços devem receber o decorator, sem conflito com o adapter de persistência")
    void portaDeveResolverParaODecorator() {
        assertInstanceOf(OsHistoricoStatusMetricasDecorator.class, historicoPort);
    }

    @Test
    @DisplayName("a porta de métricas deve resolver para o adapter do New Relic")
    void metricasDevemResolverParaOAdapter() {
        assertInstanceOf(NewRelicMetricasNegocioAdapter.class, metricas);
    }
}
