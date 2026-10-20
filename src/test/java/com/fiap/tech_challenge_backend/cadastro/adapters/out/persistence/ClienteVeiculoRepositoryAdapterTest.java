package com.fiap.tech_challenge_backend.cadastro.adapters.out.persistence;

import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("ClienteVeiculoRepositoryAdapter")
class ClienteVeiculoRepositoryAdapterTest {

    private ClienteVeiculoJpaRepository jpaRepository;
    private ClienteVeiculoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(ClienteVeiculoJpaRepository.class);
        adapter = new ClienteVeiculoRepositoryAdapter(jpaRepository);
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @Test
        @DisplayName("delega para jpaRepository.save e retorna resultado mapeado")
        void delegaParaSave() {
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();

            ClienteVeiculo entrada = ClienteVeiculo.builder()
                    .clienteId(clienteId)
                    .veiculoId(veiculoId)
                    .ativo(true)
                    .build();

            ClienteVeiculoJpaEntity salvo = ClienteVeiculoJpaEntity.builder()
                    .clienteId(clienteId)
                    .veiculoId(veiculoId)
                    .ativo(true)
                    .build();

            when(jpaRepository.save(any(ClienteVeiculoJpaEntity.class))).thenReturn(salvo);

            ClienteVeiculo resultado = adapter.salvar(entrada);

            assertThat(resultado).isEqualTo(entrada);
        }

        @Test
        @DisplayName("repassa o objeto mapeado sem modificacoes para o jpaRepository")
        void repassaObjetoSemModificacoes() {
            ClienteVeiculo cv = ClienteVeiculo.builder()
                    .clienteId(UUID.randomUUID())
                    .veiculoId(UUID.randomUUID())
                    .ativo(false)
                    .build();

            when(jpaRepository.save(any(ClienteVeiculoJpaEntity.class)))
                    .thenReturn(ClienteVeiculoMapper.toEntity(cv));

            adapter.salvar(cv);

            verify(jpaRepository, times(1)).save(ClienteVeiculoMapper.toEntity(cv));
        }
    }

    @Nested
    @DisplayName("existeVinculoAtivo")
    class ExisteVinculoAtivo {

        @Test
        @DisplayName("retorna true quando vinculo ativo existe")
        void retornaTrueQuandoExiste() {
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();

            when(jpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, veiculoId))
                    .thenReturn(true);

            boolean resultado = adapter.existeVinculoAtivo(clienteId, veiculoId);

            assertThat(resultado).isTrue();
            verify(jpaRepository).existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, veiculoId);
        }

        @Test
        @DisplayName("retorna false quando vinculo ativo nao existe")
        void retornaFalseQuandoNaoExiste() {
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();

            when(jpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, veiculoId))
                    .thenReturn(false);

            boolean resultado = adapter.existeVinculoAtivo(clienteId, veiculoId);

            assertThat(resultado).isFalse();
            verify(jpaRepository).existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, veiculoId);
        }

        @Test
        @DisplayName("passa exatamente os ids recebidos para o jpaRepository")
        void passaIdsCorretamente() {
            UUID clienteId = UUID.randomUUID();
            UUID veiculoId = UUID.randomUUID();
            UUID outroId = UUID.randomUUID();

            when(jpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, veiculoId))
                    .thenReturn(true);
            when(jpaRepository.existsByClienteIdAndVeiculoIdAndAtivoTrue(clienteId, outroId))
                    .thenReturn(false);

            assertThat(adapter.existeVinculoAtivo(clienteId, veiculoId)).isTrue();
            assertThat(adapter.existeVinculoAtivo(clienteId, outroId)).isFalse();
        }
    }

    @Nested
    @DisplayName("deletarPorVeiculoId")
    class DeletarPorVeiculoId {

        @Test
        @DisplayName("delega para jpaRepository.deleteByVeiculoId")
        void delegaParaDeleteByVeiculoId() {
            UUID veiculoId = UUID.randomUUID();

            doNothing().when(jpaRepository).deleteByVeiculoId(veiculoId);

            adapter.deletarPorVeiculoId(veiculoId);

            verify(jpaRepository).deleteByVeiculoId(veiculoId);
        }

        @Test
        @DisplayName("passa o veiculoId correto para o jpaRepository")
        void passaVeiculoIdCorreto() {
            UUID veiculoId = UUID.randomUUID();
            UUID outroId = UUID.randomUUID();

            adapter.deletarPorVeiculoId(veiculoId);

            verify(jpaRepository).deleteByVeiculoId(veiculoId);
            verify(jpaRepository, never()).deleteByVeiculoId(outroId);
        }
    }
}
