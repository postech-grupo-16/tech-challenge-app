package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MecanicoInfoDTO")
class MecanicoInfoDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID id = UUID.randomUUID();

        MecanicoInfoDTO dto = new MecanicoInfoDTO(
                id,
                "Carlos Mecânico",
                "carlos@email.com",
                "11987654321"
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("Carlos Mecânico");
        assertThat(dto.email()).isEqualTo("carlos@email.com");
        assertThat(dto.telefone()).isEqualTo("11987654321");
    }

    @Test
    @DisplayName("Deve criar instância com campos opcionais null")
    void testCriarComCamposOpcionaisNull() {
        UUID id = UUID.randomUUID();

        MecanicoInfoDTO dto = new MecanicoInfoDTO(
                id,
                "Pedro",
                null,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("Pedro");
        assertThat(dto.email()).isNull();
        assertThat(dto.telefone()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID id = UUID.randomUUID();

        MecanicoInfoDTO dto1 = new MecanicoInfoDTO(id, "João", "joao@email.com", "11988888888");
        MecanicoInfoDTO dto2 = new MecanicoInfoDTO(id, "João", "joao@email.com", "11988888888");

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        MecanicoInfoDTO dto1 = new MecanicoInfoDTO(UUID.randomUUID(), "João", "joao@email.com", "11988888888");
        MecanicoInfoDTO dto2 = new MecanicoInfoDTO(UUID.randomUUID(), "João", "joao@email.com", "11988888888");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID id = UUID.randomUUID();

        MecanicoInfoDTO dto = new MecanicoInfoDTO(id, "Mário", "mario@email.com", "11999999999");
        String toString = dto.toString();

        assertThat(toString).contains("MecanicoInfoDTO");
        assertThat(toString).contains("Mário");
        assertThat(toString).contains("mario@email.com");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID id = UUID.randomUUID();

        MecanicoInfoDTO dto1 = new MecanicoInfoDTO(id, "Pedro", "pedro@email.com", "11977777777");
        MecanicoInfoDTO dto2 = new MecanicoInfoDTO(id, "Pedro", "pedro@email.com", "11977777777");

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Deve criar instância null usando from com null")
    void testFromComNull() {
        MecanicoInfoDTO dto = MecanicoInfoDTO.from(null);
        assertThat(dto).isNull();
    }
}
