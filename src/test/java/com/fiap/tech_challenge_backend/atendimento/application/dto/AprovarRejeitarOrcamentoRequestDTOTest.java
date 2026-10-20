package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AprovarRejeitarOrcamentoRequestDTO")
class AprovarRejeitarOrcamentoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com status aprovado")
    void testCriarComAprovado() {
        AprovarRejeitarOrcamentoRequestDTO dto = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );

        assertThat(dto).isNotNull();
        assertThat(dto.status()).isEqualTo(StatusOrcamento.APROVADO);
    }

    @Test
    @DisplayName("Deve criar instância com status rejeitado")
    void testCriarComRejeitado() {
        AprovarRejeitarOrcamentoRequestDTO dto = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.REJEITADO
        );

        assertThat(dto).isNotNull();
        assertThat(dto.status()).isEqualTo(StatusOrcamento.REJEITADO);
    }

    @Test
    @DisplayName("Deve criar instância com status pendente")
    void testCriarComPendente() {
        AprovarRejeitarOrcamentoRequestDTO dto = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.PENDENTE
        );

        assertThat(dto).isNotNull();
        assertThat(dto.status()).isEqualTo(StatusOrcamento.PENDENTE);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records aprovados")
    void testIgualdadeRecordsAprovados() {
        AprovarRejeitarOrcamentoRequestDTO dto1 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );
        AprovarRejeitarOrcamentoRequestDTO dto2 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records rejeitados")
    void testIgualdadeRecordsRejeitados() {
        AprovarRejeitarOrcamentoRequestDTO dto1 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.REJEITADO
        );
        AprovarRejeitarOrcamentoRequestDTO dto2 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.REJEITADO
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade entre status diferentes")
    void testDesigualdadeStatusDiferente() {
        AprovarRejeitarOrcamentoRequestDTO dto1 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );
        AprovarRejeitarOrcamentoRequestDTO dto2 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.REJEITADO
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        AprovarRejeitarOrcamentoRequestDTO dto = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );
        String toString = dto.toString();

        assertThat(toString).contains("AprovarRejeitarOrcamentoRequestDTO");
        assertThat(toString).contains("APROVADO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        AprovarRejeitarOrcamentoRequestDTO dto1 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );
        AprovarRejeitarOrcamentoRequestDTO dto2 = new AprovarRejeitarOrcamentoRequestDTO(
                StatusOrcamento.APROVADO
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
