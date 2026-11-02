package com.fiap.tech_challenge_backend.atendimento.adapters.out.observability;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.newrelic.api.agent.Insights;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewRelicMetricasNegocioAdapter Tests")
class NewRelicMetricasNegocioAdapterTest {

    @Mock
    private Insights insights;

    @Captor
    private ArgumentCaptor<Map<String, Object>> atributos;

    private NewRelicMetricasNegocioAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NewRelicMetricasNegocioAdapter(insights);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        TransactionSynchronizationManager.clear();
    }

    @Test
    @DisplayName("deve enviar evento de OS criada")
    void deveEnviarOsCriada() {
        adapter.ordemServicoCriada(42L, true);

        verify(insights).recordCustomEvent(eq("OrdemServicoCriada"), atributos.capture());
        assertEquals(42L, atributos.getValue().get("osId"));
        assertEquals(true, atributos.getValue().get("urgente"));
    }

    @Test
    @DisplayName("deve enviar evento de status alterado com os tempos em segundos")
    void deveEnviarStatusAlterado() {
        adapter.statusAlterado(42L, StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_EXECUCAO,
                Duration.ofMinutes(35), Duration.ofHours(2));

        verify(insights).recordCustomEvent(eq("OrdemServicoStatusAlterado"), atributos.capture());
        Map<String, Object> enviados = atributos.getValue();
        assertEquals(42L, enviados.get("osId"));
        assertEquals("EM_DIAGNOSTICO", enviados.get("statusOrigem"));
        assertEquals("EM_EXECUCAO", enviados.get("statusDestino"));
        assertEquals(2100L, enviados.get("tempoNoStatusSegundos"));
        assertEquals(7200L, enviados.get("tempoDesdeCriacaoSegundos"));
    }

    @Test
    @DisplayName("não deve enviar tempos nulos")
    void naoDeveEnviarTemposNulos() {
        adapter.statusAlterado(42L, StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, null, null);

        verify(insights).recordCustomEvent(eq("OrdemServicoStatusAlterado"), atributos.capture());
        assertFalse(atributos.getValue().containsKey("tempoNoStatusSegundos"));
        assertFalse(atributos.getValue().containsKey("tempoDesdeCriacaoSegundos"));
    }

    @Test
    @DisplayName("deve enviar evento de integração com falha")
    void deveEnviarIntegracaoComFalha() {
        adapter.integracao("email", "com_anexo", false, Duration.ofMillis(150), "MailSendException");

        verify(insights).recordCustomEvent(eq("Integracao"), atributos.capture());
        Map<String, Object> enviados = atributos.getValue();
        assertEquals("email", enviados.get("integracao"));
        assertEquals("com_anexo", enviados.get("operacao"));
        assertEquals(false, enviados.get("sucesso"));
        assertEquals(150L, enviados.get("duracaoMs"));
        assertEquals("MailSendException", enviados.get("erro"));
    }

    @Test
    @DisplayName("não deve enviar atributo de erro quando a integração funcionou")
    void naoDeveEnviarErroComSucesso() {
        adapter.integracao("pdf", "orcamento", true, Duration.ofMillis(10), null);

        verify(insights).recordCustomEvent(eq("Integracao"), atributos.capture());
        assertEquals(true, atributos.getValue().get("sucesso"));
        assertFalse(atributos.getValue().containsKey("erro"));
    }

    @Test
    @DisplayName("deve incluir o requestId do MDC")
    void deveIncluirRequestId() {
        MDC.put("requestId", "abc-123");

        adapter.ordemServicoCriada(42L, false);

        verify(insights).recordCustomEvent(eq("OrdemServicoCriada"), atributos.capture());
        assertEquals("abc-123", atributos.getValue().get("requestId"));
    }

    @Test
    @DisplayName("não deve incluir requestId quando não houver no MDC")
    void naoDeveIncluirRequestIdAusente() {
        adapter.ordemServicoCriada(42L, false);

        verify(insights).recordCustomEvent(eq("OrdemServicoCriada"), atributos.capture());
        assertFalse(atributos.getValue().containsKey("requestId"));
    }

    @Test
    @DisplayName("não deve propagar falha do New Relic")
    void naoDevePropagarFalha() {
        doThrow(new RuntimeException("falha")).when(insights).recordCustomEvent(any(), anyMap());

        assertDoesNotThrow(() -> adapter.ordemServicoCriada(42L, false));
    }

    @Test
    @DisplayName("eventos de OS só devem sair depois do commit")
    void eventosDeOsDevemSairAposCommit() {
        TransactionSynchronizationManager.initSynchronization();

        adapter.ordemServicoCriada(42L, false);
        adapter.statusAlterado(42L, StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, null, null);

        verify(insights, never()).recordCustomEvent(any(), anyMap());

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        verify(insights).recordCustomEvent(eq("OrdemServicoCriada"), anyMap());
        verify(insights).recordCustomEvent(eq("OrdemServicoStatusAlterado"), anyMap());
    }

    @Test
    @DisplayName("eventos de OS não devem sair quando a transação é desfeita")
    void eventosDeOsNaoDevemSairNoRollback() {
        TransactionSynchronizationManager.initSynchronization();

        adapter.ordemServicoCriada(42L, false);

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(insights, never()).recordCustomEvent(any(), anyMap());
    }

    @Test
    @DisplayName("evento de integração deve sair na hora, mesmo dentro de transação")
    void integracaoDeveSairNaHora() {
        TransactionSynchronizationManager.initSynchronization();

        adapter.integracao("email", "sem_anexo", false, Duration.ofMillis(5), "MailSendException");

        verify(insights).recordCustomEvent(eq("Integracao"), anyMap());
    }
}
