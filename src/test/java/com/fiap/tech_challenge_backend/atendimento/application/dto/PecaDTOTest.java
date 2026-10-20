package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PecaDTO")
class PecaDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto = new PecaDTO(
                id,
                pecaId,
                "Pneu Michelin",
                4,
                BigDecimal.valueOf(300.00)
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.pecaId()).isEqualTo(pecaId);
        assertThat(dto.nome()).isEqualTo("Pneu Michelin");
        assertThat(dto.quantidade()).isEqualTo(4);
        assertThat(dto.precoVendaAplicado()).isEqualByComparingTo(BigDecimal.valueOf(300.00));
    }

    @Test
    @DisplayName("Deve criar instância com quantidade zero")
    void testCriarComQuantidadeZero() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto = new PecaDTO(
                id,
                pecaId,
                "Filtro Ar",
                0,
                BigDecimal.valueOf(50.00)
        );

        assertThat(dto).isNotNull();
        assertThat(dto.quantidade()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto1 = new PecaDTO(id, pecaId, "Óleo Motor", 2, BigDecimal.valueOf(120.00));
        PecaDTO dto2 = new PecaDTO(id, pecaId, "Óleo Motor", 2, BigDecimal.valueOf(120.00));

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto1 = new PecaDTO(UUID.randomUUID(), pecaId, "Óleo", 2, BigDecimal.valueOf(120.00));
        PecaDTO dto2 = new PecaDTO(UUID.randomUUID(), pecaId, "Óleo", 2, BigDecimal.valueOf(120.00));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto = new PecaDTO(id, pecaId, "Pastilha Freio", 4, BigDecimal.valueOf(180.00));
        String toString = dto.toString();

        assertThat(toString).contains("PecaDTO");
        assertThat(toString).contains("Pastilha");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID id = UUID.randomUUID();
        UUID pecaId = UUID.randomUUID();

        PecaDTO dto1 = new PecaDTO(id, pecaId, "Óleo", 2, BigDecimal.valueOf(120.00));
        PecaDTO dto2 = new PecaDTO(id, pecaId, "Óleo", 2, BigDecimal.valueOf(120.00));

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
