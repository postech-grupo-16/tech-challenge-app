package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.dto.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteVeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastroVeiculoService")
class CadastroVeiculoServiceTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteVeiculoRepositoryPort clienteVeiculoRepository;

    @InjectMocks
    private CadastroVeiculoService cadastroVeiculoService;

    private CadastroVeiculoRequest request;
    private Cliente cliente;
    private Veiculo veiculo;
    private UUID clienteId;
    private UUID veiculoId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        veiculoId = UUID.randomUUID();

        request = new CadastroVeiculoRequest("ABC1234", "Fiat", "Uno", 2020, "Branco", "12345678901");

        cliente = Cliente.builder()
                .id(clienteId)
                .nome("João Silva")
                .cpfCnpj(new CpfCnpj("12345678901"))
                .build();

        veiculo = Veiculo.builder()
                .id(veiculoId)
                .placa(new Placa("ABC1234"))
                .modelo("Fiat Uno")
                .build();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("deve cadastrar veículo e criar vínculo com cliente com sucesso")
        void deveCadastrarComSucesso() {
            when(veiculoRepository.existePorPlaca(any(Placa.class))).thenReturn(false);
            when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.of(cliente));
            when(veiculoRepository.salvar(any(Veiculo.class))).thenReturn(veiculo);
            when(clienteVeiculoRepository.salvar(any(ClienteVeiculo.class))).thenReturn(new ClienteVeiculo());

            var response = cadastroVeiculoService.execute(request);

            assertThat(response.id()).isEqualTo(veiculoId);
            assertThat(response.placa()).isEqualTo("ABC1234");
            assertThat(response.modelo()).isEqualTo("Fiat Uno");
            assertThat(response.cpfCnpj()).isEqualTo("12345678901");
            verify(veiculoRepository).salvar(any(Veiculo.class));
            verify(clienteVeiculoRepository).salvar(any(ClienteVeiculo.class));
        }

        @Test
        @DisplayName("deve lançar VeiculoJaCadastradoException quando placa já existe")
        void deveLancarExcecaoQuandoPlacaJaExiste() {
            when(veiculoRepository.existePorPlaca(any(Placa.class))).thenReturn(true);

            assertThatThrownBy(() -> cadastroVeiculoService.execute(request))
                    .isInstanceOf(VeiculoJaCadastradoException.class);

            verify(clienteRepository, never()).buscarPorCpfCnpj(any());
            verify(veiculoRepository, never()).salvar(any());
        }

        @Test
        @DisplayName("deve lançar ClienteNaoEncontradoException quando cliente não existe")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            when(veiculoRepository.existePorPlaca(any(Placa.class))).thenReturn(false);
            when(clienteRepository.buscarPorCpfCnpj(any(CpfCnpj.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cadastroVeiculoService.execute(request))
                    .isInstanceOf(ClienteNaoEncontradoException.class);

            verify(veiculoRepository, never()).salvar(any());
            verify(clienteVeiculoRepository, never()).salvar(any());
        }
    }
}
