package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrdemServicoRequestDTO")
class OrdemServicoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                mecanicoId,
                true
        );

        assertThat(dto).isNotNull();
        assertThat(dto.clienteId()).isEqualTo(clienteId);
        assertThat(dto.veiculoId()).isEqualTo(veiculoId);
        assertThat(dto.mecanicoId()).isEqualTo(mecanicoId);
        assertThat(dto.urgente()).isTrue();
    }

    @Test
    @DisplayName("Deve criar instância com campos opcionais null")
    void testCriarComCamposOpcionaisNull() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.clienteId()).isEqualTo(clienteId);
        assertThat(dto.veiculoId()).isEqualTo(veiculoId);
        assertThat(dto.mecanicoId()).isNull();
        assertThat(dto.urgente()).isNull();
    }

    @Test
    @DisplayName("Deve criar instância com urgente false")
    void testCriarComUrgenteFalse() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto = new OrdemServicoRequestDTO(
                clienteId,
                veiculoId,
                null,
                false
        );

        assertThat(dto).isNotNull();
        assertThat(dto.urgente()).isFalse();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto1 = new OrdemServicoRequestDTO(clienteId, veiculoId, mecanicoId, true);
        OrdemServicoRequestDTO dto2 = new OrdemServicoRequestDTO(clienteId, veiculoId, mecanicoId, true);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com cliente diferente")
    void testDesigualdadeComClienteDiferente() {
        UUID veiculoId = UUID.randomUUID();
        UUID mecanicoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto1 = new OrdemServicoRequestDTO(UUID.randomUUID(), veiculoId, mecanicoId, true);
        OrdemServicoRequestDTO dto2 = new OrdemServicoRequestDTO(UUID.randomUUID(), veiculoId, mecanicoId, true);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto = new OrdemServicoRequestDTO(clienteId, veiculoId, null, true);
        String toString = dto.toString();

        assertThat(toString).contains("OrdemServicoRequestDTO");
        assertThat(toString).contains(clienteId.toString());
        assertThat(toString).contains(veiculoId.toString());
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        OrdemServicoRequestDTO dto1 = new OrdemServicoRequestDTO(clienteId, veiculoId, null, true);
        OrdemServicoRequestDTO dto2 = new OrdemServicoRequestDTO(clienteId, veiculoId, null, true);

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
