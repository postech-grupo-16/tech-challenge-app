package com.fiap.tech_challenge_backend.acompanhamento.application.services;

import com.fiap.tech_challenge_backend.acompanhamento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.acompanhamento.application.ports.out.AcompanhamentoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcompanhamentoService")
class AcompanhamentoServiceTest {

    @Mock
    private AcompanhamentoRepositoryPort acompanhamentoRepository;

    @Mock
    private com.fiap.tech_challenge_backend.atendimento.application.services.OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private AcompanhamentoService service;

    private UUID clienteId;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        cliente = new Cliente();
        cliente.setId(clienteId);
        cliente.setNome("Joao");

        veiculo = new Veiculo();
        veiculo.setId(UUID.randomUUID());
        veiculo.setPlaca(new Placa("ABC1234"));
        veiculo.setModelo("Gol");

        org.mockito.Mockito.lenient().when(enriquecimentoService.resolverVeiculo(veiculo.getId())).thenReturn(veiculo);
    }

    @Nested
    @DisplayName("listarPorCliente")
    class ListarPorCliente {

        @Test
        @DisplayName("deve mapear as ordens do cliente para DTO")
        void deveMapearOrdensDoCliente() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(acompanhamentoRepository.buscarPorClienteId(clienteId)).thenReturn(List.of(os));

            var resultado = service.listarPorCliente(clienteId);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando o cliente nao tem ordens")
        void deveRetornarListaVazia() {
            when(acompanhamentoRepository.buscarPorClienteId(clienteId)).thenReturn(List.of());

            assertThat(service.listarPorCliente(clienteId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("buscarDetalhe")
    class BuscarDetalhe {

        @Test
        @DisplayName("deve retornar o detalhe quando a ordem pertence ao cliente")
        void deveRetornarDetalhe() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(acompanhamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

            var resultado = service.buscarDetalhe(clienteId, 1L);

            assertThat(resultado.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lancar excecao quando a ordem nao existe")
        void deveLancarExcecaoQuandoOsNaoExiste() {
            when(acompanhamentoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarDetalhe(clienteId, 999L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class);
        }

        @Test
        @DisplayName("deve lancar excecao quando a ordem pertence a outro cliente")
        void deveLancarExcecaoQuandoOrdemDeOutroCliente() {
            Cliente outroCliente = new Cliente();
            outroCliente.setId(UUID.randomUUID());
            outroCliente.setNome("Outro");

            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(outroCliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_EXECUCAO).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(acompanhamentoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.buscarDetalhe(clienteId, 1L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("cliente informado");
        }
    }
}
