package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PecaOrcamentoRequestDTO")
class PecaOrcamentoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com peça id e quantidade")
    void testCriarComPecaIdEQuantidade() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto = new PecaOrcamentoRequestDTO(pecaId, 5);

        assertThat(dto).isNotNull();
        assertThat(dto.pecaId()).isEqualTo(pecaId);
        assertThat(dto.quantidade()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve criar instância com quantidade um")
    void testCriarComQuantidadeUm() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto = new PecaOrcamentoRequestDTO(pecaId, 1);

        assertThat(dto).isNotNull();
        assertThat(dto.quantidade()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve criar instância com quantidade grande")
    void testCriarComQuantidadeGrande() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto = new PecaOrcamentoRequestDTO(pecaId, 1000);

        assertThat(dto).isNotNull();
        assertThat(dto.quantidade()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto1 = new PecaOrcamentoRequestDTO(pecaId, 10);
        PecaOrcamentoRequestDTO dto2 = new PecaOrcamentoRequestDTO(pecaId, 10);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com peça ids diferentes")
    void testDesigualdadeComPecaIdDiferente() {
        PecaOrcamentoRequestDTO dto1 = new PecaOrcamentoRequestDTO(UUID.randomUUID(), 10);
        PecaOrcamentoRequestDTO dto2 = new PecaOrcamentoRequestDTO(UUID.randomUUID(), 10);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com quantidades diferentes")
    void testDesigualdadeComQuantidadeDiferente() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto1 = new PecaOrcamentoRequestDTO(pecaId, 5);
        PecaOrcamentoRequestDTO dto2 = new PecaOrcamentoRequestDTO(pecaId, 10);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto = new PecaOrcamentoRequestDTO(pecaId, 3);
        String toString = dto.toString();

        assertThat(toString).contains("PecaOrcamentoRequestDTO");
        assertThat(toString).contains(pecaId.toString());
        assertThat(toString).contains("3");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID pecaId = UUID.randomUUID();

        PecaOrcamentoRequestDTO dto1 = new PecaOrcamentoRequestDTO(pecaId, 5);
        PecaOrcamentoRequestDTO dto2 = new PecaOrcamentoRequestDTO(pecaId, 5);

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
