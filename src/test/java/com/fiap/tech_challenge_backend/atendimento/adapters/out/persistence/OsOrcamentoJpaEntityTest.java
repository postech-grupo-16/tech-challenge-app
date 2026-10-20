package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OsOrcamentoJpaEntity.prePersist")
class OsOrcamentoJpaEntityTest {

    @Test
    @DisplayName("Deve inicializar dataCriacao ao persistir")
    void testInitiateDataCriacao() {
        OsOrcamentoJpaEntity novo = new OsOrcamentoJpaEntity();
        novo.prePersist();

        assertNotNull(novo.getDataCriacao());
    }

    @Test
    @DisplayName("Deve manter dataCriacao existente ao persistir")
    void testMantendoDataCriacaoExistente() {
        LocalDateTime data = LocalDateTime.now().minusDays(1);
        OsOrcamentoJpaEntity novo = OsOrcamentoJpaEntity.builder()
                .dataCriacao(data)
                .build();
        novo.prePersist();

        assertEquals(data, novo.getDataCriacao());
    }
}
