package com.fiap.tech_challenge_backend.atendimento.adapters.out.observability;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.OsHistoricoStatusRepositoryAdapter;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.MetricasNegocioPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OsHistoricoStatusMetricasDecorator Tests")
class OsHistoricoStatusMetricasDecoratorTest {

    private static final LocalDateTime CRIACAO = LocalDateTime.of(2026, 11, 1, 8, 0);
    private static final LocalDateTime MUDANCA = CRIACAO.plusHours(3);

    @Mock
    private OsHistoricoStatusRepositoryAdapter delegate;

    @Mock
    private MetricasNegocioPort metricas;

    @InjectMocks
    private OsHistoricoStatusMetricasDecorator decorator;

    private OrdemServico os;

    @BeforeEach
    void setUp() {
        os = OrdemServico.builder()
                .id(42L)
                .status(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataCriacao(CRIACAO)
                .urgente(true)
                .build();
    }

    private OsHistoricoStatus historico(StatusOrdemServico origem, StatusOrdemServico destino, LocalDateTime data) {
        return OsHistoricoStatus.builder()
                .ordemServico(os)
                .statusOrigem(origem)
                .statusDestino(destino)
                .dataMudanca(data)
                .build();
    }

    @Test
    @DisplayName("histórico sem status de origem deve registrar a criação da OS")
    void deveRegistrarCriacao() {
        OsHistoricoStatus entrada = historico(null, StatusOrdemServico.RECEBIDA, null);
        OsHistoricoStatus salvo = historico(null, StatusOrdemServico.RECEBIDA, CRIACAO);
        when(delegate.salvar(entrada)).thenReturn(salvo);

        assertSame(salvo, decorator.salvar(entrada));

        verify(metricas).ordemServicoCriada(42L, true);
        verify(metricas, never()).statusAlterado(any(), any(), any(), any(), any());
        verify(delegate, never()).buscarPorOrdensServicoOrdenado(any());
    }

    @Test
    @DisplayName("troca de status deve registrar o tempo no status anterior e desde a criação")
    void deveRegistrarMudancaDeStatus() {
        OsHistoricoStatus entrada = historico(StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_EXECUCAO, null);
        OsHistoricoStatus salvo = historico(StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_EXECUCAO, MUDANCA);
        when(delegate.buscarPorOrdensServicoOrdenado(List.of(42L))).thenReturn(List.of(
                historico(null, StatusOrdemServico.RECEBIDA, CRIACAO),
                historico(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, CRIACAO.plusHours(1))));
        when(delegate.salvar(entrada)).thenReturn(salvo);

        assertSame(salvo, decorator.salvar(entrada));

        verify(metricas).statusAlterado(42L, StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_EXECUCAO,
                Duration.ofHours(2), Duration.ofHours(3));
    }

    @Test
    @DisplayName("sem histórico anterior o tempo no status deve contar desde a criação")
    void deveContarDesdeCriacaoSemHistorico() {
        OsHistoricoStatus entrada = historico(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, null);
        when(delegate.buscarPorOrdensServicoOrdenado(List.of(42L))).thenReturn(List.of());
        when(delegate.salvar(entrada))
                .thenReturn(historico(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, MUDANCA));

        decorator.salvar(entrada);

        verify(metricas).statusAlterado(42L, StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO,
                Duration.ofHours(3), Duration.ofHours(3));
    }

    @Test
    @DisplayName("mesmo status (troca de mecânico) não deve gerar métrica")
    void naoDeveRegistrarMesmoStatus() {
        OsHistoricoStatus entrada = historico(StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_DIAGNOSTICO, null);
        when(delegate.salvar(entrada)).thenReturn(entrada);

        assertSame(entrada, decorator.salvar(entrada));

        verifyNoInteractions(metricas);
        verify(delegate, never()).buscarPorOrdensServicoOrdenado(any());
    }

    @Test
    @DisplayName("falha na métrica de criação não deve impedir o salvar")
    void falhaNaMetricaDeCriacaoNaoDeveImpedirSalvar() {
        OsHistoricoStatus entrada = historico(null, StatusOrdemServico.RECEBIDA, null);
        when(delegate.salvar(entrada)).thenReturn(entrada);
        doThrow(new RuntimeException("falha")).when(metricas).ordemServicoCriada(anyLong(), anyBoolean());

        assertSame(entrada, decorator.salvar(entrada));
    }

    @Test
    @DisplayName("falha na métrica de mudança não deve impedir o salvar")
    void falhaNaMetricaDeMudancaNaoDeveImpedirSalvar() {
        OsHistoricoStatus entrada = historico(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, null);
        when(delegate.buscarPorOrdensServicoOrdenado(List.of(42L))).thenReturn(List.of());
        when(delegate.salvar(entrada)).thenReturn(entrada);
        doThrow(new RuntimeException("falha")).when(metricas).statusAlterado(any(), any(), any(), any(), any());

        assertSame(entrada, decorator.salvar(entrada));
    }

    @Test
    @DisplayName("busca do histórico deve apenas delegar")
    void buscaDeveDelegar() {
        List<OsHistoricoStatus> esperado = List.of(historico(null, StatusOrdemServico.RECEBIDA, CRIACAO));
        when(delegate.buscarPorOrdensServicoOrdenado(List.of(42L))).thenReturn(esperado);

        assertEquals(esperado, decorator.buscarPorOrdensServicoOrdenado(List.of(42L)));
        verifyNoInteractions(metricas);
    }
}
