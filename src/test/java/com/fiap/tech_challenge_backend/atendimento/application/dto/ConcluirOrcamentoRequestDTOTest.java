package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ConcluirOrcamentoRequestDTO")
class ConcluirOrcamentoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(5);

        ConcluirOrcamentoRequestDTO dto = new ConcluirOrcamentoRequestDTO(
                1L,
                "cliente@email.com",
                prazo
        );

        assertThat(dto).isNotNull();
        assertThat(dto.orcamentoId()).isEqualTo(1L);
        assertThat(dto.emailCliente()).isEqualTo("cliente@email.com");
        assertThat(dto.prazoEstipulado()).isEqualTo(prazo);
    }

    @Test
    @DisplayName("Deve criar instância com email diferente")
    void testCriarComEmailDiferente() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(7);

        ConcluirOrcamentoRequestDTO dto = new ConcluirOrcamentoRequestDTO(
                2L,
                "outro@email.com",
                prazo
        );

        assertThat(dto).isNotNull();
        assertThat(dto.emailCliente()).isEqualTo("outro@email.com");
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(5);

        ConcluirOrcamentoRequestDTO dto1 = new ConcluirOrcamentoRequestDTO(
                1L, "cliente@email.com", prazo
        );
        ConcluirOrcamentoRequestDTO dto2 = new ConcluirOrcamentoRequestDTO(
                1L, "cliente@email.com", prazo
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com orcamento ids diferentes")
    void testDesigualdadeComOrcamentoIdDiferente() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(5);

        ConcluirOrcamentoRequestDTO dto1 = new ConcluirOrcamentoRequestDTO(
                1L, "cliente@email.com", prazo
        );
        ConcluirOrcamentoRequestDTO dto2 = new ConcluirOrcamentoRequestDTO(
                2L, "cliente@email.com", prazo
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(5);

        ConcluirOrcamentoRequestDTO dto = new ConcluirOrcamentoRequestDTO(
                1L, "teste@email.com", prazo
        );
        String toString = dto.toString();

        assertThat(toString).contains("ConcluirOrcamentoRequestDTO");
        assertThat(toString).contains("teste@email.com");
        assertThat(toString).contains("1");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        LocalDateTime prazo = LocalDateTime.now().plusDays(5);

        ConcluirOrcamentoRequestDTO dto1 = new ConcluirOrcamentoRequestDTO(
                1L, "cliente@email.com", prazo
        );
        ConcluirOrcamentoRequestDTO dto2 = new ConcluirOrcamentoRequestDTO(
                1L, "cliente@email.com", prazo
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
