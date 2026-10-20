package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.ConcluirOrcamentoRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConcluirOrcamentoService")
class ConcluirOrcamentoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private OrdemServicoNotificacaoService notificacaoService;

    @InjectMocks
    private ConcluirOrcamentoService service;

    @Nested
    @DisplayName("concluirEEnviar")
    class ConcluirEEnviar {

        @Test
        @DisplayName("deve lançar exceção quando OS não encontrada para o orçamento")
        void deveLancarExcecaoQuandoOsNaoEncontrada() {
            ConcluirOrcamentoRequestDTO request = new ConcluirOrcamentoRequestDTO(
                    1L, "cliente@email.com", LocalDateTime.now().plusDays(5));

            when(ordemServicoRepository.buscarPorOrcamentoId(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.concluirEEnviar(request))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("Ordem de serviço não encontrada para o orçamento");
        }

        @Test
        @DisplayName("deve concluir o diagnóstico, salvar histórico e enviar e-mail de aprovação")
        void deveConcluirEEnviarComSucesso() {
            OsOrcamento orc = OsOrcamento.builder()
                    .id(1L).tipo(TipoOrcamento.INICIAL)
                    .valorTotal(BigDecimal.valueOf(500))
                    .dataCriacao(LocalDateTime.now()).build();

            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNome("Joao");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Gol");

            List<OsOrcamento> orcamentos = new ArrayList<>();
            orcamentos.add(orc);

            OrdemServico os = OrdemServico.builder()
                    .id(10L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.EM_DIAGNOSTICO).valorTotalAcumulado(BigDecimal.ZERO)
                    .dataCriacao(LocalDateTime.now()).orcamentos(orcamentos).build();

            ConcluirOrcamentoRequestDTO request = new ConcluirOrcamentoRequestDTO(
                    1L, "cliente@email.com", LocalDateTime.now().plusDays(5));

            when(ordemServicoRepository.buscarPorOrcamentoId(1L)).thenReturn(Optional.of(os));
            when(ordemServicoRepository.salvar(any())).thenReturn(os);

            service.concluirEEnviar(request);

            verify(ordemServicoRepository, times(1)).salvar(any());
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
            verify(notificacaoService, times(1))
                    .enviarEmailAprovacaoOrcamento(any(), any(), eq("cliente@email.com"));
            verify(notificacaoService, times(1))
                    .notificarMudancaStatus(any(), eq(StatusOrdemServico.EM_DIAGNOSTICO));
        }
    }
}
