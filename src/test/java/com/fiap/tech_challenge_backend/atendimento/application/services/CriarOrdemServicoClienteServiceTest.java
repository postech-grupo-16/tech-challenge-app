package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.CriarOrdemServicoClienteRequestDTO;
import com.fiap.tech_challenge_backend.atendimento.application.dto.OrdemServicoResponseDTO;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OrdemServicoRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.application.ports.out.OsHistoricoStatusRepositoryPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarOrdemServicoClienteService")
class CriarOrdemServicoClienteServiceTest {

    @Mock
    private OrdemServicoRepositoryPort ordemServicoRepository;

    @Mock
    private OsHistoricoStatusRepositoryPort osHistoricoStatusRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private OrdemServicoEnriquecimentoService enriquecimentoService;

    @InjectMocks
    private CriarOrdemServicoClienteService service;

    private static OrdemServicoResponseDTO dto(OrdemServico os) {
        return new OrdemServicoResponseDTO(os.getId(), null, null, null, null, os.getStatus(),
                os.getValorTotalAcumulado(), os.getQueixaCliente(), os.getObservacoes(),
                null, null, null, os.getUrgente(), List.of());
    }

    @Nested
    @DisplayName("criar")
    class Criar {

        @Test
        @DisplayName("deve criar ordem de serviço pelo cliente com sucesso")
        void deveCriarComSucesso() {
            CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                    "12345678901", "ABC1234", "Carro não inicia", "Verificar bateria", true);

            Cliente cliente = new Cliente();
            cliente.setNome("João Silva");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Ford Ka");

            OrdemServico osParaSalvar = OrdemServico.builder()
                    .id(1L)
                    .clienteId(cliente.getId())
                    .veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.RECEBIDA)
                    .queixaCliente("Carro não inicia")
                    .observacoes("Verificar bateria")
                    .valorTotalAcumulado(BigDecimal.ZERO)
                    .urgente(true)
                    .orcamentos(List.of())
                    .build();

            when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(cliente));
            when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.of(veiculo));
            when(idGeneratorService.gerarIdOrdemServico()).thenReturn(1L);
            when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);
            when(enriquecimentoService.montar(osParaSalvar)).thenReturn(dto(osParaSalvar));

            OrdemServicoResponseDTO resultado = service.criar(request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.status()).isEqualTo(StatusOrdemServico.RECEBIDA);
            verify(ordemServicoRepository, times(1)).salvar(any(OrdemServico.class));
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
        }

        @Test
        @DisplayName("deve criar ordem registrando queixa e observações do cliente")
        void deveCriarComObservacoes() {
            CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                    "98765432100", "XYZ9876", "Carro faz barulho", "Som estranho no motor", false);

            Cliente cliente = new Cliente();
            cliente.setNome("Maria Santos");

            Veiculo veiculo = new Veiculo();
            veiculo.setModelo("Honda Civic");

            OrdemServico osParaSalvar = OrdemServico.builder()
                    .id(2L)
                    .clienteId(cliente.getId())
                    .veiculoId(veiculo.getId())
                    .status(StatusOrdemServico.RECEBIDA)
                    .queixaCliente("Carro faz barulho")
                    .observacoes("Som estranho no motor")
                    .valorTotalAcumulado(BigDecimal.ZERO)
                    .urgente(false)
                    .orcamentos(List.of())
                    .build();

            when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(cliente));
            when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.of(veiculo));
            when(idGeneratorService.gerarIdOrdemServico()).thenReturn(2L);
            when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(osParaSalvar);
            when(enriquecimentoService.montar(osParaSalvar)).thenReturn(dto(osParaSalvar));

            OrdemServicoResponseDTO resultado = service.criar(request);

            assertThat(resultado.id()).isEqualTo(2L);
            verify(osHistoricoStatusRepository, times(1)).salvar(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando cliente não encontrado pelo CPF/CNPJ")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                    "12345678901", "ABC1234", "Queixa", "Obs", false);

            when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cliente não encontrado");

            verify(ordemServicoRepository, org.mockito.Mockito.never()).salvar(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando veículo não encontrado pela placa")
        void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
            CriarOrdemServicoClienteRequestDTO request = new CriarOrdemServicoClienteRequestDTO(
                    "12345678901", "ABC1234", "Queixa", "Obs", false);

            when(clienteRepository.buscarPorCpfCnpj(any())).thenReturn(Optional.of(new Cliente()));
            when(veiculoRepository.buscarPorPlaca(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Veículo não encontrado");

            verify(ordemServicoRepository, org.mockito.Mockito.never()).salvar(any());
        }
    }
}
