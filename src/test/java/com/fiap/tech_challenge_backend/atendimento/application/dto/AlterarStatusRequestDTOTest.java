package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AlterarStatusRequestDTO")
class AlterarStatusRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com status e mecânico id")
    void testCriarComTodosOsCampos() {
        UUID mecanicoId = UUID.randomUUID();

        AlterarStatusRequestDTO dto = new AlterarStatusRequestDTO(
                StatusOrdemServico.EM_DIAGNOSTICO,
                mecanicoId
        );

        assertThat(dto).isNotNull();
        assertThat(dto.novoStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(dto.mecanicoId()).isEqualTo(mecanicoId);
    }

    @Test
    @DisplayName("Deve criar instância com mecânico id null")
    void testCriarComMecanicoIdNull() {
        AlterarStatusRequestDTO dto = new AlterarStatusRequestDTO(
                StatusOrdemServico.EM_EXECUCAO,
                null
        );

        assertThat(dto).isNotNull();
        assertThat(dto.novoStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        assertThat(dto.mecanicoId()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records com mesmos valores")
    void testIgualdadeRecords() {
        UUID mecanicoId = UUID.randomUUID();

        AlterarStatusRequestDTO dto1 = new AlterarStatusRequestDTO(
                StatusOrdemServico.FINALIZADA,
                mecanicoId
        );

        AlterarStatusRequestDTO dto2 = new AlterarStatusRequestDTO(
                StatusOrdemServico.FINALIZADA,
                mecanicoId
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com status diferentes")
    void testDesigualdadeComStatusDiferentes() {
        UUID mecanicoId = UUID.randomUUID();

        AlterarStatusRequestDTO dto1 = new AlterarStatusRequestDTO(
                StatusOrdemServico.FINALIZADA,
                mecanicoId
        );

        AlterarStatusRequestDTO dto2 = new AlterarStatusRequestDTO(
                StatusOrdemServico.ENTREGUE,
                mecanicoId
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID mecanicoId = UUID.randomUUID();

        AlterarStatusRequestDTO dto = new AlterarStatusRequestDTO(
                StatusOrdemServico.EM_DIAGNOSTICO,
                mecanicoId
        );

        String toString = dto.toString();

        assertThat(toString).contains("AlterarStatusRequestDTO");
        assertThat(toString).contains("EM_DIAGNOSTICO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID mecanicoId = UUID.randomUUID();

        AlterarStatusRequestDTO dto1 = new AlterarStatusRequestDTO(
                StatusOrdemServico.FINALIZADA,
                mecanicoId
        );

        AlterarStatusRequestDTO dto2 = new AlterarStatusRequestDTO(
                StatusOrdemServico.FINALIZADA,
                mecanicoId
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
