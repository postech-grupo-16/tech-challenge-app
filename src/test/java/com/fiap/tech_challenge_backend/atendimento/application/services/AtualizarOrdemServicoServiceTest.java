package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.dto.DeletarOrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoAtualizarRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.ReferenciaNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarOrdemServicoService")
class AtualizarOrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private AtualizarOrdemServicoService service;

    private UUID clienteId;
    private UUID veiculoId;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();

        cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("João Silva");

        veiculo = new Veiculo();
        veiculo.setId(veiculoId);
    }

    private OrdemServicoResponseDTO dtoComStatus(StatusOrdemServico status) {
        return new OrdemServicoResponseDTO(1L, null, null, null, null, status,
                BigDecimal.ZERO, null, null, null, null, null, false, List.of());
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar ordem de serviço com sucesso")
        void deveAtualizarComSucesso() {
            UUID mecanicoId = UUID.randomUUID();
            OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                    clienteId, veiculoId, mecanicoId, StatusOrdemServico.EM_DIAGNOSTICO, null, null, false);

            Usuario mecanico = new Usuario();
            mecanico.setId(mecanicoId);

            OrdemServico osAtual = OrdemServico.builder()
                    .id(1L).clienteId(clienteId).veiculoId(veiculoId).mecanicoId(mecanicoId)
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            OrdemServico osAtualizada = OrdemServico.builder()
                    .id(1L).clienteId(clienteId).veiculoId(veiculoId).mecanicoId(mecanicoId)
                    .status(StatusOrdemServico.EM_DIAGNOSTICO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
            when(enriquecimentoService.resolverCliente(clienteId)).thenReturn(cliente);
            when(enriquecimentoService.resolverVeiculo(veiculoId)).thenReturn(veiculo);
            when(enriquecimentoService.resolverMecanicoObrigatorio(mecanicoId)).thenReturn(mecanico);
            when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osAtualizada);
            when(enriquecimentoService.montar(osAtualizada)).thenReturn(dtoComStatus(StatusOrdemServico.EM_DIAGNOSTICO));

            OrdemServicoResponseDTO resultado = service.atualizar(1L, request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
            verify(ordemServicoRepository, times(1)).salvar(any(OrdemServico.class));
        }

        @Test
        @DisplayName("deve atualizar status sem exigir mecânico quando não informado")
        void deveAtualizarStatusSemMecanico() {
            OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                    clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), null, true);

            OrdemServico osAtual = OrdemServico.builder()
                    .id(1L).clienteId(clienteId).veiculoId(veiculoId)
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            OrdemServico osAtualizada = OrdemServico.builder()
                    .id(1L).clienteId(clienteId).veiculoId(veiculoId)
                    .status(StatusOrdemServico.EM_EXECUCAO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
            when(enriquecimentoService.resolverCliente(clienteId)).thenReturn(cliente);
            when(enriquecimentoService.resolverVeiculo(veiculoId)).thenReturn(veiculo);
            when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osAtualizada);
            when(enriquecimentoService.montar(osAtualizada)).thenReturn(dtoComStatus(StatusOrdemServico.EM_EXECUCAO));

            OrdemServicoResponseDTO resultado = service.atualizar(1L, request);

            assertThat(resultado.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        }

        @Test
        @DisplayName("deve lançar exceção quando cliente não encontrado")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                    clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO, null, null, true);

            OrdemServico osAtual = OrdemServico.builder()
                    .id(1L).clienteId(clienteId).status(StatusOrdemServico.RECEBIDA).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(osAtual));
            when(enriquecimentoService.resolverCliente(clienteId))
                    .thenThrow(new ReferenciaNaoEncontradaException("Cliente não encontrado: " + clienteId));

            assertThatThrownBy(() -> service.atualizar(1L, request))
                    .isInstanceOf(ReferenciaNaoEncontradaException.class);
        }

        @Test
        @DisplayName("deve lançar exceção quando a ordem de serviço não existe")
        void deveLancarExcecaoQuandoOrdemNaoExiste() {
            OrdemServicoAtualizarRequestDTO request = new OrdemServicoAtualizarRequestDTO(
                    clienteId, veiculoId, null, StatusOrdemServico.EM_EXECUCAO, null, null, true);

            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.atualizar(999L, request))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class);
        }
    }

    @Nested
    @DisplayName("remover")
    class Remover {

        @Test
        @DisplayName("deve remover ordem de serviço existente")
        void deveRemoverComSucesso() {
            when(ordemServicoRepository.existePorId(1L)).thenReturn(true);
            doNothing().when(ordemServicoRepository).remover(1L);

            DeletarOrdemServicoResponseDTO resultado = service.remover(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.status()).isEqualTo("DELETADO");
            verify(ordemServicoRepository, times(1)).remover(1L);
        }

        @Test
        @DisplayName("deve lançar exceção ao remover ordem inexistente")
        void deveLancarExcecaoAoRemoverInexistente() {
            when(ordemServicoRepository.existePorId(999L)).thenReturn(false);

            assertThatThrownBy(() -> service.remover(999L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("Ordem de serviço não encontrada");

            verify(ordemServicoRepository, never()).remover(anyLong());
        }
    }
}
