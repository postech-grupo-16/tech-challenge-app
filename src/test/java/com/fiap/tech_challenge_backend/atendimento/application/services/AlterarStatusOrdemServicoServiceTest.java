package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.acesso.application.ports.out.UsuarioRepositoryPort;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.OrdemServicoNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.exceptions.ReferenciaNaoEncontradaException;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlterarStatusOrdemServicoService")
class AlterarStatusOrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private OrdemServicoNotificacaoService notificacaoService;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private AlterarStatusOrdemServicoService service;

    @Nested
    @DisplayName("alterarStatus")
    class AlterarStatus {

        @Test
        @DisplayName("deve retornar OS sem salvar quando status e mecânico não mudam")
        void deveRetornarAtualQuandoNadaMuda() {
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNome("Joao");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Gol");

            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));

            service.alterarStatus(1L, StatusOrdemServico.RECEBIDA, null, null);

            verify(ordemServicoRepository, never()).salvar(any());
            verify(osHistoricoStatusRepository, never()).salvar(any());
            verify(notificacaoService, never()).notificarMudancaStatus(any(), any());
        }

        @Test
        @DisplayName("deve alterar status com sucesso e notificar a mudança")
        void deveAlterarStatusComSucesso() {
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNome("Joao");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Gol");

            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            Usuario usuario = new Usuario();
            usuario.setId(UUID.randomUUID());
            usuario.setNome("Funcionario");

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
            when(usuarioRepository.procuraPorEmail(any())).thenReturn(Optional.of(usuario));
            when(ordemServicoRepository.salvar(any())).thenReturn(os);

            service.alterarStatus(1L, StatusOrdemServico.EM_EXECUCAO, null, "funcionario@email.com");

            verify(ordemServicoRepository, times(1)).salvar(any());
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
            verify(notificacaoService, times(1)).notificarMudancaStatus(any(), eq(StatusOrdemServico.RECEBIDA));
        }

        @Test
        @DisplayName("deve trocar o mecânico quando mecanicoId é fornecido")
        void deveTrocarMecanico() {
            UUID mecanicoId = UUID.randomUUID();

            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNome("Joao");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Gol");

            OrdemServico os = OrdemServico.builder()
                    .id(1L).clienteId(cliente.getId()).veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            Usuario mecanico = new Usuario();
            mecanico.setId(mecanicoId);
            mecanico.setNome("Carlos");

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
            when(usuarioRepository.procuraPorEmail(any())).thenReturn(Optional.empty());
            when(enriquecimentoService.resolverMecanicoObrigatorio(mecanicoId)).thenReturn(mecanico);
            when(ordemServicoRepository.salvar(any())).thenReturn(os);

            service.alterarStatus(1L, StatusOrdemServico.EM_EXECUCAO, mecanicoId, "func@email.com");

            verify(enriquecimentoService, times(1)).resolverMecanicoObrigatorio(mecanicoId);
            verify(ordemServicoRepository, times(1)).salvar(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando mecânico não encontrado")
        void deveLancarExcecaoQuandoMecanicoNaoEncontrado() {
            UUID mecanicoId = UUID.randomUUID();

            OrdemServico os = OrdemServico.builder()
                    .id(1L).status(StatusOrdemServico.RECEBIDA).valorTotalAcumulado(BigDecimal.ZERO)
                    .orcamentos(List.of()).build();

            when(ordemServicoRepository.buscarPorId(1L)).thenReturn(Optional.of(os));
            when(enriquecimentoService.resolverMecanicoObrigatorio(mecanicoId))
                    .thenThrow(new ReferenciaNaoEncontradaException("Mecânico não encontrado: " + mecanicoId));

            assertThatThrownBy(() -> service.alterarStatus(1L, StatusOrdemServico.EM_EXECUCAO, mecanicoId, null))
                    .isInstanceOf(ReferenciaNaoEncontradaException.class)
                    .hasMessageContaining("Mecânico não encontrado");
        }

        @Test
        @DisplayName("deve lançar exceção quando OS não encontrada")
        void deveLancarExcecaoQuandoOsNaoEncontrada() {
            when(ordemServicoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.alterarStatus(999L, StatusOrdemServico.EM_EXECUCAO, null, null))
                    .isInstanceOf(OrdemServicoNaoEncontradaException.class)
                    .hasMessageContaining("Ordem de serviço não encontrada");
        }
    }
}
