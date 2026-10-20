package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.RelatorioOsEnriquecidoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioOrdensServicoService")
class RelatorioOrdensServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private RelatorioEnriquecimentoService relatorioEnriquecimentoService;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private RelatorioOrdensServicoService service;

    @Nested
    @DisplayName("listarRelatorio")
    class ListarRelatorio {

        @Test
        @DisplayName("deve retornar lista vazia quando não há ordens")
        void deveRetornarVazioQuandoNaoHaOrdens() {
            when(ordemServicoRepository.listarParaRelatorio()).thenReturn(List.of());

            List<RelatorioOsEnriquecidoResponseDTO> resultado = service.listarRelatorio(null);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve montar relatório com OS e retornar DTOs enriquecidos")
        void deveMontarRelatorioComOrdens() {
            Cliente cliente = new Cliente();
            cliente.setNome("Joao Silva");

            OrdemServico os = OrdemServico.builder()
                    .id(1L)
                    .status(StatusOrdemServico.RECEBIDA).urgente(false)
                    .dataCriacao(LocalDateTime.now()).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            RelatorioOsEnriquecidoResponseDTO dto = new RelatorioOsEnriquecidoResponseDTO(
                    1L, "Joao Silva", "RECEBIDA", false, "0min", Map.of(), BigDecimal.ZERO, null, null, null, null);

            when(enriquecimentoService.resolverCliente(any())).thenReturn(cliente);
            when(ordemServicoRepository.listarParaRelatorio()).thenReturn(List.of(os));
            when(osHistoricoStatusRepository.buscarPorOrdensServicoOrdenado(List.of(1L))).thenReturn(List.of());
            when(relatorioEnriquecimentoService.enriquecer(any(), any(), any())).thenReturn(dto);

            List<RelatorioOsEnriquecidoResponseDTO> resultado = service.listarRelatorio(null);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("listarRelatorioPorStatus")
    class ListarRelatorioPorStatus {

        @Test
        @DisplayName("deve retornar lista vazia quando não há ordens no status")
        void deveRetornarVazioQuandoNaoHaOrdens() {
            when(ordemServicoRepository.listarPorStatus(StatusOrdemServico.RECEBIDA)).thenReturn(List.of());

            List<RelatorioOsEnriquecidoResponseDTO> resultado =
                    service.listarRelatorioPorStatus(StatusOrdemServico.RECEBIDA, null);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve montar relatório por status com OS e retornar DTOs enriquecidos")
        void deveMontarRelatorioComOrdens() {
            Cliente cliente = new Cliente();
            cliente.setNome("Maria");

            OrdemServico os = OrdemServico.builder()
                    .id(2L)
                    .status(StatusOrdemServico.EM_EXECUCAO).urgente(true)
                    .dataCriacao(LocalDateTime.now()).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            RelatorioOsEnriquecidoResponseDTO dto = new RelatorioOsEnriquecidoResponseDTO(
                    2L, "Maria", "EM_EXECUCAO", true, "1h", Map.of(), BigDecimal.ZERO, null, null, null, null);

            when(enriquecimentoService.resolverCliente(any())).thenReturn(cliente);
            when(ordemServicoRepository.listarPorStatus(StatusOrdemServico.EM_EXECUCAO)).thenReturn(List.of(os));
            when(osHistoricoStatusRepository.buscarPorOrdensServicoOrdenado(List.of(2L))).thenReturn(List.of());
            when(relatorioEnriquecimentoService.enriquecer(any(), any(), any())).thenReturn(dto);

            List<RelatorioOsEnriquecidoResponseDTO> resultado =
                    service.listarRelatorioPorStatus(StatusOrdemServico.EM_EXECUCAO, null);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(2L);
        }
    }
}
