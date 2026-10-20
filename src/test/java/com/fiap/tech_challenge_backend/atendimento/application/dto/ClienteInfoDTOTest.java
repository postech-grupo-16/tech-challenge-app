package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ClienteInfoDTO")
class ClienteInfoDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto = new ClienteInfoDTO(
                id,
                "João Silva",
                "11999999999",
                "joao@email.com"
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("João Silva");
        assertThat(dto.telefone()).isEqualTo("11999999999");
        assertThat(dto.email()).isEqualTo("joao@email.com");
    }

    @Test
    @DisplayName("Deve criar instância com telefone e email null")
    void testCriarComTelefoneEmailNull() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto = new ClienteInfoDTO(
                id,
                "Maria Santos",
                null,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("Maria Santos");
        assertThat(dto.telefone()).isNull();
        assertThat(dto.email()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto1 = new ClienteInfoDTO(id, "Cliente A", "11988888888", "a@email.com");
        ClienteInfoDTO dto2 = new ClienteInfoDTO(id, "Cliente A", "11988888888", "a@email.com");

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        ClienteInfoDTO dto1 = new ClienteInfoDTO(UUID.randomUUID(), "Cliente A", "11988888888", "a@email.com");
        ClienteInfoDTO dto2 = new ClienteInfoDTO(UUID.randomUUID(), "Cliente A", "11988888888", "a@email.com");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto = new ClienteInfoDTO(id, "João", "11999999999", "joao@email.com");
        String toString = dto.toString();

        assertThat(toString).contains("ClienteInfoDTO");
        assertThat(toString).contains("João");
        assertThat(toString).contains("joao@email.com");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID id = UUID.randomUUID();

        ClienteInfoDTO dto1 = new ClienteInfoDTO(id, "Cliente", "11999999999", "a@email.com");
        ClienteInfoDTO dto2 = new ClienteInfoDTO(id, "Cliente", "11999999999", "a@email.com");

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
