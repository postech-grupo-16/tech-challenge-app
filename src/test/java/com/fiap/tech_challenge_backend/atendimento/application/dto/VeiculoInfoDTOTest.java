package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("VeiculoInfoDTO")
class VeiculoInfoDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        VeiculoInfoDTO dto = new VeiculoInfoDTO(
                "ABC1234",
                "Chevrolet Onix",
                "Prata"
        );

        assertThat(dto).isNotNull();
        assertThat(dto.placa()).isEqualTo("ABC1234");
        assertThat(dto.modelo()).isEqualTo("Chevrolet Onix");
        assertThat(dto.cor()).isEqualTo("Prata");
    }

    @Test
    @DisplayName("Deve criar instância com campos null")
    void testCriarComCamposNull() {
        VeiculoInfoDTO dto = new VeiculoInfoDTO(null, null, null);

        assertThat(dto).isNotNull();
        assertThat(dto.placa()).isNull();
        assertThat(dto.modelo()).isNull();
        assertThat(dto.cor()).isNull();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        VeiculoInfoDTO dto1 = new VeiculoInfoDTO("XYZ9876", "Ford Ka", "Branco");
        VeiculoInfoDTO dto2 = new VeiculoInfoDTO("XYZ9876", "Ford Ka", "Branco");

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com placas diferentes")
    void testDesigualdadeComPlacaDiferente() {
        VeiculoInfoDTO dto1 = new VeiculoInfoDTO("ABC1234", "Ford Ka", "Branco");
        VeiculoInfoDTO dto2 = new VeiculoInfoDTO("XYZ9876", "Ford Ka", "Branco");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        VeiculoInfoDTO dto = new VeiculoInfoDTO("DEF5678", "Hyundai HB20", "Preto");
        String toString = dto.toString();

        assertThat(toString).contains("VeiculoInfoDTO");
        assertThat(toString).contains("DEF5678");
        assertThat(toString).contains("Hyundai");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        VeiculoInfoDTO dto1 = new VeiculoInfoDTO("ABC1234", "Ford Ka", "Branco");
        VeiculoInfoDTO dto2 = new VeiculoInfoDTO("ABC1234", "Ford Ka", "Branco");

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
