package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarOrdemServicoService")
class BuscarOrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private BuscarOrdemServicoService service;

    private static OrdemServicoResponseDTO dto(OrdemServico os) {
        return new OrdemServicoResponseDTO(os.getId(), null, null, null, null, os.getStatus(),
                os.getValorTotalAcumulado(), null, null, null, null, null, false, List.of());
    }

    private void stubMontarDinamico() {
        when(enriquecimentoService.montar(any(OrdemServico.class)))
                .thenAnswer(invocation -> dto(invocation.getArgument(0)));
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve buscar ordem de serviço por id")
        void deveBuscarPorId() {
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();

            OrdemServico os = OrdemServico.builder()
                    .id(1L)
                    .clienteId(clienteId)
                    .veiculoId(veiculoId)
                    .status(StatusOrdemServico.RECEBIDA)
                    .valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of())
                    .build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
            stubMontarDinamico();

            OrdemServicoResponseDTO resultado = service.buscarPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
            verify(ordemServicoRepository, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("deve lançar exceção quando ordem não encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(999L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("Ordem de serviço não encontrada");
        }
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("deve listar todas as ordens de serviço")
        void deveListarTodos() {
            OrdemServico os1 = OrdemServico.builder()
                    .id(1L).clienteId(UUID.randomUUID()).veiculoId(UUID.randomUUID())
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            OrdemServico os2 = OrdemServico.builder()
                    .id(2L).clienteId(UUID.randomUUID()).veiculoId(UUID.randomUUID())
                    .status(StatusOrdemServico.FINALIZADA).valorTotalAcumulado(BigDecimal.valueOf(500))
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.listarPriorizadas()).thenReturn(List.of(os1, os2));
            stubMontarDinamico();

            List<OrdemServicoResponseDTO> resultado = service.listarTodos();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).id()).isEqualTo(1L);
            assertThat(resultado.get(1).id()).isEqualTo(2L);
            verify(ordemServicoRepository, times(1)).listarPriorizadas();
        }

        @Test
        @DisplayName("deve listar múltiplas ordens preservando a ordem retornada pelo repositório")
        void deveListarMultiplasOrdens() {
            List<OrdemServico> ordens = new ArrayList<>();
            for (long i = 1; i <= 5; i++) {
                ordens.add(OrdemServico.builder()
                        .id(i).clienteId(UUID.randomUUID()).veiculoId(UUID.randomUUID())
                        .status(StatusOrdemServico.RECEBIDA)
                        .valorTotalAcumulado(BigDecimal.valueOf(i * 100))
                        .orcamentos(List.of()).build());
            }

            when(ordemServicoRepository.listarPriorizadas()).thenReturn(ordens);
            stubMontarDinamico();

            List<OrdemServicoResponseDTO> resultado = service.listarTodos();

            assertThat(resultado).hasSize(5);
            for (int i = 0; i < 5; i++) {
                assertThat(resultado.get(i).id()).isEqualTo(i + 1);
            }
        }
    }

    @Nested
    @DisplayName("listarAtivasPriorizadas")
    class ListarAtivasPriorizadas {

        @Test
        @DisplayName("deve listar ordens de serviço ativas priorizadas")
        void deveListarAtivasPriorizadas() {
            OrdemServico os1 = OrdemServico.builder()
                    .id(1L).clienteId(UUID.randomUUID()).veiculoId(UUID.randomUUID())
                    .status(StatusOrdemServico.EM_EXECUCAO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            OrdemServico os2 = OrdemServico.builder()
                    .id(2L).clienteId(UUID.randomUUID()).veiculoId(UUID.randomUUID())
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.valueOf(500))
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.listarAtivasPriorizadas()).thenReturn(List.of(os1, os2));
            stubMontarDinamico();

            List<OrdemServicoResponseDTO> resultado = service.listarAtivasPriorizadas();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
            assertThat(resultado.get(1).status()).isEqualTo(StatusOrdemServico.RECEBIDA);
            verify(ordemServicoRepository, times(1)).listarAtivasPriorizadas();
        }
    }
}
