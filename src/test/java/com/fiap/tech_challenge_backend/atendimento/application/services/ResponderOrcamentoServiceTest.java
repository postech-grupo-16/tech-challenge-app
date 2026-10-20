package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.AprovarRejeitarOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrcamentoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResponderOrcamentoService")
class ResponderOrcamentoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private OrdemServicoNotificacaoService notificacaoService;

    @InjectMocks
    private ResponderOrcamentoService service;

    private OrdemServico osComOrcamentoPendente() {
        OsOrcamento orcamento = OsOrcamento.builder()
                .id(1L).tipo(TipoOrcamento.INICIAL).status(StatusOrcamento.PENDENTE)
                .valorTotal(BigDecimal.valueOf(500)).dataCriacao(LocalDateTime.now())
                .build();

        List<OsOrcamento> orcamentos = new ArrayList<>();
        orcamentos.add(orcamento);

        return OrdemServico.builder()
                .id(10L).status(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .valorTotalAcumulado(BigDecimal.ZERO)
                .orcamentos(orcamentos).build();
    }

    @Nested
    @DisplayName("responder")
    class Responder {

        @Test
        @DisplayName("deve aprovar o orçamento, salvar histórico e notificar aprovação")
        void deveAprovarComSucesso() {
            OrdemServico os = osComOrcamentoPendente();
            when(ordemServicoRepository.buscarPorId(10L)).thenReturn(Optional.of(os));
            lenient().when(ordemServicoRepository.salvar(any(OrdemServico.class)))
                    .thenAnswer((Answer<OrdemServico>) inv -> inv.getArgument(0));

            OrcamentoResponseDTO resultado = service.responder(
                    10L, 1L, new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO));

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusOrcamento.APROVADO);
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
            verify(notificacaoService, times(1))
                    .notificarMudancaStatus(any(), eq(StatusOrdemServico.AGUARDANDO_APROVACAO));
            verify(notificacaoService, times(1))
                    .notificarRespostaOrcamento(any(), any(), eq(true));
        }

        @Test
        @DisplayName("deve rejeitar o orçamento e notificar rejeição, sem alterar o histórico de status da OS")
        void deveRejeitarComSucesso() {
            OrdemServico os = osComOrcamentoPendente();
            os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
            // orçamento ADICIONAL para permitir rejeição (INICIAL não pode ser rejeitado)
            os.getOrcamentos().set(0, OsOrcamento.builder()
                    .id(1L).tipo(TipoOrcamento.ADICIONAL).status(StatusOrcamento.PENDENTE)
                    .valorTotal(BigDecimal.valueOf(200)).dataCriacao(LocalDateTime.now())
                    .build());

            when(ordemServicoRepository.buscarPorId(10L)).thenReturn(Optional.of(os));
            lenient().when(ordemServicoRepository.salvar(any(OrdemServico.class)))
                    .thenAnswer((Answer<OrdemServico>) inv -> inv.getArgument(0));

            OrcamentoResponseDTO resultado = service.responder(
                    10L, 1L, new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.REJEITADO));

            assertThat(resultado).isNotNull();
            assertThat(resultado.status()).isEqualTo(StatusOrcamento.REJEITADO);
            verify(osHistoricoStatusRepository, never()).salvar(any());
            verify(notificacaoService, times(1))
                    .notificarRespostaOrcamento(any(), any(), eq(false));
        }

        @Test
        @DisplayName("deve lançar exceção quando a ordem de serviço não existe")
        void deveLancarExcecaoQuandoOrdemNaoExiste() {
            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.responder(
                    999L, 1L, new AprovarRejeitarOrcamentoRequestDTO(StatusOrcamento.APROVADO)))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class);
        }
    }
}
