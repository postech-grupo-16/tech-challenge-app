package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CriarOrcamentoRequestDTO")
class CriarOrcamentoRequestDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of(
                new ServicoOrcamentoRequestDTO(UUID.randomUUID())
        );
        List<PecaOrcamentoRequestDTO> pecas = List.of();

        CriarOrcamentoRequestDTO dto = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL,
                "2026-07-05",
                servicos,
                pecas
        );

        assertThat(dto).isNotNull();
        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.INICIAL);
        assertThat(dto.prazoEstipulado()).isEqualTo("2026-07-05");
        assertThat(dto.servicos()).hasSize(1);
        assertThat(dto.pecas()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar instância com tipo adicional")
    void testCriarComTipoAdicional() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of(
                new ServicoOrcamentoRequestDTO(UUID.randomUUID())
        );
        List<PecaOrcamentoRequestDTO> pecas = List.of(
                new PecaOrcamentoRequestDTO(UUID.randomUUID(), 2)
        );

        CriarOrcamentoRequestDTO dto = new CriarOrcamentoRequestDTO(
                TipoOrcamento.ADICIONAL,
                "2026-08-01",
                servicos,
                pecas
        );

        assertThat(dto).isNotNull();
        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.ADICIONAL);
        assertThat(dto.pecas()).hasSize(1);
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of();
        List<PecaOrcamentoRequestDTO> pecas = List.of();

        CriarOrcamentoRequestDTO dto1 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );
        CriarOrcamentoRequestDTO dto2 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com tipos diferentes")
    void testDesigualdadeComTipoDiferente() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of();
        List<PecaOrcamentoRequestDTO> pecas = List.of();

        CriarOrcamentoRequestDTO dto1 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );
        CriarOrcamentoRequestDTO dto2 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.ADICIONAL, "2026-07-05", servicos, pecas
        );

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of();
        List<PecaOrcamentoRequestDTO> pecas = List.of();

        CriarOrcamentoRequestDTO dto = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );
        String toString = dto.toString();

        assertThat(toString).contains("CriarOrcamentoRequestDTO");
        assertThat(toString).contains("INICIAL");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        List<ServicoOrcamentoRequestDTO> servicos = List.of();
        List<PecaOrcamentoRequestDTO> pecas = List.of();

        CriarOrcamentoRequestDTO dto1 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );
        CriarOrcamentoRequestDTO dto2 = new CriarOrcamentoRequestDTO(
                TipoOrcamento.INICIAL, "2026-07-05", servicos, pecas
        );

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
