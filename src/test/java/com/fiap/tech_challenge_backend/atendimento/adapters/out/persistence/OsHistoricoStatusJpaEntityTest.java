package com.fiap.tech_challenge_backend.atendimento.adapters.out.persistence;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OsHistoricoStatusJpaEntity.prePersist")
class OsHistoricoStatusJpaEntityTest {

    @Test
    @DisplayName("Deve inicializar dataMudanca ao persistir se null")
    void testPrePersistComDataMudancaNula() {
        OsHistoricoStatusJpaEntity novo = OsHistoricoStatusJpaEntity.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .build();
        novo.prePersist();

        assertNotNull(novo.getDataMudanca());
    }

    @Test
    @DisplayName("Deve manter dataMudanca existente ao persistir")
    void testPrePersistComDataMudancaExistente() {
        LocalDateTime data = LocalDateTime.now().minusDays(1);
        OsHistoricoStatusJpaEntity novo = OsHistoricoStatusJpaEntity.builder()
                .statusOrigem(StatusOrdemServico.RECEBIDA)
                .statusDestino(StatusOrdemServico.EM_DIAGNOSTICO)
                .dataMudanca(data)
                .build();
        novo.prePersist();

        assertEquals(data, novo.getDataMudanca());
    }
}
