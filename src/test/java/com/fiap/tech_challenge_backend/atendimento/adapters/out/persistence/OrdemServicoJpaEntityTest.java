package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrdemServicoJpaEntity - Ciclo de vida de persistência")
class OrdemServicoJpaEntityTest {

    @Test
    @DisplayName("Deve atualizar datas ao mudar para EM_EXECUCAO")
    void testAtualizarDataInicioExecucao() {
        OrdemServicoJpaEntity entity = OrdemServicoJpaEntity.builder()
                .id(1L)
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .status(StatusOrdemServico.EM_EXECUCAO)
                .dataInicioExecucao(null)
                .build();

        entity.preUpdate();

        assertNotNull(entity.getDataInicioExecucao());
    }

    @Test
    @DisplayName("Deve atualizar data de finalização ao mudar para FINALIZADA")
    void testAtualizarDataFinalizacao() {
        OrdemServicoJpaEntity entity = OrdemServicoJpaEntity.builder()
                .id(1L)
                .clienteId(UUID.randomUUID())
                .veiculoId(UUID.randomUUID())
                .status(StatusOrdemServico.FINALIZADA)
                .dataFinalizacao(null)
                .build();

        entity.preUpdate();

        assertNotNull(entity.getDataFinalizacao());
    }
}
