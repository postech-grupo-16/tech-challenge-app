package com.fiap.tech_challenge_backend.estoque.application.dto;

import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTOs de Movimentacao")
class MovimentacaoDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ─────────────────────────────────────────────
    // MovimentacaoRequestDTO
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("MovimentacaoRequestDTO")
    class Request {

        private final UUID pecaId = UUID.randomUUID();

        @Test
        @DisplayName("deve construir com todos os campos")
        void deveConstruirComTodosOsCampos() {
            var dto = new MovimentacaoRequestDTO(
                    pecaId, TipoMovimentacao.ENTRADA, 10, "Compra NF-001"
            );

            assertThat(dto.pecaInsumoId()).isEqualTo(pecaId);
            assertThat(dto.tipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(dto.quantidade()).isEqualTo(10);
            assertThat(dto.observacao()).isEqualTo("Compra NF-001");
        }

        @Test
        @DisplayName("deve aceitar observacao nula")
        void deveAceitarObservacaoNula() {
            var dto = new MovimentacaoRequestDTO(pecaId, TipoMovimentacao.SAIDA, 5, null);

            assertThat(dto.observacao()).isNull();
            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("deve passar na validação com dados válidos")
        void devePassarNaValidacaoComDadosValidos() {
            var dto = new MovimentacaoRequestDTO(pecaId, TipoMovimentacao.VENDA, 3, "Venda OS-001");

            assertThat(validator.validate(dto)).isEmpty();
        }

        @Test
        @DisplayName("deve falhar quando pecaInsumoId é nulo")
        void deveFalharQuandoPecaIdNulo() {
            var dto = new MovimentacaoRequestDTO(null, TipoMovimentacao.ENTRADA, 1, null);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("pecaInsumoId"));
        }

        @Test
        @DisplayName("deve falhar quando tipoMovimentacao é nulo")
        void deveFalharQuandoTipoNulo() {
            var dto = new MovimentacaoRequestDTO(pecaId, null, 1, null);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("tipoMovimentacao"));
        }

        @Test
        @DisplayName("deve falhar quando quantidade é nula")
        void deveFalharQuandoQuantidadeNula() {
            var dto = new MovimentacaoRequestDTO(pecaId, TipoMovimentacao.ENTRADA, null, null);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidade"));
        }

        @Test
        @DisplayName("deve falhar quando quantidade é zero ou negativa")
        void deveFalharQuandoQuantidadeNaoPositiva() {
            var dto = new MovimentacaoRequestDTO(pecaId, TipoMovimentacao.ENTRADA, 0, null);

            var violacoes = validator.validate(dto);
            assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("quantidade"));
        }

        @Test
        @DisplayName("deve aceitar todos os tipos de movimentação")
        void deveAceitarTodosOsTipos() {
            for (var tipo : TipoMovimentacao.values()) {
                var dto = new MovimentacaoRequestDTO(pecaId, tipo, 1, null);
                assertThat(validator.validate(dto)).isEmpty();
            }
        }
    }

    // ─────────────────────────────────────────────
    // MovimentacaoResponseDTO
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("MovimentacaoResponseDTO")
    class Response {

        private PecaInsumo peca;

        @BeforeEach
        void setUp() {
            peca = PecaInsumo.builder()
                    .id(UUID.fromString("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb"))
                    .nome("Pastilha de freio")
                    .precoVenda(new BigDecimal("80.00"))
                    .precoCompra(new BigDecimal("50.00"))
                    .quantidadeEstoque(20)
                    .quantidadeMinima(5)
                    .build();
        }

        private MovimentacaoEstoque movimentacao(TipoMovimentacao tipo, String obs) {
            return MovimentacaoEstoque.builder()
                    .pecaInsumo(peca)
                    .tipoMovimentacao(tipo)
                    .quantidade(5)
                    .observacao(obs)
                    .criadoEm(java.time.LocalDateTime.now())
                    .build();
        }

        @Test
        @DisplayName("from() deve mapear todos os campos")
        void deveMapearTodosOsCampos() {
            var mov = movimentacao(TipoMovimentacao.ENTRADA, "Compra NF-001");
            var dto = MovimentacaoResponseDTO.from(mov);

            assertThat(dto.pecaInsumoId()).isEqualTo(peca.getId());
            assertThat(dto.pecaNome()).isEqualTo("Pastilha de freio");
            assertThat(dto.tipoMovimentacao()).isEqualTo(TipoMovimentacao.ENTRADA);
            assertThat(dto.quantidade()).isEqualTo(5);
            assertThat(dto.observacao()).isEqualTo("Compra NF-001");
            assertThat(dto.criadoEm()).isNotNull();
        }

        @Test
        @DisplayName("from() deve preservar observacao nula")
        void devePreservarObservacaoNula() {
            var mov = movimentacao(TipoMovimentacao.VENDA, null);
            var dto = MovimentacaoResponseDTO.from(mov);

            assertThat(dto.observacao()).isNull();
        }

        @Test
        @DisplayName("from() deve refletir id nulo antes de persistir")
        void deveRefletirIdNuloAntesDePeristir() {
            var mov = movimentacao(TipoMovimentacao.RESERVA, null);
            var dto = MovimentacaoResponseDTO.from(mov);

            assertThat(dto.id()).isNull();
        }

        @Test
        @DisplayName("from() deve mapear criadoEm corretamente")
        void deveMapearCriadoEm() {
            var mov = movimentacao(TipoMovimentacao.AJUSTE, null);
            var antes = LocalDateTime.now().minusSeconds(1);
            var dto = MovimentacaoResponseDTO.from(mov);
            var depois = LocalDateTime.now().plusSeconds(1);

            assertThat(dto.criadoEm()).isAfter(antes).isBefore(depois);
        }

        @Test
        @DisplayName("from() deve mapear corretamente para cada tipo de movimentação")
        void deveMapearParaCadaTipo() {
            for (var tipo : TipoMovimentacao.values()) {
                var mov = movimentacao(tipo, null);
                var dto = MovimentacaoResponseDTO.from(mov);

                assertThat(dto.tipoMovimentacao()).isEqualTo(tipo);
            }
        }
    }
}
