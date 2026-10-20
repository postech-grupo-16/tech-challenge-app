package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutorizarOrdemServicoService")
class AutorizarOrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private OrdemServicoNotificacaoService notificacaoService;

    @InjectMocks
    private AutorizarOrdemServicoService service;

    @Nested
    @DisplayName("autorizar")
    class Autorizar {

        @Test
        @DisplayName("deve lançar exceção quando OS não encontrada")
        void deveLancarExcecaoQuandoOsNaoEncontrada() {
            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.autorizar(999L))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("Ordem de serviço não encontrada");
        }

        @Test
        @DisplayName("deve autorizar a OS pelo cliente, salvar histórico e notificar")
        void deveAutorizarComSucesso() {
            OrdemServico os = OrdemServico.builder()
                    .id(1L).status(StatusOrdemServico.AGUARDANDO_APROVACAO)
                    .valorTotalAcumulado(BigDecimal.ZERO).orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
            when(ordemServicoRepository.salvar(any())).thenReturn(os);

            service.autorizar(1L);

            verify(ordemServicoRepository, times(1)).salvar(any());
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
            verify(notificacaoService, times(1))
                    .notificarMudancaStatus(any(), eq(StatusOrdemServico.AGUARDANDO_APROVACAO));
        }
    }
}
