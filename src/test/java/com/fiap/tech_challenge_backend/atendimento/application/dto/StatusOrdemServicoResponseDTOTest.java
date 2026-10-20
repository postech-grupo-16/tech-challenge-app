package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StatusOrdemServicoResponseDTO")
class StatusOrdemServicoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID ordemServicoId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "EM_EXECUCAO",
                "Os trabalhos estão em execução",
                true
        );

        assertThat(dto).isNotNull();
        assertThat(dto.ordemServicoId()).isEqualTo(ordemServicoId);
        assertThat(dto.status()).isEqualTo("EM_EXECUCAO");
        assertThat(dto.mensagem()).isEqualTo("Os trabalhos estão em execução");
        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve criar instância com diferentes status")
    void testCriarComDiferentesStatus() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto1 = new StatusOrdemServicoResponseDTO(id1, "RECEBIDA", "Recebida", true);
        StatusOrdemServicoResponseDTO dto2 = new StatusOrdemServicoResponseDTO(id2, "FINALIZADA", "Finalizada", false);

        assertThat(dto1.status()).isEqualTo("RECEBIDA");
        assertThat(dto2.status()).isEqualTo("FINALIZADA");
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID ordemServicoId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto1 = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "EM_DIAGNOSTICO",
                "Diagnóstico",
                false
        );
        StatusOrdemServicoResponseDTO dto2 = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "EM_DIAGNOSTICO",
                "Diagnóstico",
                false
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        StatusOrdemServicoResponseDTO dto1 = new StatusOrdemServicoResponseDTO(
                UUID.randomUUID(),
                "EM_DIAGNOSTICO",
                "Diagnóstico",
                false
        );
        StatusOrdemServicoResponseDTO dto2 = new StatusOrdemServicoResponseDTO(
                UUID.randomUUID(),
                "EM_DIAGNOSTICO",
                "Diagnóstico",
                false
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar método from")
    void testFrom() {
        UUID ordemServicoId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto = StatusOrdemServicoResponseDTO.from(
                ordemServicoId,
                "RECEBIDA",
                true
        );

        assertThat(dto).isNotNull();
        assertThat(dto.ordemServicoId()).isEqualTo(ordemServicoId);
        assertThat(dto.status()).isEqualTo("RECEBIDA");
        assertThat(dto.mensagem()).isNotNull();
        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID ordemServicoId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "AGUARDANDO_APROVACAO",
                "Aguardando aprovação",
                true
        );
        String toString = dto.toString();

        assertThat(toString).contains("StatusOrdemServicoResponseDTO");
        assertThat(toString).contains("AGUARDANDO_APROVACAO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID ordemServicoId = UUID.randomUUID();

        StatusOrdemServicoResponseDTO dto1 = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "FINALIZADA",
                "Finalizada",
                true
        );
        StatusOrdemServicoResponseDTO dto2 = new StatusOrdemServicoResponseDTO(
                ordemServicoId,
                "FINALIZADA",
                "Finalizada",
                true
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
