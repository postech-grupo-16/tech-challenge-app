package com.fiap.tech_challenge_backend.atendimento.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ServicoDTO")
class ServicoDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto = new ServicoDTO(
                id,
                servicoId,
                "Troca de Óleo",
                BigDecimal.valueOf(80.00)
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.servicoId()).isEqualTo(servicoId);
        assertThat(dto.nome()).isEqualTo("Troca de Óleo");
        assertThat(dto.precoMaoDeObraAplicado()).isEqualByComparingTo(BigDecimal.valueOf(80.00));
    }

    @Test
    @DisplayName("Deve criar instância com preço alto")
    void testCriarComPrecoAlto() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto = new ServicoDTO(
                id,
                servicoId,
                "Reparo Motor Completo",
                BigDecimal.valueOf(2500.00)
        );

        assertThat(dto).isNotNull();
        assertThat(dto.precoMaoDeObraAplicado()).isEqualByComparingTo(BigDecimal.valueOf(2500.00));
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto1 = new ServicoDTO(id, servicoId, "Alinhamento", BigDecimal.valueOf(150.00));
        ServicoDTO dto2 = new ServicoDTO(id, servicoId, "Alinhamento", BigDecimal.valueOf(150.00));

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com preços diferentes")
    void testDesigualdadeComPrecoDiferente() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto1 = new ServicoDTO(id, servicoId, "Alinhamento", BigDecimal.valueOf(150.00));
        ServicoDTO dto2 = new ServicoDTO(id, servicoId, "Alinhamento", BigDecimal.valueOf(200.00));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto = new ServicoDTO(id, servicoId, "Balanceamento", BigDecimal.valueOf(100.00));
        String toString = dto.toString();

        assertThat(toString).contains("ServicoDTO");
        assertThat(toString).contains("Balanceamento");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        UUID id = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        ServicoDTO dto1 = new ServicoDTO(id, servicoId, "Reparo Freio", BigDecimal.valueOf(200.00));
        ServicoDTO dto2 = new ServicoDTO(id, servicoId, "Reparo Freio", BigDecimal.valueOf(200.00));

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
