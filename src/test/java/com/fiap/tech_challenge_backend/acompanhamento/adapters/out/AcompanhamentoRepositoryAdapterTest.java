package com.fiap.tech_challenge_backend.acompanhamento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcompanhamentoRepositoryAdapter")
class AcompanhamentoRepositoryAdapterTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @InjectMocks
    private AcompanhamentoRepositoryAdapter adapter;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        ordemServico = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.RECEBIDA)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("buscarPorClienteId deve delegar à porta de saída de atendimento")
    void deveBuscarPorClienteId() {
        UUID clienteId = UUID.randomUUID();
        when(ordemServicoRepository.buscarPorClienteId(clienteId)).thenReturn(List.of(ordemServico));

        var resultado = adapter.buscarPorClienteId(clienteId);

        assertThat(resultado).containsExactly(ordemServico);
        verify(ordemServicoRepository).buscarPorClienteId(clienteId);
    }

    @Test
    @DisplayName("buscarPorId deve delegar à porta de saída de atendimento")
    void deveBuscarPorId() {
        when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(ordemServico));

        var resultado = adapter.buscarPorId(1L);

        assertThat(resultado).contains(ordemServico);
        verify(ordemServicoRepository).buscarPorId(1L);
    }

    @Test
    @DisplayName("buscarPorId deve retornar vazio quando a ordem não existe")
    void deveRetornarVazioQuandoNaoEncontrado() {
        when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        var resultado = adapter.buscarPorId(999L);

        assertThat(resultado).isEmpty();
    }
}
