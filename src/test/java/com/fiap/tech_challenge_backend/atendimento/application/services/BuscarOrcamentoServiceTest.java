package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrcamentoNaoEncontradoException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarOrcamentoService")
class BuscarOrcamentoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @InjectMocks
    private BuscarOrcamentoService service;

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar o orçamento quando encontrado na ordem de serviço")
        void deveRetornarOrcamentoEncontrado() {
            OsOrcamento orcamento = OsOrcamento.builder()
                    .id(1L).tipo(TipoOrcamento.INICIAL).valorTotal(BigDecimal.valueOf(300))
                    .dataCriacao(LocalDateTime.now()).build();

            OrdemServico os = OrdemServico.builder()
                    .id(10L).status(StatusOrdemServico.AGUARDANDO_APROVACAO)
                    .valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of(orcamento)).build();

            when(ordemServicoRepository.buscarPorId(10L)).thenReturn(Optional.of(os));

            OrcamentoResponseDTO resultado = service.buscarPorId(10L, 1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar exceção quando a ordem de serviço não existe")
        void deveLancarExcecaoQuandoOrdemNaoExiste() {
            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(999L, 1L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class);
        }

        @Test
        @DisplayName("deve lançar exceção quando o orçamento não pertence à ordem de serviço")
        void deveLancarExcecaoQuandoOrcamentoNaoEncontrado() {
            OrdemServico os = OrdemServico.builder()
                    .id(10L).status(StatusOrdemServico.RECEBIDA)
                    .valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(10L)).thenReturn(Optional.of(os));

            assertThatThrownBy(() -> service.buscarPorId(10L, 999L))
                    .isInstanceOf(OrcamentoNaoEncontradoException.class)
                    .hasMessageContaining("Orçamento não encontrado");
        }
    }
}
