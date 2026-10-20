package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OrdemServicoRepository;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusJpaEntity;
import com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence.OsHistoricoStatusRepository;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsHistoricoStatus;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OsHistoricoStatusRepositoryAdapter")
class OsHistoricoStatusRepositoryAdapterTest {

    @Mock
    private OsHistoricoStatusRepository repository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private OsHistoricoStatusRepositoryAdapter adapter;

    private OrdemServico ordemServico;
    private OrdemServicoJpaEntity ordemServicoEntity;
    private OsHistoricoStatus historico;
    private OsHistoricoStatusJpaEntity historicoEntity;

    @BeforeEach
    void setUp() {
        ordemServico = OrdemServico.builder()
                .id(1L)
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        ordemServicoEntity = OrdemServicoJpaEntity.builder()
                .id(1L)
                .status(StatusOrdemServico.RECEBIDA)
                .build();

        historico = OsHistoricoStatus.builder()
                .id(UUID.randomUUID())
                .ordemServico(ordemServico)
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(LocalDateTime.now())
                .build();

        historicoEntity = OsHistoricoStatusJpaEntity.builder()
                .id(historico.getId())
                .ordemServico(ordemServicoEntity)
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(historico.getDataMudanca())
                .build();
    }

    @Test
    @DisplayName("Deve salvar um histórico de status")
    void testSalvar() {
        when(ordemServicoRepository.getReferenceById(1L)).thenReturn(ordemServicoEntity);
        when(repository.save(any(OsHistoricoStatusJpaEntity.class))).thenReturn(historicoEntity);

        OsHistoricoStatus resultado = adapter.salvar(historico);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatusDestino()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(repository, times(1)).save(any(OsHistoricoStatusJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar históricos por lista de ids de ordens com ordenação")
    void testBuscarPorOrdensServicoOrdenado() {
        List<Long> ordemIds = List.of(1L, 2L, 3L);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds)).thenReturn(List.of(historicoEntity));

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getStatusDestino()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ids é null")
    void testBuscarPorOrdensServicoOrdenadoComListaNula() {
        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(null);

        assertThat(resultado).isEmpty();
        verify(repository, never()).findByOrdemServicoIdsOrderedWithUsuario(anyList());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando lista de ids está vazia")
    void testBuscarPorOrdensServicoOrdenadoComListaVazia() {
        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(List.of());

        assertThat(resultado).isEmpty();
        verify(repository, never()).findByOrdemServicoIdsOrderedWithUsuario(anyList());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não encontrar históricos")
    void testBuscarPorOrdensServicoOrdenadoVazio() {
        List<Long> ordemIds = List.of(1L, 2L);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds)).thenReturn(List.of());

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).isEmpty();
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }

    @Test
    @DisplayName("Deve retornar múltiplos históricos ordenados")
    void testBuscarPorOrdensServicoOrdenadoMultiplos() {
        OsHistoricoStatusJpaEntity historicoEntity2 = OsHistoricoStatusJpaEntity.builder()
                .id(UUID.randomUUID())
                .ordemServico(OrdemServicoJpaEntity.builder().id(2L).build())
                .statusDestino(StatusOrdemServico.AGUARDANDO_APROVACAO)
                .dataMudanca(LocalDateTime.now())
                .build();

        List<Long> ordemIds = List.of(1L, 2L);
        when(repository.findByOrdemServicoIdsOrderedWithUsuario(ordemIds))
                .thenReturn(List.of(historicoEntity, historicoEntity2));

        List<OsHistoricoStatus> resultado = adapter.buscarPorOrdensServicoOrdenado(ordemIds);

        assertThat(resultado).hasSize(2);
        verify(repository, times(1)).findByOrdemServicoIdsOrderedWithUsuario(ordemIds);
    }
}
