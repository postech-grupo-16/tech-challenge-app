package com.fiap.tech_challenge_backend.cadastro.application.services;

import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.ClienteVeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.application.ports.out.VeiculoRepositoryPort;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("DeletarVeiculoService")
class DeletarVeiculoServiceTest {

    @Mock
    private VeiculoRepositoryPort veiculoRepository;

    @Mock
    private ClienteVeiculoRepositoryPort clienteVeiculoRepository;

    @InjectMocks
    private DeletarVeiculoService deletarVeiculoService;

    private UUID veiculoId;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculoId = UUID.randomUUID();
        veiculo = Veiculo.builder()
                .id(veiculoId)
                .placa(new Placa("ABC1234"))
                .modelo("Fiesta")
                .build();
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("marca o veículo como excluído e desativa o vínculo com o cliente")
        void deletaVeiculoEDesativaVinculo() {
            when(veiculoRepository.buscarPorPlaca(new Placa("ABC1234")))
                    .thenReturn(Optional.of(veiculo));
            when(veiculoRepository.salvar(any(Veiculo.class))).thenAnswer(inv -> inv.getArgument(0));

            deletarVeiculoService.execute("ABC1234");

            ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
            verify(veiculoRepository).salvar(captor.capture());
            assertThat(captor.getValue().getDeletedAt()).isNotNull();

            verify(clienteVeiculoRepository).deletarPorVeiculoId(veiculoId);
        }

        @Test
        @DisplayName("lança VeiculoNaoEncontradoException e não toca nos repositórios quando a placa não existe")
        void lancaExcecaoQuandoVeiculoNaoEncontrado() {
            when(veiculoRepository.buscarPorPlaca(new Placa("XXX9999")))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> deletarVeiculoService.execute("XXX9999"))
                    .isInstanceOf(VeiculoNaoEncontradoException.class);

            verify(veiculoRepository, never()).salvar(any());
            verify(clienteVeiculoRepository, never()).deletarPorVeiculoId(any());
        }
    }
}
