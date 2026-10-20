package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.ServicoCatalogo;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrcamentoResponseDTO")
class OrcamentoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        LocalDateTime prazo = LocalDateTime.now();
        LocalDateTime dataCriacao = LocalDateTime.now();
        List<ServicoDTO> servicos = List.of();
        List<PecaDTO> pecas = List.of();

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO(
                1L,
                TipoOrcamento.ADICIONAL,
                StatusOrcamento.APROVADO,
                BigDecimal.valueOf(1500.00),
                prazo,
                dataCriacao,
                servicos,
                pecas
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.ADICIONAL);
        assertThat(dto.status()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
        assertThat(dto.servicos()).isEmpty();
        assertThat(dto.pecas()).isEmpty();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com status diferentes")
    void testDesigualdadeComStatusDiferente() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.REJEITADO, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO(1L, TipoOrcamento.ADICIONAL,
                StatusOrcamento.APROVADO, BigDecimal.valueOf(1500.00), agora, agora, List.of(), List.of());
        String toString = dto.toString();

        assertThat(toString).contains("OrcamentoResponseDTO");
        assertThat(toString).contains("ADICIONAL");
        assertThat(toString).contains("APROVADO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    // ─────────────────────────────────────────────
    // from(OsOrcamento)
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("from(OsOrcamento)")
    class From {

        @Test
        @DisplayName("mapeia todos os campos escalares do orcamento")
        void mapeiaScalares() {
            LocalDateTime prazo = LocalDateTime.now().plusDays(5);
            LocalDateTime criacao = LocalDateTime.now();

            OsOrcamento orc = OsOrcamento.builder()
                    .id(42L)
                    .tipo(TipoOrcamento.INICIAL)
                    .valorTotal(BigDecimal.valueOf(750))
                    .prazoEstipulado(prazo)
                    .dataCriacao(criacao)
                    .build();

            OrcamentoResponseDTO dto = OrcamentoResponseDTO.from(orc, id -> null);

            assertThat(dto.id()).isEqualTo(42L);
            assertThat(dto.tipo()).isEqualTo(TipoOrcamento.INICIAL);
            assertThat(dto.status()).isEqualTo(StatusOrcamento.PENDENTE);
            assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(750));
            assertThat(dto.prazoEstipulado()).isEqualTo(prazo);
            assertThat(dto.dataCriacao()).isEqualTo(criacao);
        }

        @Test
        @DisplayName("mapeia listas vazias quando nao ha servicos nem pecas")
        void listasVazias() {
            OsOrcamento orc = OsOrcamento.builder()
                    .id(1L)
                    .tipo(TipoOrcamento.INICIAL)
                    .dataCriacao(LocalDateTime.now())
                    .build();

            OrcamentoResponseDTO dto = OrcamentoResponseDTO.from(orc, id -> null);

            assertThat(dto.servicos()).isNotNull().isEmpty();
            assertThat(dto.pecas()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("mapeia servicos via ServicoDTO.from")
        void mapeiaServicos() {
            UUID servicoCatalogoId = UUID.randomUUID();
            ServicoCatalogo catalogo = new ServicoCatalogo();
            catalogo.setId(servicoCatalogoId);
            catalogo.setNome("Troca de Oleo");

            UUID osServicoId = UUID.randomUUID();
            OsServico osServico = OsServico.builder()
                    .id(osServicoId)
                    .servico(catalogo)
                    .precoMaoDeObraAplicado(BigDecimal.valueOf(120))
                    .build();

            OsOrcamento orc = OsOrcamento.builder()
                    .id(1L)
                    .tipo(TipoOrcamento.INICIAL)
                    .dataCriacao(LocalDateTime.now())
                    .servicos(List.of(osServico))
                    .build();

            OrcamentoResponseDTO dto = OrcamentoResponseDTO.from(orc, id -> null);

            assertThat(dto.servicos()).hasSize(1);
            assertThat(dto.servicos().get(0).id()).isEqualTo(osServicoId);
            assertThat(dto.servicos().get(0).servicoId()).isEqualTo(servicoCatalogoId);
            assertThat(dto.servicos().get(0).nome()).isEqualTo("Troca de Oleo");
            assertThat(dto.servicos().get(0).precoMaoDeObraAplicado()).isEqualByComparingTo(BigDecimal.valueOf(120));
        }

        @Test
        @DisplayName("mapeia pecas via PecaDTO.from")
        void mapeiaPecas() {
            UUID pecaInsumoId = UUID.randomUUID();
            PecaInsumo pecaInsumo = new PecaInsumo();
            pecaInsumo.setId(pecaInsumoId);
            pecaInsumo.setNome("Filtro de Oleo");

            UUID osPecaId = UUID.randomUUID();
            OsPeca osPeca = OsPeca.builder()
                    .id(osPecaId)
                    .pecaInsumoId(pecaInsumoId)
                    .quantidade(2)
                    .precoVendaAplicado(BigDecimal.valueOf(45))
                    .build();

            OsOrcamento orc = OsOrcamento.builder()
                    .id(1L)
                    .tipo(TipoOrcamento.INICIAL)
                    .dataCriacao(LocalDateTime.now())
                    .pecas(List.of(osPeca))
                    .build();

            OrcamentoResponseDTO dto = OrcamentoResponseDTO.from(orc, id -> pecaInsumo.getNome());

            assertThat(dto.pecas()).hasSize(1);
            assertThat(dto.pecas().get(0).id()).isEqualTo(osPecaId);
            assertThat(dto.pecas().get(0).pecaId()).isEqualTo(pecaInsumoId);
            assertThat(dto.pecas().get(0).nome()).isEqualTo("Filtro de Oleo");
            assertThat(dto.pecas().get(0).quantidade()).isEqualTo(2);
            assertThat(dto.pecas().get(0).precoVendaAplicado()).isEqualByComparingTo(BigDecimal.valueOf(45));
        }

        @Test
        @DisplayName("mapeia servicos e pecas simultaneamente")
        void mapeiaServicoEPecaJuntos() {
            ServicoCatalogo catalogo = new ServicoCatalogo();
            catalogo.setId(UUID.randomUUID());
            catalogo.setNome("Alinhamento");

            OsServico osServico = OsServico.builder()
                    .id(UUID.randomUUID())
                    .servico(catalogo)
                    .precoMaoDeObraAplicado(BigDecimal.valueOf(80))
                    .build();

            PecaInsumo pecaInsumo = new PecaInsumo();
            pecaInsumo.setId(UUID.randomUUID());
            pecaInsumo.setNome("Fluido de Direcao");

            OsPeca osPeca = OsPeca.builder()
                    .id(UUID.randomUUID())
                    .pecaInsumoId(pecaInsumo.getId())
                    .quantidade(1)
                    .precoVendaAplicado(BigDecimal.valueOf(35))
                    .build();

            OsOrcamento orc = OsOrcamento.builder()
                    .id(5L)
                    .tipo(TipoOrcamento.ADICIONAL)
                    .valorTotal(BigDecimal.valueOf(115))
                    .dataCriacao(LocalDateTime.now())
                    .servicos(List.of(osServico))
                    .pecas(List.of(osPeca))
                    .build();

            OrcamentoResponseDTO dto = OrcamentoResponseDTO.from(orc, id -> pecaInsumo.getNome());

            assertThat(dto.servicos()).hasSize(1);
            assertThat(dto.pecas()).hasSize(1);
            assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(115));
        }
    }
}
