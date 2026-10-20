package com.fiap.tech_challenge_backend.atendimento.domain.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TempoAtendimentoDomainService - Testes Unitários")
class TempoAtendimentoDomainServiceTest {

    private OrdemServico ordemServico;
    private LocalDateTime referencia;

    @BeforeEach
    void setUp() {
        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .build();

        Veiculo veiculo = Veiculo.builder()
                .id(UUID.randomUUID())
                .placa(new Placa("ABC1234"))
                .marca("Ford")
                .modelo("Fiesta")
                .ano(2020)
                .cor("Branco")
                .build();

        LocalDateTime agora = LocalDateTime.now();
        ordemServico = OrdemServico.builder()
                .id(1L)
                .clienteId(cliente.getId())
                .veiculoId(veiculo.getId())
                .status(StatusOrdemServico.RECEBIDA)
                .queixaCliente("Problema no motor")
                .dataCriacao(agora.minusHours(5))
                .build();

        referencia = agora;
    }

    @Test
    @DisplayName("Deve calcular tempo total formatado para OS em andamento")
    void testCalcularTempoTotalFormatadoEmAndamento() {
        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertNotNull(resultado);
        assertTrue(resultado.contains("em andamento"));
        assertTrue(resultado.contains("hora") || resultado.contains("minuto"));
    }

    @Test
    @DisplayName("Deve calcular tempo total formatado para OS finalizada")
    void testCalcularTempoTotalFormatadoFinalizada() {
        LocalDateTime dataFim = ordemServico.getDataCriacao().plusHours(3);
        ordemServico.setDataFinalizacao(dataFim);

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertNotNull(resultado);
        assertFalse(resultado.contains("em andamento"));
    }

    @Test
    @DisplayName("Deve calcular tempo total de 1 dia")
    void testCalcularTempoTotalUmDia() {
        ordemServico.setDataCriacao(referencia.minusDays(1));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("1 dia"));
    }

    @Test
    @DisplayName("Deve calcular tempo total de múltiplos dias")
    void testCalcularTempoTotalMultiplosDias() {
        ordemServico.setDataCriacao(referencia.minusDays(3).minusHours(2));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("dias"));
    }

    @Test
    @DisplayName("Deve calcular tempo total de horas")
    void testCalcularTempoTotalHoras() {
        ordemServico.setDataCriacao(referencia.minusHours(2).minusMinutes(30));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("hora") || resultado.contains("minuto"));
    }

    @Test
    @DisplayName("Deve calcular tempo por status sem histórico")
    void testCalcularTempoPorStatusSemHistorico() {
        List<OsHistoricoStatus> historico = new ArrayList<>();

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey(StatusOrdemServico.RECEBIDA.name()));
        assertTrue(resultado.get(StatusOrdemServico.RECEBIDA.name()).contains("em andamento"));
    }

    @Test
    @DisplayName("Deve calcular tempo por status com um histórico")
    void testCalcularTempoPorStatusComUmHistorico() {
        LocalDateTime mudanca = ordemServico.getDataCriacao().plusHours(2);
        OsHistoricoStatus evento = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(mudanca)
                .ordemServico(ordemServico)
                .build();

        List<OsHistoricoStatus> historico = new ArrayList<>();
        historico.add(evento);

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey(StatusOrdemServico.RECEBIDA.name()));
        assertTrue(resultado.containsKey(StatusOrdemServico.EM_DIAGNOSTICO.name()));
    }

    @Test
    @DisplayName("Deve calcular tempo por status com múltiplos históricos")
    void testCalcularTempoPorStatusComMultiplosHistoricos() {
        LocalDateTime mudanca1 = ordemServico.getDataCriacao().plusHours(2);
        LocalDateTime mudanca2 = mudanca1.plusHours(1);

        OsHistoricoStatus evento1 = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(mudanca1)
                .ordemServico(ordemServico)
                .build();

        OsHistoricoStatus evento2 = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.EM_DIAGNOSTICO)
                .statusDestino(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .dataMudanca(mudanca2)
                .ordemServico(ordemServico)
                .build();

        List<OsHistoricoStatus> historico = new ArrayList<>();
        historico.add(evento1);
        historico.add(evento2);

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey(StatusOrdemServico.RECEBIDA.name()));
        assertTrue(resultado.containsKey(StatusOrdemServico.EM_DIAGNOSTICO.name()));
        assertTrue(resultado.containsKey(StatusOrdemServico.AGUARDANDO_APROVACAO.name()));
    }

    @Test
    @DisplayName("Deve manter status em andamento quando OS não foi finalizada")
    void testMantendoStatusEmAndamento() {
        LocalDateTime mudanca = ordemServico.getDataCriacao().plusHours(2);
        OsHistoricoStatus evento = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_EXECUCAO)
                .dataMudanca(mudanca)
                .ordemServico(ordemServico)
                .build();

        List<OsHistoricoStatus> historico = new ArrayList<>();
        historico.add(evento);

        ordemServico.setStatus(StatusOrdemServico.EM_EXECUCAO);
        ordemServico.setDataFinalizacao(null);

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertTrue(resultado.get(StatusOrdemServico.EM_EXECUCAO.name()).contains("em andamento"));
    }

    @Test
    @DisplayName("Deve remover status 'em andamento' quando OS foi finalizada")
    void testRemovendoStatusEmAndamentoAposFinalizar() {
        LocalDateTime mudanca = ordemServico.getDataCriacao().plusHours(2);
        LocalDateTime finalizado = mudanca.plusHours(1);

        OsHistoricoStatus evento = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_EXECUCAO)
                .dataMudanca(mudanca)
                .ordemServico(ordemServico)
                .build();

        List<OsHistoricoStatus> historico = new ArrayList<>();
        historico.add(evento);

        ordemServico.setStatus(StatusOrdemServico.FINALIZADA);
        ordemServico.setDataFinalizacao(finalizado);

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertFalse(resultado.get(StatusOrdemServico.EM_EXECUCAO.name()).contains("em andamento"));
    }

    @Test
    @DisplayName("Deve formatar tempo com dias, horas e minutos")
    void testFormatarTempoDiasHorasMinutos() {
        ordemServico.setDataCriacao(referencia.minusDays(2).minusHours(3).minusMinutes(45));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("2 dias"));
        assertTrue(resultado.contains("3 horas"));
        assertTrue(resultado.contains("45 minutos"));
    }

    @Test
    @DisplayName("Deve formatar tempo com singular quando apropriado")
    void testFormatarTempoSingular() {
        ordemServico.setDataCriacao(referencia.minusDays(1).minusHours(1).minusMinutes(1));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("1 dia"));
        assertTrue(resultado.contains("1 hora"));
        assertTrue(resultado.contains("1 minuto"));
    }

    @Test
    @DisplayName("Deve retornar zero minutos quando tempo é exato")
    void testFormatarTempoZeroMinutos() {
        LocalDateTime inicio = referencia;
        ordemServico.setDataCriacao(inicio);

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("0 minuto"));
    }

    @Test
    @DisplayName("Deve tratar duração negativa como zero")
    void testTratarDuracaoNegativa() {
        ordemServico.setDataCriacao(referencia.plusDays(1));

        String resultado = TempoAtendimentoDomainService.calcularTempoTotalFormatado(ordemServico, referencia);

        assertTrue(resultado.contains("0 minuto"));
    }

    @Test
    @DisplayName("Deve acumular tempos para mesmo status")
    void testAcumularTemposMesmoStatus() {
        LocalDateTime mudanca1 = ordemServico.getDataCriacao().plusHours(2);
        LocalDateTime mudanca2 = mudanca1.plusHours(1);
        LocalDateTime mudanca3 = mudanca2.plusHours(2);

        OsHistoricoStatus evento1 = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(mudanca1)
                .build();

        OsHistoricoStatus evento2 = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.EM_DIAGNOSTICO)
                .statusDestino(StatusOrdemServico.RECEBIDA)
                .dataMudanca(mudanca2)
                .build();

        OsHistoricoStatus evento3 = OsHistoricoStatus.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(mudanca3)
                .build();

        List<OsHistoricoStatus> historico = new ArrayList<>();
        historico.add(evento1);
        historico.add(evento2);
        historico.add(evento3);

        ordemServico.setDataFinalizacao(mudanca3.plusHours(1));

        Map<String, String> resultado = TempoAtendimentoDomainService.calcularTempoPorStatusFormatado(
                ordemServico, historico, referencia);

        assertNotNull(resultado.get(StatusOrdemServico.RECEBIDA.name()));
        assertNotNull(resultado.get(StatusOrdemServico.EM_DIAGNOSTICO.name()));
    }
}
