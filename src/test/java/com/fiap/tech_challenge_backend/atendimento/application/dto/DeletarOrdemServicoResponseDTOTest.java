package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DeletarOrdemServicoResponseDTO")
class DeletarOrdemServicoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto = new DeletarOrdemServicoResponseDTO(
                1L,
                "Ordem de Serviço deletada com sucesso",
                agora,
                "DELETADO"
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.mensagem()).isEqualTo("Ordem de Serviço deletada com sucesso");
        assertThat(dto.dataDelecao()).isEqualTo(agora);
        assertThat(dto.status()).isEqualTo("DELETADO");
    }

    @Test
    @DisplayName("Deve criar instância usando método sucesso")
    void testSucesso() {
        DeletarOrdemServicoResponseDTO dto = DeletarOrdemServicoResponseDTO.sucesso(123L);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(123L);
        assertThat(dto.mensagem()).isEqualTo("Ordem de Serviço deletada com sucesso");
        assertThat(dto.status()).isEqualTo("DELETADO");
        assertThat(dto.dataDelecao()).isNotNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto1 = new DeletarOrdemServicoResponseDTO(
                1L, "Deletado", agora, "DELETADO"
        );
        DeletarOrdemServicoResponseDTO dto2 = new DeletarOrdemServicoResponseDTO(
                1L, "Deletado", agora, "DELETADO"
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto1 = new DeletarOrdemServicoResponseDTO(
                1L, "Deletado", agora, "DELETADO"
        );
        DeletarOrdemServicoResponseDTO dto2 = new DeletarOrdemServicoResponseDTO(
                2L, "Deletado", agora, "DELETADO"
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto = new DeletarOrdemServicoResponseDTO(
                123L, "Mensagem", agora, "DELETADO"
        );
        String toString = dto.toString();

        assertThat(toString).contains("DeletarOrdemServicoResponseDTO");
        assertThat(toString).contains("123");
        assertThat(toString).contains("DELETADO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        LocalDateTime agora = LocalDateTime.now();

        DeletarOrdemServicoResponseDTO dto1 = new DeletarOrdemServicoResponseDTO(
                1L, "Deletado", agora, "DELETADO"
        );
        DeletarOrdemServicoResponseDTO dto2 = new DeletarOrdemServicoResponseDTO(
                1L, "Deletado", agora, "DELETADO"
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
