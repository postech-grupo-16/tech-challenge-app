package com.fiap.tech_challenge_backend.atendimento.domain.services;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("TempoAtendimentoDomainService.tempoNoStatusAtual - Testes Unitários")
class TempoNoStatusAtualTest {

    private static final LocalDateTime CRIACAO = LocalDateTime.of(2026, 11, 1, 8, 0);

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServico = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataCriacao(CRIACAO)
                .build();
    }

    @Test
    @DisplayName("Sem histórico deve contar desde a criação")
    void semHistorico() {
        Duration tempo = TempoAtendimentoDomainService.tempoNoStatusAtual(
                ordemServico, List.of(), CRIACAO.plusHours(2));

        assertEquals(Duration.ofHours(2), tempo);
    }

    @Test
    @DisplayName("Deve contar desde a última mudança de status")
    void desdeUltimaMudanca() {
        List<OsHistoricoStatus> historico = List.of(
                evento(null, StatusOrdemServico.RECEBIDA, CRIACAO),
                evento(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, CRIACAO.plusHours(1)));

        Duration tempo = TempoAtendimentoDomainService.tempoNoStatusAtual(
                ordemServico, historico, CRIACAO.plusHours(4));

        assertEquals(Duration.ofHours(3), tempo);
    }

    @Test
    @DisplayName("Troca de mecânico no mesmo status não deve zerar o tempo")
    void ignoraTrocaDeMecanico() {
        List<OsHistoricoStatus> historico = List.of(
                evento(StatusOrdemServico.RECEBIDA, StatusOrdemServico.EM_DIAGNOSTICO, CRIACAO.plusHours(1)),
                evento(StatusOrdemServico.EM_DIAGNOSTICO, StatusOrdemServico.EM_DIAGNOSTICO, CRIACAO.plusHours(3)));

        Duration tempo = TempoAtendimentoDomainService.tempoNoStatusAtual(
                ordemServico, historico, CRIACAO.plusHours(4));

        assertEquals(Duration.ofHours(3), tempo);
    }

    @Test
    @DisplayName("Duração negativa deve virar zero")
    void negativoViraZero() {
        Duration tempo = TempoAtendimentoDomainService.tempoNoStatusAtual(
                ordemServico, List.of(), CRIACAO.minusHours(1));

        assertEquals(Duration.ZERO, tempo);
    }

    @Test
    @DisplayName("Sem data de início deve retornar nulo")
    void semDataDeInicio() {
        ordemServico.setDataCriacao(null);

        assertNull(TempoAtendimentoDomainService.tempoNoStatusAtual(ordemServico, List.of(), CRIACAO));
    }

    private OsHistoricoStatus evento(StatusOrdemServico origem, StatusOrdemServico destino, LocalDateTime data) {
        return OsHistoricoStatus.builder()
                .ordemServico(ordemServico)
                .statusOrigem(origem)
                .statusDestino(destino)
                .dataMudanca(data)
                .build();
    }
}
