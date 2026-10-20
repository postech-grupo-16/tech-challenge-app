package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ServicoOrcamentoRequestDTO")
class ServicoOrcamentoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com serviço id")
    void testCriarComServicoId() {
        UUID servicoId = UUID.randomUUID();

        ServicoOrcamentoRequestDTO dto = new ServicoOrcamentoRequestDTO(servicoId);

        assertThat(dto).isNotNull();
        assertThat(dto.servicoId()).isEqualTo(servicoId);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID servicoId = UUID.randomUUID();

        ServicoOrcamentoRequestDTO dto1 = new ServicoOrcamentoRequestDTO(servicoId);
        ServicoOrcamentoRequestDTO dto2 = new ServicoOrcamentoRequestDTO(servicoId);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com serviço ids diferentes")
    void testDesigualdadeComServicoIdDiferente() {
        ServicoOrcamentoRequestDTO dto1 = new ServicoOrcamentoRequestDTO(UUID.randomUUID());
        ServicoOrcamentoRequestDTO dto2 = new ServicoOrcamentoRequestDTO(UUID.randomUUID());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID servicoId = UUID.randomUUID();

        ServicoOrcamentoRequestDTO dto = new ServicoOrcamentoRequestDTO(servicoId);
        String toString = dto.toString();

        assertThat(toString).contains("ServicoOrcamentoRequestDTO");
        assertThat(toString).contains(servicoId.toString());
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID servicoId = UUID.randomUUID();

        ServicoOrcamentoRequestDTO dto1 = new ServicoOrcamentoRequestDTO(servicoId);
        ServicoOrcamentoRequestDTO dto2 = new ServicoOrcamentoRequestDTO(servicoId);

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
