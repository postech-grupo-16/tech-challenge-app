package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrcamentoDTO")
class OrcamentoDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        LocalDateTime prazo = LocalDateTime.now();
        LocalDateTime dataCriacao = LocalDateTime.now();
        List<ServicoDTO> servicos = List.of();
        List<PecaDTO> pecas = List.of();

        OrcamentoDTO dto = new OrcamentoDTO(
                1L,
                TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00),
                prazo,
                dataCriacao,
                servicos,
                pecas
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.INICIAL);
        assertThat(dto.status()).isEqualTo(StatusOrcamento.PENDENTE);
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
        assertThat(dto.prazoEstipulado()).isEqualTo(prazo);
        assertThat(dto.dataCriacao()).isEqualTo(dataCriacao);
        assertThat(dto.servicos()).isEmpty();
        assertThat(dto.pecas()).isEmpty();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        LocalDateTime prazo = LocalDateTime.now();
        LocalDateTime dataCriacao = LocalDateTime.now();

        OrcamentoDTO dto1 = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), prazo, dataCriacao, List.of(), List.of());
        OrcamentoDTO dto2 = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), prazo, dataCriacao, List.of(), List.of());

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com ids diferentes")
    void testDesigualdadeComIdDiferente() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoDTO dto1 = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoDTO dto2 = new OrcamentoDTO(2L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoDTO dto = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        String toString = dto.toString();

        assertThat(toString).contains("OrcamentoDTO");
        assertThat(toString).contains("INICIAL");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoDTO dto1 = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoDTO dto2 = new OrcamentoDTO(1L, TipoOrcamento.INICIAL, StatusOrcamento.PENDENTE,
                BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
